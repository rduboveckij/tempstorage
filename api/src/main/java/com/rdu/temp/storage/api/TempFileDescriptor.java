package com.rdu.temp.storage.api;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Collection;
import java.util.Date;

/**
 * @author rdu
 * @since 08.10.2016
 */
@Data
@Builder
public class TempFileDescriptor {
    private String id;
    private String name;
    private String contentType;
    private long size;
    private Collection<String> comments;
    private Date uploaded;

    @Tolerate
    public TempFileDescriptor() {
    }
}
