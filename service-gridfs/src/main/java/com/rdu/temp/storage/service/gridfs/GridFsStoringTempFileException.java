package com.rdu.temp.storage.service.gridfs;

import com.rdu.temp.storage.api.TempFileServiceException;
import com.rdu.temp.storage.api.TempFileUpload;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author rdu
 * @since 08.10.2016
 */
public class GridFsStoringTempFileException extends TempFileServiceException {
    public GridFsStoringTempFileException(TempFileUpload file, IOException exception) {
        super(HttpStatus.SERVICE_UNAVAILABLE, String.format("Storing file [%s] was failed", file.getName()), exception);
    }
}
