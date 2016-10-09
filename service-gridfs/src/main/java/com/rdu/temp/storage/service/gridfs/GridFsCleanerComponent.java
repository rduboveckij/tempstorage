package com.rdu.temp.storage.service.gridfs;

import com.rdu.temp.storage.api.TempFileCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.rdu.temp.storage.service.gridfs.GridFsProperties.PROPERTIES_PREFIX;

/**
 * @author rdu
 * @since 09.10.2016
 */
@Component
@Slf4j
public class GridFsCleanerComponent implements TempFileCleaner {
    private final GridFsOperations gridFsOperations;

    @Autowired
    public GridFsCleanerComponent(GridFsOperations gridFsOperations) {
        this.gridFsOperations = gridFsOperations;
    }

    @Override
    @Scheduled(
            initialDelayString = "${" + PROPERTIES_PREFIX + ".cleaner-start-delay}",
            fixedRateString = "${" + PROPERTIES_PREFIX + ".cleaner-repeat-interval}"
    )
    public void clean() {
        log.info("The temp file grid fs cleaner was started.");
        // be careful with timezone on server and mongodb
        gridFsOperations.delete(Query.query(
                GridFsCriteria.whereMetaData("expiredAt").lte(new Date())
        ));
        log.info("The temp file grid fs cleaner was finished.");
    }
}
