package com.ralise.workforcemgmt.model.response;

import lombok.Data;
import lombok.Getter;

@Getter
public enum StatusCode {
    SUCCESS(200, "Success"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Resource Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");


    private final int code;
    private final String message;


    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

