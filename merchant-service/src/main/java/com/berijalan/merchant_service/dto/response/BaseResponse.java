package com.berijalan.merchant_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse <T> {
    private String message;
    private T data;

    public static <T> BaseResponse<T> success(String msg, T data){
        BaseResponse<T> response = new BaseResponse<>();
        response.setMessage(msg);
        response.setData(data);
        return response;
    }


    public static <T> BaseResponse<T> failed(String msg){
        BaseResponse<T> response = new BaseResponse<>();
        response.setMessage(msg);
        return response;
    }
}