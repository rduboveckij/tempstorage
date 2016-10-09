package com.rdu.temp.storage.api;

import org.springframework.core.io.Resource;


/**
 * @author rdu
 * @since 08.10.2016
 */
public interface TempFileService {
    String PROPERTIES_PREFIX = "temp.file.";

    TempFileDescriptor findById(String fileId);

    TempFileDescriptor upload(TempFileUpload file);

    Resource download(String fileId);
}
