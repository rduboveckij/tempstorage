package com.rdu.temp.storage.api;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author rdu
 * @since 16.10.2016
 */
@Data
@Builder
public class TempFileComment {
    private String text;
    private Date created;
    private Date updated;

    @Tolerate
    public TempFileComment() {
    }
}
