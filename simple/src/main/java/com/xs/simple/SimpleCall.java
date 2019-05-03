package com.xs.simple;

import com.xs.simplehttp.api.Request;
import com.xs.simplehttp.api.Response;
import com.xs.simplehttp.core.HttpException;

/**
 * simple call
 *
 * Created by xs code on 2019/3/15.
 */

public class SimpleCall<R> implements Call<R> {

    private HandleMethod<R,?> handleMethod;
    private Object[] args;
    private com.xs.simplehttp.api.Call httpCall;
    private Callback<R> callback;

    public SimpleCall(HandleMethod handleMethod, Object[] args) {
        this.handleMethod = handleMethod;
        this.args = args;
    }


    @Override
    public SimpleResponse<R> execute() {
        if (httpCall == null) {
            httpCall = createCall();
        }
        Response response = httpCall.execute();
        R t = handleMethod.toResponse(response);
        return new SimpleResponse<R>(response,t);
    }

    @Override
    public void enqueue(Callback<R> callback) {
        this.callback = callback;
        if (httpCall == null) {
            httpCall = createCall();
        }
        httpCall.enqueue(new com.xs.simplehttp.api.Callback() {
            @Override
            public void onResponse(Response response) {
                R t = handleMethod.toResponse(response);
                onSimpleResponse(new SimpleResponse<R>(response,t));
            }

            @Override
            public void onFailure(HttpException e) {
                onSimpleFailure(e);
            }
        });
    }

    private void onSimpleResponse(SimpleResponse<R> response) {
        if (callback != null) {
            callback.onResponse(response);
        }
    }

    private void onSimpleFailure(HttpException e) {
        if (callback != null) {
            callback.onFailure(e);
        }
    }

    @Override
    public boolean isExecuted() {
        if (httpCall == null) {
            return false;
        }
        return httpCall.isExecuted();
    }

    @Override
    public void cancel() {
        if (httpCall == null) {
            return;
        }
        callback = null;
        httpCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        if (httpCall == null) {
            return false;
        }
        return httpCall.isCanceled();
    }

    /**
     * create httpCall
     * @return
     */
    private com.xs.simplehttp.api.Call createCall() {
        Request request = handleMethod.toRequest(args);
        return handleMethod.simpleHttp.newCall(request);
    }
}
