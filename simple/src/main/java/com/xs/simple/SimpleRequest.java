package com.xs.simple;

import android.text.TextUtils;

import com.xs.simplehttp.api.Multiparts;
import com.xs.simplehttp.api.ProgressListener;
import com.xs.simplehttp.api.Request;
import com.xs.simplehttp.core.MethodType;
import com.xs.simplehttp.core.PostType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * simple request
 *
 * Created by xs code on 2019/3/21.
 */

public class SimpleRequest {

    public static final String PARAM_FIELD = "field";
    public static final String PARAM_FIELDMAP = "fieldmap";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_QUERYMAP = "querymap";
    public static final String PARAM_PART = "part";
    public static final String PARAM_PARTMAP = "partmap";
    public static final String PARAM_HEADER = "header";
    public static final String PARAM_HEADERMAP = "headermap";
    public static final String PARAM_PROGRESS_UP = "upProgress";
    public static final String PARAM_PROGRESS_DOWN = "downProgress";
    public static final String PARAM_PATH = "path";
    public static final String PARAM_JSON = "json";


    private String path;

    private Map<String, String> paths = new HashMap<>();

    private Map<String, Object> headers = new HashMap<>();

    private Map<String, Object> params = new HashMap<>();

    private List<Param> paramList = new ArrayList<>();

    private String jsonParams;

    private Multiparts multipart;

    private MethodType methodType;

    private PostType postType = PostType.FORM;

    private ProgressListener downloadlistener;

    private ProgressListener uploadlistener;


    public void addParam(String key, Object o) {
        params.put(key, o);
    }

    public List<Param> getParamList() {
        return paramList;
    }

    public void addHeader(String key, Object o) {
        headers.put(key, o);
    }

    public void addPath(String key, String value) {
        paths.put(key, value);
    }

    public void addParam(Param param) {
        paramList.add(param);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setJsonParams(String jsonParams) {
        this.jsonParams = jsonParams;
    }

    public void setMultipart(Multiparts multipart) {
        this.multipart = multipart;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public void setDownloadlistener(ProgressListener downloadlistener) {
        this.downloadlistener = downloadlistener;
    }

    public void setUploadlistener(ProgressListener uploadlistener) {
        this.uploadlistener = uploadlistener;
    }

    /**
     * build request
     * @return
     */
    public Request buildRequest() {

        Request.Builder builder = new Request.Builder();
        builder.path(path()).header(headers).uploadProgress(uploadlistener).downloadProgress(downloadlistener);
        if (methodType == MethodType.GET) {
            builder.get(params);
        } else if (methodType == MethodType.POST) {
            if (postType == PostType.FORM) {
                builder.postForm(params);
            } else {
                if (!TextUtils.isEmpty(jsonParams)) {
                    builder.postJson(jsonParams);
                } else if (params.size() > 0) {
                    builder.postJson(params);
                }
            }
        } else if (methodType == MethodType.UPLOAD) {
            if (multipart == null || multipart.getParts().size() == 0) {
                throw new IllegalArgumentException("multipart is null");
            }
            builder.upload(multipart, params);
        } else {
            return null;
        }
        return builder.build();
    }

    /**
     * replace path
     *
     * @return
     */
    private String path() {
        if (path != null && paths.size() > 0) {
            for (Map.Entry<String, String> entry : paths.entrySet()) {
                path = path.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return TextUtils.isEmpty(path) ? "" : path;
    }

    public static class Param {

        private String param;

        private Type type;

        private String key;

        public Param(String param, Type type, String key) {
            this.param = param;
            this.type = type;
            this.key = key;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
