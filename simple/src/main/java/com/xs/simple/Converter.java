package com.xs.simple;



import com.xs.simplehttp.api.Response;

import java.lang.reflect.Type;

/**
 * converter
 *
 * Created by xs code on 2019/3/12.
 */

public interface Converter<T,R> {

    /**
     * T convert R
     * @param t
     * @return
     */
    R convert(T t);

    abstract class Factory {

        /**
         * response convert ?(type)
         * @param type
         * @return
         */
        public abstract Converter<Response, ?> responseConverter(Type type);


        /**
         * @Body ? convert String(Json)
         * @param type
         * @return
         */
        public abstract Converter<?, String> requestConverter(Type type);

    }
}
