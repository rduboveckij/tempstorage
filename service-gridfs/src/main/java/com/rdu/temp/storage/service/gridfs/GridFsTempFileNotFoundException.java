package com.rdu.temp.storage.service.gridfs;

import com.rdu.temp.storage.api.TempFileServiceException;
import org.springframework.http.HttpStatus;

/**
 * @author rdu
 * @since 08.10.2016
 */
public class GridFsTempFileNotFoundException extends TempFileServiceException {
    public GridFsTempFileNotFoundException(String fileId) {
        super(HttpStatus.NOT_FOUND, String.format("The file [%s] was not found", fileId));
    }
}
