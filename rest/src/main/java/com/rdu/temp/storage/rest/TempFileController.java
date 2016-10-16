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
import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author rdu
 * @since 08.10.2016
 */
@RestController
public class TempFileController {
    public static final String ENDPOINT_URL = "/files";

    private final TempFileService tempFileService;

    @Autowired
    public TempFileController(TempFileService tempFileService) {
        this.tempFileService = tempFileService;
    }

    // @GetMapping(ENDPOINT_URL + "/{fileId}") hateoas is not supported
    @RequestMapping(method = RequestMethod.GET, value = ENDPOINT_URL + "/{fileId}")
    @ResponseBody
    public ResponseEntity<TempFileDescriptor> findById(@PathVariable String fileId) {
        return ResponseEntity
                .ok()
                .body(addFileMetaData(tempFileService.findById(fileId)));
    }

    @GetMapping(ENDPOINT_URL)
    @ResponseBody
    public ResponseEntity<Collection<TempFileDescriptor>> findAll() {
        return ResponseEntity
                .ok()
                .body(
                        tempFileService.findAll()
                                .stream()
                                .map(this::addFileMetaData)
                                .collect(Collectors.toList())
                );
    }

    @PostMapping(ENDPOINT_URL)
    @ResponseBody
    public ResponseEntity<TempFileDescriptor> upload(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("description") String description) {
        if (file.isEmpty()) {
            throw new UploadingFileBodyEmptyException(file);
        }

        return ResponseEntity.ok().body(addFileMetaData(uploadSafe(file, description)));
    }

    private TempFileDescriptor uploadSafe(MultipartFile file, String description) {
        try {
            return tempFileService.upload(TempFileUpload.builder()
                    .contentType(file.getContentType())
                    .name(file.getOriginalFilename())
                    .description(description)
                    .stream(file.getInputStream())
                    .build());
        } catch (IOException e) {
            throw new TemporaryUploadingStorageFails(file, e);
        }
    }
    // @GetMapping(ENDPOINT_URL + "/{fileId}/download")(ENDPOINT_URL + "/{fileId}") hateoas is not supported
    @RequestMapping(method = RequestMethod.GET, value = ENDPOINT_URL + "/{fileId}/download")
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable String fileId) {
        Resource file = tempFileService.download(fileId);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getFilename()))
                .body(file);
    }

    @PutMapping(ENDPOINT_URL + "/{fileId}")
    @ResponseBody
    public ResponseEntity<TempFileDescriptor> updateFile(@PathVariable String fileId, @RequestBody TempFileDescriptor file) {
        file.setFileId(fileId);
        return ResponseEntity
                .ok()
                .body(addFileMetaData(tempFileService.update(file)));
    }

    @ExceptionHandler(TempFileServiceException.class)
    public ResponseEntity handleUploadingFileBodyEmptyException(TempFileServiceException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(RestErrorMessage.of(exception.getMessage()));
    }

    private TempFileDescriptor addFileMetaData(TempFileDescriptor file) {
        file.add(linkTo(methodOn(TempFileController.class).findById(file.getFileId())).withSelfRel());
        file.add(linkTo(methodOn(TempFileController.class).download(file.getFileId())).withRel("download"));
        return file;
    }
}