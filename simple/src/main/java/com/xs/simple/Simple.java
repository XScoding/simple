package com.xs.simple;

import com.xs.simplehttp.api.SimpleHttp;
import com.xs.simple.impl.DefaultCallAdapter;
import com.xs.simple.impl.DefaultConverterFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xs code on 2019/3/11.
 */

public class Simple {

    /**
     * method map
     */
    private Map<Method,HandleMethod> methods = new HashMap<>();

    /**
     * simpleHttp
     */
    private SimpleHttp simpleHttp;

    /**
     * CallAdapter Factories
     */
    private List<CallAdapter.Factory> callAdapterFactories;

    /**
     * Converter Factories
     */
    private List<Converter.Factory> converterFactories;

    /**
     * base url
     */
    private String baseUrl;

    public Simple(SimpleHttp simpleHttp, List<CallAdapter.Factory> callAdapters, List<Converter.Factory> converterFactories, String baseUrl) {
        this.simpleHttp = simpleHttp;
        this.callAdapterFactories = callAdapters;
        this.converterFactories = converterFactories;
        this.baseUrl = baseUrl;
    }

    public SimpleHttp getSimpleHttp() {
        return simpleHttp;
    }

    public List<CallAdapter.Factory> getCallAdapterFactories() {
        return callAdapterFactories;
    }

    public List<Converter.Factory> getConverterFactories() {
        return converterFactories;
    }

    public String getBaseUrl() {
        return baseUrl;
    }



    public static class Builder{

        private SimpleHttp simpleHttp;

        private List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();

        private List<Converter.Factory> converterFactories = new ArrayList<>();

        private String baseUrl;

        public Builder(SimpleHttp simpleHttp, String baseUrl) {
            this.simpleHttp = simpleHttp;
            this.baseUrl = baseUrl;
            callAdapterFactories.add(new DefaultCallAdapter());
            converterFactories.add(new DefaultConverterFactory());
        }

        /**
         * add CallAdapterFactory
         * @param callAdapterFactory
         * @return
         */
        public Builder addCallAdapter(CallAdapter.Factory callAdapterFactory) {
            callAdapterFactories.add(callAdapterFactory);
            return this;
        }

        /**
         * add ConverterFactory
         * @param converterFactory
         * @return
         */
        public Builder addConverter(Converter.Factory converterFactory) {
            converterFactories.add(converterFactory);
            return this;
        }

        public Simple build() {
            return new Simple(simpleHttp,callAdapterFactories,converterFactories,baseUrl);
        }
    }

    /**
     * create service
     *
     * @param service
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("service is not interface");
        }
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        HandleMethod<Object,Object> handleMethod = getHandleMethod(method);
                        SimpleCall<Object> simpleCall = new SimpleCall(handleMethod,args);
                        return handleMethod.callAdapter.adapter(simpleCall);
                    }
                });

    }

    /**
     * get HandleMethod
     *
     * @param method
     * @return
     */
    private HandleMethod getHandleMethod(Method method) {
        HandleMethod handleMethod = methods.get(method);
        if (handleMethod == null) {
            HandleMethod.Builder builder = new HandleMethod.Builder(method, this);
            handleMethod = builder.build();
            methods.put(method,handleMethod);
        }
        return handleMethod;
    }
}
