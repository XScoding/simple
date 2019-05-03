package com.xs.simple;

import java.lang.reflect.Type;

/**
 * Created by xs code on 2019/3/15.
 */

public interface CallAdapter<R,C> {

    /**
     * get response type
     * @return
     */
    Type getResponseType();

    /**
     * call convert C
     * @param call
     * @return
     */
    C adapter(Call<R> call);

    /**
     * factory
     */
    abstract class Factory {

        public abstract CallAdapter<?,?> get(Type retrunType);
    }
}
