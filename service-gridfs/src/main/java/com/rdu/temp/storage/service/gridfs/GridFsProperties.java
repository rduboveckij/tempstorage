package com.rdu.temp.storage.service.gridfs;

import com.rdu.temp.storage.api.TempFileService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.rdu.temp.storage.service.gridfs.GridFsProperties.PROPERTIES_PREFIX;

/**
 * @author rdu
 * @since 09.10.2016
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = PROPERTIES_PREFIX)
public class GridFsProperties {
    public static final String PROPERTIES_PREFIX = TempFileService.PROPERTIES_PREFIX + "grid.fs";

    private long cleanerStartDelay;
    private long cleanerRepeatInterval;
    private long cleanerTimeToLive;
    private String databaseHost;
    private String databaseName;
}
