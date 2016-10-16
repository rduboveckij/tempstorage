package com.rdu.temp.storage.service.gridfs;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.rdu.temp.storage.api.TempFileComment;
import com.rdu.temp.storage.api.TempFileDescriptor;
import com.rdu.temp.storage.api.TempFileService;
import com.rdu.temp.storage.api.TempFileUpload;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * @author rdu
 * @since 08.10.2016
 */
@RunWith(MockitoJUnitRunner.class)
public class GridFsTempFileServiceTest {
    private static final long FILE_SIZE = 123L;
    private static final String FILE_ID = UUID.randomUUID().toString();
    private static final String FILE_NAME = "test_file.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final Date UPLOADED_DATE = new Date();
    private static final String FILE_DESCRIPTION = "Description 1";

    private TempFileService tempFileService;
    @Mock
    private GridFsProperties gridFsProperties;
    @Mock
    private GridFsOperations gridFsOperations;
    @Mock
    private MappingMongoConverter mappingMongoConverter;
    @Mock
    private GridFSDBFile file;

    private TempFileDescriptor tempFileDescriptor;
    private TempFileMetadata metaData;


    @Before
    public void setUp() throws Exception {
        tempFileService = new GridFsTempFileService(gridFsProperties, gridFsOperations, mappingMongoConverter);
        when(file.getId()).thenReturn(FILE_ID);
        when(file.getFilename()).thenReturn(FILE_NAME);
        when(file.getContentType()).thenReturn(CONTENT_TYPE);
        when(file.getUploadDate()).thenReturn(UPLOADED_DATE);
        when(file.getLength()).thenReturn(FILE_SIZE);

        tempFileDescriptor = TempFileDescriptor.builder()
                .fileId(FILE_ID)
                .name(FILE_NAME)
                .contentType(CONTENT_TYPE)
                .uploaded(UPLOADED_DATE)
                .description(FILE_DESCRIPTION)
                .size(FILE_SIZE)
                .comments(Collections.emptyList())
                .build();

        metaData = TempFileMetadata.builder()
                .description(FILE_DESCRIPTION)
                .comments(Collections.emptyList())
                .build();

        when(mappingMongoConverter.read(Mockito.eq(TempFileMetadata.class), Mockito.any())).thenReturn(metaData);
    }

    @Test
    public void testFindByIdSuccess() throws Exception {
        when(gridFsOperations.findOne(Mockito.any())).thenReturn(file);

        TempFileDescriptor descriptor = tempFileService.findById(FILE_ID);
        Assert.assertEquals(tempFileDescriptor, descriptor);
    }

    @Test(expected = GridFsTempFileNotFoundException.class)
    public void testFindByIdNotFound() throws Exception {
        when(gridFsOperations.findOne(Mockito.any())).thenReturn(null);

        tempFileService.findById(FILE_ID);
    }

    @Test
    public void testFindAllSuccess() throws Exception {
        when(gridFsOperations.find(Mockito.any())).thenReturn(Collections.singletonList(file));

        Optional<TempFileDescriptor> optional = tempFileService.findAll().stream().findFirst();
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(tempFileDescriptor, optional.get());
    }

    @Test
    public void testDownloadSuccess() throws Exception {
        when(file.getInputStream()).thenReturn(new ClassPathResource("upload.file.test.txt", getClass()).getInputStream());

        when(gridFsOperations.findOne(Mockito.any())).thenReturn(file);

        Resource resource = tempFileService.download(FILE_ID);
        Assert.assertEquals(FILE_NAME, resource.getFilename());
    }

    @Test(expected = GridFsTempFileNotFoundException.class)
    public void testDownloadNotFound() throws Exception {
        when(gridFsOperations.findOne(Mockito.any())).thenReturn(null);

        tempFileService.download(FILE_ID);
    }

    @Test
    public void testUploadSuccess() throws Exception {
        InputStream inputStream = new ClassPathResource("upload.file.test.txt", getClass()).getInputStream();

        when(gridFsProperties.getCleanerTimeToLive()).thenReturn(60L);
        when(gridFsOperations.store(eq(inputStream), eq(FILE_NAME), eq(CONTENT_TYPE), any(TempFileMetadata.class))).thenReturn(file);

        TempFileDescriptor descriptor = tempFileService.upload(TempFileUpload.builder()
                .description(FILE_DESCRIPTION)
                .name(FILE_NAME)
                .contentType(CONTENT_TYPE)
                .stream(inputStream)
                .build());

        Assert.assertEquals(tempFileDescriptor, descriptor);
    }

    @Test(expected = GridFsStoringTempFileException.class)
    public void testUploadNegative() throws Exception {
        InputStream inputStream = new ClassPathResource("upload.file.test.txt", getClass()).getInputStream();

        when(gridFsProperties.getCleanerTimeToLive()).thenReturn(60L);
        when(gridFsOperations.store(eq(inputStream), eq(FILE_NAME), eq(CONTENT_TYPE), any(TempFileMetadata.class))).thenThrow(IOException.class);

        tempFileService.upload(TempFileUpload.builder()
                .description(FILE_DESCRIPTION)
                .name(FILE_NAME)
                .contentType(CONTENT_TYPE)
                .stream(inputStream)
                .build());
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        when(gridFsOperations.findOne(Mockito.any())).thenReturn(file);

        tempFileDescriptor.setComments(Collections.singleton(TempFileComment.builder()
                .created(new Date())
                .text("Text 1")
                .updated(new Date())
                .build()));

        TempFileDescriptor descriptor = tempFileService.update(tempFileDescriptor);

        verify(file, times(1)).save();
        verify(mappingMongoConverter, times(1)).write(metaData, new BasicDBObject());

        Assert.assertEquals(descriptor, tempFileDescriptor);
    }

    @Test(expected = GridFsTempFileNotFoundException.class)
    public void testUpdateNotFound() throws Exception {
        when(gridFsOperations.findOne(Mockito.any())).thenReturn(null);

        tempFileService.update(tempFileDescriptor);
    }
}
