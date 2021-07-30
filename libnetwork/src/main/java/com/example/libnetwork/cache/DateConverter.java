package com.example.libnetwork.cache;

import java.sql.Date;

import androidx.room.TypeConverter;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/25 11:35
 * @类描述 ${TODO}
 */
public class DateConverter {
    @TypeConverter
    public static Long date2Long(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public static Date long2Date(Long date) {
        return new Date(date);
    }
}
