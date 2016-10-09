package com.rdu.temp.storage.rest;

import com.rdu.temp.storage.api.TempFileServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author rdu
 * @since 08.10.2016
 */
public class TemporaryUploadingStorageFails extends TempFileServiceException {
    public TemporaryUploadingStorageFails(MultipartFile file, IOException exception) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format("The temporary uploading storage fails, while file [%s] was uploading", file.getOriginalFilename()), exception);
    }
}
