package com.clj.utils;

import lombok.Data;

import java.util.List;
@Data
public class Result {
    private String code;
    private String message;
    private Object data;
    private List<Object> list;
    public static Result ok(){
        Result result = new Result();
        result.setCode("200");
        return result;
    }
    public static Result ok(Object data){
        Result result = new Result();
        result.setCode("200");
        result.setData(data);
        return result;
    }
    public static Result ok(List<Object> list){
        Result result = new Result();
        result.setCode("200");
        result.setList(list);
        return result;
    }

    public static Result error(String message){
        Result result = new Result();
        result.setCode("500");
        result.setMessage(message);
        return result;
    }
}
