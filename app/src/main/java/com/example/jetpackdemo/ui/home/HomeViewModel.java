package com.example.jetpackdemo.ui.home;

import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.example.jetpackdemo.UserManager;
import com.example.jetpackdemo.model.Feed;
import com.example.jetpackdemo.ui.AbsViewModel;
import com.example.jetpackdemo.ui.MutableDataSource;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallback;
import com.example.libnetwork.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

public class HomeViewModel extends AbsViewModel<Feed> {
    private volatile boolean witchCache = true;
    private static final String TAG = "HomeViewModel";
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();
    private AtomicBoolean loadAfter = new AtomicBoolean(false);//同步位标志，防止加载数据重复
    private String mFeedType;
    @Override
    public DataSource createDataSource() {
        return new HomeDataSource();
    }

    public void setFeedType(String feedType) {

        mFeedType = feedType;
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    class HomeDataSource extends ItemKeyedDataSource<Integer, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载在初始化数据的
            loadData(0, callback);
            witchCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //加载分页数据的
            loadData(params.key, callback);//278
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
            //能够向前加载数据的
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    };

    private void loadData(int key, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        Log.e("smy", "loadData:" );
        if (key > 0) {
            loadAfter.set(true);
        }
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParams("feedType", null)
                .addParams("userId", UserManager.getUserManager().getUserId())
                .addParams("feedId", key)
                .addParams("pageCount", 10)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if (witchCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.e(TAG, "onCacheSuccess == " + response.body.size());
                    List<Feed> list = response.body;
                    MutableDataSource dataSource = new MutableDataSource<Integer, Feed>();
                    dataSource.mList.addAll(list);

                    PagedList pagedList = dataSource.buildPageList(mConfig);
                    cacheLiveData.postValue(pagedList);//子线程用post
                }
            });
        }

        try {
            Request netRequest = witchCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;
//            Log.e(TAG, "data == " + data.toString() + ", witchCache == " + witchCache);
            callback.onResult(data);

            if (key > 0) {
                //通过LiveData发送数据，告诉UI层，是否主动关闭上拉加载分页动画
                ((MutableLiveData) getBoundaryLiveData()).postValue(data.size() > 0);
                loadAfter.set(false);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "loadData: key == " + key);
    }

    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id, callback);
            }
        });
    }


}