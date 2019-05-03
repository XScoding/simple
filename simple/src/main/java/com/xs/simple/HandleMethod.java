package com.xs.simple;

import com.xs.simplehttp.api.Multiparts;
import com.xs.simplehttp.api.ProgressListener;
import com.xs.simplehttp.api.Request;
import com.xs.simplehttp.api.Response;
import com.xs.simplehttp.api.SimpleHttp;
import com.xs.simplehttp.core.MethodType;
import com.xs.simplehttp.core.PostType;
import com.xs.simple.annotation.Field;
import com.xs.simple.annotation.FieldMap;
import com.xs.simple.annotation.FormPost;
import com.xs.simple.annotation.GET;
import com.xs.simple.annotation.Header;
import com.xs.simple.annotation.HeaderMap;
import com.xs.simple.annotation.Headers;
import com.xs.simple.annotation.Json;
import com.xs.simple.annotation.JsonPost;
import com.xs.simple.annotation.Multipart;
import com.xs.simple.annotation.POST;
import com.xs.simple.annotation.Part;
import com.xs.simple.annotation.PartMap;
import com.xs.simple.annotation.Path;
import com.xs.simple.annotation.ProgressDown;
import com.xs.simple.annotation.ProgressUp;
import com.xs.simple.annotation.Query;
import com.xs.simple.annotation.QueryMap;
import com.xs.simple.util.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * handle method
 *
 * Created by xs code on 2019/3/14.
 */

public class HandleMethod<R,C> {

    private Simple simple;
    private SimpleRequest simpleRequest;
    SimpleHttp simpleHttp;
    CallAdapter<R,C> callAdapter;
    Converter<Response,R> responseTConverter;

    public HandleMethod(Builder builder) {
        this.simple = builder.simple;
        this.simpleHttp = builder.simpleHttp;
        this.callAdapter = builder.callAdapter;
        this.responseTConverter = builder.responseTConverter;
        this.simpleRequest = builder.simpleRequest;
    }

    /**
     * build request
     * @param args
     * @return
     */
    public Request toRequest(Object[] args) {
        List<SimpleRequest.Param> paramList = simpleRequest.getParamList();
        if (args != null) {
            if (args.length != paramList.size()) {
                throw new IllegalArgumentException("Each parameter should be annotated");
            }
            if (paramList.size() > 0) {
                for (int i = 0; i < args.length; i++) {
                    SimpleRequest.Param param = paramList.get(i);
                    switch (param.getParam()) {
                        case SimpleRequest.PARAM_FIELD:
                            if (TypeUtil.isBasicType(param.getType())) {
                               simpleRequest.addParam(param.getKey(),args[i]);
                            }
                            break;
                        case SimpleRequest.PARAM_FIELDMAP:
                            if (TypeUtil.typeMapWithStringObject(param.getType())) {
                                Map<String,Object> map = (Map<String, Object>) args[i];
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    simpleRequest.addParam(entry.getKey(),entry.getValue());
                                }
                            }
                            break;
                        case SimpleRequest.PARAM_QUERY:
                            if (TypeUtil.isBasicType(param.getType())) {
                                simpleRequest.addParam(param.getKey(),args[i]);
                            }
                            break;
                        case SimpleRequest.PARAM_QUERYMAP:
                            if (TypeUtil.typeMapWithStringObject(param.getType())) {
                                Map<String,Object> map = (Map<String, Object>) args[i];
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    simpleRequest.addParam(entry.getKey(),entry.getValue());
                                }
                            }
                            break;
                        case SimpleRequest.PARAM_PART:
                            if (TypeUtil.typeClass(param.getType(), Multiparts.class)) {
                                simpleRequest.setMultipart((Multiparts) args[i]);
                            }
                            break;
                        case SimpleRequest.PARAM_PARTMAP:
                            if (TypeUtil.typeMapWithStringObject(param.getType())) {
                                Map<String,Object> map = (Map<String, Object>) args[i];
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    simpleRequest.addParam(entry.getKey(),entry.getValue());
                                }
                            }
                            break;
                        case SimpleRequest.PARAM_HEADER:
                            if (TypeUtil.isBasicType(param.getType())) {
                                simpleRequest.addHeader(param.getKey(),args[i]);
                            }
                            break;
                        case SimpleRequest.PARAM_HEADERMAP:
                            if (TypeUtil.typeMapWithStringObject(param.getType())) {
                                Map<String,Object> map = (Map<String, Object>) args[i];
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    simpleRequest.addHeader(entry.getKey(),entry.getValue());
                                }
                            }
                            break;
                        case SimpleRequest.PARAM_PROGRESS_UP:
                            if (TypeUtil.typeClass(param.getType(), ProgressListener.class)) {
                                simpleRequest.setUploadlistener((ProgressListener) args[i]);
                            }
                            break;
                        case SimpleRequest.PARAM_PROGRESS_DOWN:
                            if (TypeUtil.typeClass(param.getType(), ProgressListener.class)) {
                                simpleRequest.setDownloadlistener((ProgressListener) args[i]);
                            }
                            break;
                        case SimpleRequest.PARAM_PATH:
                            if (TypeUtil.typeClass(param.getType(),String.class)) {
                                simpleRequest.addPath(param.getKey(), (String) args[i]);
                            }
                            break;
                        case SimpleRequest.PARAM_JSON:
                            simpleRequest.setJsonParams(getJsonString(args[i],param.getType()));
                            break;
                    }
                }
            }
        }
        return simpleRequest.buildRequest();
    }

    /**
     * object to json
     * @param o
     * @param type
     * @return
     */
    private String getJsonString(Object o, Type type) {
        List<Converter.Factory> converterFactories = simple.getConverterFactories();
        for (Converter.Factory converterFactory : converterFactories) {
            Converter converter = converterFactory.requestConverter(type);
            if (converter != null) {
                return (String) converter.convert(o);
            }
        }
        throw new IllegalArgumentException("@Json object not convent String");
    }

    /**
     * response to R
     * @param response
     * @return
     */
    public R toResponse(Response response) {
        return responseTConverter.convert(response);
    }


    public static class Builder<R,C> {

        private Method method;
        private Simple simple;
        private SimpleHttp simpleHttp;
        private SimpleRequest simpleRequest;

        private Type returnType;
        private Type responseType;
        private Annotation[] methodAnnotations;
        private Annotation[][] parameterAnnotations;
        private Type[] genericParameterTypes;

        private CallAdapter<R,C> callAdapter;
        private Converter<Response,R> responseTConverter;

        public Builder(Method method, Simple simple) {
            this.method = method;
            this.simple = simple;
            this.simpleRequest = new SimpleRequest();
            this.returnType = method.getGenericReturnType();
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotations = method.getParameterAnnotations();
            this.genericParameterTypes = method.getGenericParameterTypes();
        }

        public HandleMethod build() {
            this.callAdapter = createCallAdapter(returnType);
            this.responseType = callAdapter.getResponseType();
            this.responseTConverter = createResponseConverter(responseType);
            this.simpleHttp = getSimpleHttp();

            parseMethodAnnotations(simpleRequest,methodAnnotations);
            parseParameterAnnotations(simpleRequest,parameterAnnotations,genericParameterTypes);

            return new HandleMethod(this);
        }

        private void parseParameterAnnotations(SimpleRequest simpleRequest, Annotation[][] parameterAnnotations, Type[] genericParameterTypes) {
            if (parameterAnnotations == null || genericParameterTypes == null ||
                    parameterAnnotations.length != genericParameterTypes.length) {
                return;
            }
            if (parameterAnnotations != null && parameterAnnotations.length > 0) {
                for (int i = 0; i < genericParameterTypes.length; i++) {
                    Annotation annotation = parameterAnnotations[i][0];
                    if (annotation instanceof Field) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_FIELD,genericParameterTypes[i],((Field) annotation).value()));
                    } else if (annotation instanceof FieldMap) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_FIELDMAP,genericParameterTypes[i],null));
                    } else if (annotation instanceof Query) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_QUERY,genericParameterTypes[i],((Query) annotation).value()));
                    } else if (annotation instanceof QueryMap) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_QUERYMAP,genericParameterTypes[i],null));
                    } else if (annotation instanceof Part) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_PART,genericParameterTypes[i],null));
                    } else if (annotation instanceof PartMap) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_PARTMAP,genericParameterTypes[i],null));
                    } else if (annotation instanceof Header) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_HEADER,genericParameterTypes[i],((Header) annotation).value()));
                    } else if (annotation instanceof HeaderMap) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_HEADERMAP,genericParameterTypes[i],null));
                    } else if (annotation instanceof ProgressUp) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_PROGRESS_UP,genericParameterTypes[i],null));
                    } else if (annotation instanceof ProgressDown) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_PROGRESS_DOWN,genericParameterTypes[i],null));
                    }else if (annotation instanceof Path) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_PATH,genericParameterTypes[i],((Path) annotation).value()));
                    } else if (annotation instanceof Json) {
                        simpleRequest.addParam(new SimpleRequest.Param(SimpleRequest.PARAM_JSON,genericParameterTypes[i],null));
                    }
                }
            }
        }

        private void parseMethodAnnotations(SimpleRequest simpleRequest, Annotation[] methodAnnotations) {
            if (methodAnnotations != null && methodAnnotations.length > 0) {
                for (Annotation annotation : methodAnnotations) {
                    if (annotation instanceof GET) {
                        simpleRequest.setPath(((GET) annotation).value());
                        simpleRequest.setMethodType(MethodType.GET);
                    } else if (annotation instanceof POST) {
                        simpleRequest.setPath(((POST) annotation).value());
                        simpleRequest.setMethodType(MethodType.POST);
                    } else if (annotation instanceof Multipart) {
                        simpleRequest.setPath(((Multipart) annotation).value());
                        simpleRequest.setMethodType(MethodType.UPLOAD);
                    } else if (annotation instanceof FormPost) {
                        simpleRequest.setPostType(PostType.FORM);
                    } else if (annotation instanceof JsonPost) {
                        simpleRequest.setPostType(PostType.JSON);
                    } else if (annotation instanceof Headers) {
                        String[] value = ((Headers) annotation).value();
                        if (value != null && value.length > 0) {
                            for (String header : value) {
                                String[] split = header.split(":");
                                simpleRequest.addHeader(split[0],split[1]);
                            }
                        }
                    }
                }
            }
        }

        private SimpleHttp getSimpleHttp() {
            SimpleHttp simpleHttp = simple.getSimpleHttp();
            if (simpleHttp == null) {
                SimpleHttp.Builder builder = new SimpleHttp.Builder();
                simpleHttp = builder.build();
            }
            simpleHttp.setBaseUrl(simple.getBaseUrl());
            return simpleHttp;
        }

        private CallAdapter<R,C> createCallAdapter(Type type) {
            List<CallAdapter.Factory> callAdapterFactories = simple.getCallAdapterFactories();
            for (CallAdapter.Factory callAdapterFactory : callAdapterFactories) {
                CallAdapter<?, ?> callAdapter = callAdapterFactory.get(type);
                if (callAdapter != null) {
                    return (CallAdapter<R, C>) callAdapter;
                }
            }
            throw new IllegalArgumentException("CallAdapter Facotory don`t adpter retrun type");
        }

        private Converter<Response,R> createResponseConverter(Type type) {
            List<Converter.Factory> converterFactories = simple.getConverterFactories();
            for (Converter.Factory converterFactory : converterFactories) {
                Converter<Response, ?> responseConverter = converterFactory.responseConverter(type);
                if (responseConverter != null) {
                    return (Converter<Response, R>) responseConverter;
                }
            }
            throw new IllegalArgumentException("converter factory don't convert Response");
        }
    }




}
