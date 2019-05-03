package com.xs.simple;


/**
 * Call
 *
 * Created by xs code on 2019/3/15.
 */

public interface Call<R> {

    /**
     * execute
     * @return
     */
    SimpleResponse<R> execute();

    /**
     * enqueue
     * @param callback
     */
    void enqueue(Callback<R> callback);

    /**
     * execute state
     * @return
     */
    boolean isExecuted();

    /**
     * cancel
     */
    void cancel();

    /**
     * cancel state
     * @return
     */
    boolean isCanceled();

}
