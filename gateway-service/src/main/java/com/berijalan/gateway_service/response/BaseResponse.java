package com.berijalan.gateway_service.response;

import lombok.Data;

import java.util.UUID;

@Data
public class BaseResponse<T> {
    private UUID reqId = UUID.randomUUID();
    private String status;
    private String message;
    private T data;

    public static <T> BaseResponse<T> success(T data, String message) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus("T");
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus("T");
        response.setMessage("Success");
        response.setData(data);
        return response;
    }

    public static <T> BaseResponse<T> failed(T data, String message) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus("F");
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> BaseResponse<T> failed(T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus("F");
        response.setMessage("Failed");
        response.setData(data);
        return response;
    }
}
