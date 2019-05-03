package com.xs.simple.util;

import android.text.TextUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * type util
 *
 * Created by xs code on 2019/3/17.
 */

public class TypeUtil {

    public static boolean typeClass(Type type,Class<?> clz) {
        if (type == null) return false;
        if (clz == null) return false;
        if (type instanceof Class) {
            Class typeClass = (Class) type;
            if (typeClass == clz) {
                return true;
            } else {
                return false;
            }
        } else {
            String typeStirng = type.toString();
            if (typeStirng.contains("<")) {
                typeStirng = typeStirng.substring(0,typeStirng.indexOf("<"));
            }
            String clzName = clz.getName();
            if (TextUtils.equals(typeStirng, clzName)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean typeMapWithStringObject(Type type) {
        if (typeClass(type, Map.class)) {
            Type[] parameterizedType=((ParameterizedType)type).getActualTypeArguments();
            if (typeClass(parameterizedType[0],String.class) && typeClass(parameterizedType[1],Object.class)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isBasicType(Type type) {
        if (typeClass(type,String.class) ||
                TypeUtil.typeClass(type,int.class) ||
                TypeUtil.typeClass(type,byte.class) ||
                TypeUtil.typeClass(type,short.class) ||
                TypeUtil.typeClass(type,long.class) ||
                TypeUtil.typeClass(type,double.class) ||
                TypeUtil.typeClass(type,float.class)) {
            return true;
        } else {
            return false;
        }
    }
}
