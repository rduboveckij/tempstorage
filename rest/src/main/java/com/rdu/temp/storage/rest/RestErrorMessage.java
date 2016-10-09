package com.rdu.temp.storage.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rdu
 * @since 08.10.2016
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestErrorMessage {
    private String message;

    public static RestErrorMessage of(String message) {
        return new RestErrorMessage(message);
    }
}
