package com.rdu.temp.storage.service.gridfs;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.rdu.temp.storage.api.TempFileDescriptor;
import com.rdu.temp.storage.api.TempFileService;
import com.rdu.temp.storage.api.TempFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author rdu
 * @since 08.10.2016
 */
@Service
public class GridFsTempFileService implements TempFileService {
    private static final String WHERE_ID = "_id";

    private final GridFsProperties gridFsProperties;
    private final GridFsOperations gridFsOperations;
    private final MongoConverter mappingMongoConverter;

    @Autowired
    public GridFsTempFileService(GridFsProperties gridFsProperties,
                                 GridFsOperations gridFsOperations,
                                 MappingMongoConverter mappingMongoConverter) {
        this.gridFsProperties = gridFsProperties;
        this.gridFsOperations = gridFsOperations;
        this.mappingMongoConverter = mappingMongoConverter;
    }

    @Override
    public TempFileDescriptor findById(String fileId) {
        return convert(findFile(fileId));
    }

    @Override
    public Collection<TempFileDescriptor> findAll() {
        return gridFsOperations.find(new Query())
                .parallelStream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private GridFSDBFile findFile(String fileId) {
        GridFSDBFile file = gridFsOperations.findOne(
                Query.query(Criteria.where(WHERE_ID).is(fileId))
        );

        if (file == null) {
            throw new GridFsTempFileNotFoundException(fileId);
        }

        return file;
    }

    @Override
    public TempFileDescriptor upload(TempFileUpload file) {
        return convert(uploadSafe(file));
    }

    private TempFileDescriptor convert(GridFSFile file) {
        TempFileMetadata metaData = convert(file.getMetaData());
        return TempFileDescriptor.builder()
                .fileId(String.valueOf(file.getId()))
                .name(file.getFilename())
                .contentType(file.getContentType())
                .uploaded(file.getUploadDate())
                .description(metaData.getDescription())
                .size(file.getLength())
                .comments(metaData.getComments())
                .build();
    }

    private TempFileMetadata convert(DBObject metaData) {
        return mappingMongoConverter.read(TempFileMetadata.class, metaData);
    }

    private DBObject convert(TempFileMetadata metaData) {
        BasicDBObject dbObject = new BasicDBObject();
        mappingMongoConverter.write(metaData, dbObject);
        return dbObject;
    }

    private GridFSFile uploadSafe(TempFileUpload file) {
        TempFileMetadata metadata = TempFileMetadata.builder()
                .description(file.getDescription())
                .comments(Collections.emptyList())
                .expiredAt(createExpiredAt())
                .build();

        try (InputStream stream = file.getStream()) {
            return gridFsOperations.store(stream, file.getName(), file.getContentType(), metadata);
        } catch (IOException exception) {
            throw new GridFsStoringTempFileException(file, exception);
        }
    }

    private Date createExpiredAt() {
        return new Date(new Date().getTime() + gridFsProperties.getCleanerTimeToLive());
    }

    @Override
    public Resource download(String fileId) {
        return new GridFsResource(findFile(fileId));
    }

    @Override
    public TempFileDescriptor update(TempFileDescriptor file) {
        GridFSDBFile oldFile = findFile(file.getFileId());

        TempFileMetadata metadata = convert(oldFile.getMetaData());
        metadata.setComments(file.getComments());
        metadata.setDescription(file.getDescription());

        oldFile.setMetaData(convert(metadata));
        oldFile.save();
        return file;
    }
}
