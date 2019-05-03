package com.xs.simple.impl;

import com.xs.simple.Call;
import com.xs.simple.CallAdapter;
import com.xs.simple.util.TypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Call Adapter
 *
 * Created by xs code on 2019/3/15.
 */

public class DefaultCallAdapter extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get(final Type retrunType) {

        if (TypeUtil.typeClass(retrunType,Call.class)) {
            return new CallAdapter<Object, Call<?>>() {
                @Override
                public Type getResponseType() {
                    if (retrunType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) retrunType;
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        Type paramType = actualTypeArguments[0];
                        if (paramType instanceof WildcardType) {
                            return ((WildcardType) paramType).getUpperBounds()[0];
                        }
                        return paramType;
                    }
                    throw new IllegalArgumentException("must be return " + retrunType.getClass().getName() + "<?>");
                }

                @Override
                public Call<?> adapter(Call<Object> call) {
                    return call;
                }
            };
        }
        return null;
    }
}
