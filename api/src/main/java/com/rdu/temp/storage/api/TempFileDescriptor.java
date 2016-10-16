package com.rdu.temp.storage.api;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.hateoas.ResourceSupport;

import java.util.Collection;
import java.util.Date;

/**
 * @author rdu
 * @since 08.10.2016
 */
@Data
@Builder
public class TempFileDescriptor extends ResourceSupport {
    private String fileId;
    private String name;
    private String contentType;
    private String description;
    private long size;
    private Collection<TempFileComment> comments;
    private Date uploaded;

    @Tolerate
    public TempFileDescriptor() {
    }
}
