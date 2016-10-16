package com.rdu.temp.storage.api;

import org.springframework.core.io.Resource;

import java.util.Collection;


/**
 * @author rdu
 * @since 08.10.2016
 */
public interface TempFileService {
    String PROPERTIES_PREFIX = "temp.file.";

    TempFileDescriptor findById(String fileId);

    Collection<TempFileDescriptor> findAll();

    TempFileDescriptor upload(TempFileUpload file);

    Resource download(String fileId);

    TempFileDescriptor update(TempFileDescriptor file);
}
