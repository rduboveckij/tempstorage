package com.rdu.temp.storage.rest;

import com.rdu.temp.storage.api.TempFileDescriptor;
import com.rdu.temp.storage.api.TempFileService;
import com.rdu.temp.storage.api.TempFileServiceException;
import com.rdu.temp.storage.api.TempFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author rdu
 * @since 08.10.2016
 */
@RestController
public class TempFileController {
    private final TempFileService tempFileService;

    @Autowired
    public TempFileController(TempFileService tempFileService) {
        this.tempFileService = tempFileService;
    }

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<TempFileDescriptor> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new UploadingFileBodyEmptyException(file);
        }

        return ResponseEntity.ok().body(uploadSafe(file));
    }

    private TempFileDescriptor uploadSafe(MultipartFile file) {
        try {
            return tempFileService.upload(TempFileUpload.builder()
                    .contentType(file.getContentType())
                    .name(file.getOriginalFilename())
                    .stream(file.getInputStream())
                    .build());
        } catch (IOException e) {
            throw new TemporaryUploadingStorageFails(file, e);
        }
    }

    @GetMapping("/{fileId}")
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable String fileId) {
        Resource file = tempFileService.download(fileId);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getFilename()))
                .body(file);
    }

    @ExceptionHandler(TempFileServiceException.class)
    public ResponseEntity handleUploadingFileBodyEmptyException(TempFileServiceException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(RestErrorMessage.of(exception.getMessage()));
    }
}