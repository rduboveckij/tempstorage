package com.rdu.temp.storage.rest;

import com.rdu.temp.storage.api.TempFileServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author rdu
 * @since 08.10.2016
 */
public class UploadingFileBodyEmptyException extends TempFileServiceException {
    public UploadingFileBodyEmptyException(MultipartFile file) {
        super(HttpStatus.BAD_REQUEST, String.format("Uploading file [%s] body is empty", file.getOriginalFilename()));
    }
}
