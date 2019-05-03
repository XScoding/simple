package com.xs.simple;


import com.xs.simplehttp.core.HttpException;

/**
 * callback
 *
 * Created by xs code on 2019/3/15.
 */

public interface Callback<R> {

    /**
     * success
     * @param response
     */
    void onResponse(SimpleResponse<R> response);

    /**
     * fail
     * @param e
     */
    void onFailure(HttpException e);
}
