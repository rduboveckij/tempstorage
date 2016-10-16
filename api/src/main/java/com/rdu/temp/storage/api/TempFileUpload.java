package com.rdu.temp.storage.api;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

/**
 * @author rdu
 * @since 08.10.2016
 */
@Data
@Builder
public class TempFileUpload {
    String name;
    String contentType;
    String description;
    InputStream stream;
}
