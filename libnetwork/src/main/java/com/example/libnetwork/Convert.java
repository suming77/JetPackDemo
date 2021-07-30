package com.example.libnetwork;

import java.lang.reflect.Type;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 17:15
 * @类描述 ${TODO}
 */
public interface Convert<T> {
    T convert(String response, Type type);
    T convert(String response, Class cls);
}
