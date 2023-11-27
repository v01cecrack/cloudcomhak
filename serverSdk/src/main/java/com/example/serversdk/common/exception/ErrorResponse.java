package com.example.serversdk.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private String message;
    private Date timestamp;
}
