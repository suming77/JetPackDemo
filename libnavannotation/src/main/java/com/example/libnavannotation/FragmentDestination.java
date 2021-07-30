package com.example.libnavannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/22 16:52
 * @类描述 ${TODO}
 */

@Target(ElementType.TYPE)
public @interface FragmentDestination {
    String pageUrl();

    boolean needLogin() default false;

    boolean asStarter() default false;
}
