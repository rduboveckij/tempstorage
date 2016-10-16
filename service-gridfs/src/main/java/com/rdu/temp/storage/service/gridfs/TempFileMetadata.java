package com.rdu.temp.storage.service.gridfs;

import com.rdu.temp.storage.api.TempFileComment;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.Date;

/**
 * @author rdu
 * @since 08.10.2016
 */
@Data
@Builder
public class TempFileMetadata {
    private String description;
    private Collection<TempFileComment> comments;
    private Date expiredAt;
}
