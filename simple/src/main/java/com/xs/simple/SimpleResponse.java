package com.xs.simple;

import com.xs.simplehttp.api.Response;
import com.xs.simplehttp.core.HttpException;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * simple response
 *
 * Created by xs code on 2019/3/15.
 */

public class SimpleResponse<T> {


    private Response response;
    private T t;

    public SimpleResponse(Response response,T t) {
        this.response = response;
        this.t = t;
    }

    public int getCode() {
        return response.getCode();
    }

    public T body() {
        return t;
    }

    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    public Map<String, List<String>> getHeaderFields() {
        return response.getHeaderFields();
    }

    public String getString() {
        return response.getResponseString();
    }

    public InputStream getStream () {
        return response.getResponseInputStream();
    }

    public File getFile(String destFileDir, String destFileName){
        return getFile(destFileDir,destFileName,false);
    }

    public File getFile(String destFileDir, String destFileName, boolean append) {
        return response.getResponseFile(destFileDir,destFileName,append);
    }

    public HttpException getError() {
        return response.getError();
    }

    public Map<String,String> getCookies() {
        return response.getCookies();
    }

}
