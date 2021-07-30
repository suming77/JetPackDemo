package com.example.libcommon;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/23 11:05
 * @类描述 ${TODO}
 */
public class AppGlobals {

    private static Application sApplication;

    public static Application getApplication() {
        if (sApplication == null) {
            try {
                @SuppressLint("DiscouragedPrivateApi") Method method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
                sApplication = (Application) method.invoke(null, (Object[]) null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return sApplication;
    }
}
