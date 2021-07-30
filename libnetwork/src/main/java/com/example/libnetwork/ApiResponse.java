package com.example.libnetwork;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 15:40
 * @类描述 ${TODO}返回体一个包装
 */
public class ApiResponse<T> {
    public String message;
    public int status;
    public T body;
    public boolean success;
}
