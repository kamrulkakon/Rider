package com.example.Rider.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response<T> {

    private String statusCode;
    private boolean isSuccess;
    private String message;
    private T data;
}
