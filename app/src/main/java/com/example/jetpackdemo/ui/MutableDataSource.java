package com.example.jetpackdemo.ui;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/03 17:47
 * @类描述 ${TODO}
 */
public class MutableDataSource<key, value> extends PageKeyedDataSource<key, value> {
    public List<value> mList = new ArrayList<>();

    public PagedList<value> buildPageList(PagedList.Config config) {
        PagedList<value> pagedList = new PagedList.Builder<key, value>(this, config)
                .setFetchExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
                .build();
        return pagedList;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<key> params, @NonNull LoadInitialCallback<key, value> callback) {
        callback.onResult(mList, null, null);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<key> params, @NonNull LoadCallback<key, value> callback) {
        callback.onResult(Collections.emptyList(), null);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<key> params, @NonNull LoadCallback<key, value> callback) {
        callback.onResult(Collections.emptyList(), null);

    }
}
