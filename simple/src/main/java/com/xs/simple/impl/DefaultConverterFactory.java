package com.xs.simple.impl;


import com.xs.simple.Converter;
import com.xs.simple.util.TypeUtil;
import com.xs.simplehttp.api.Response;

import java.lang.reflect.Type;

/**
 * String converter
 *
 * Created by xs code on 2019/3/15.
 */

public class DefaultConverterFactory extends Converter.Factory {


    @Override
    public Converter<Response, ?> responseConverter(Type type) {
        if (TypeUtil.typeClass(type,String.class)) {
            return new Converter<Response, String>() {
                @Override
                public String convert(Response response) {
                    return response.getResponseString();
                }
            };
        }
        if (TypeUtil.typeClass(type,Response.class)) {
            return new Converter<Response, Response>() {
                @Override
                public Response convert(Response response) {
                    return response;
                }
            };
        }
        return null;
    }

    /**
     * @Body jsonString to String
     * @param type
     * @return
     */
    @Override
    public Converter<?, String> requestConverter(Type type) {
        if (TypeUtil.typeClass(type,String.class)) {
            return new Converter<String, String>() {
                @Override
                public String convert(String s) {
                    return s;
                }
            };
        }

        return null;
    }

}
