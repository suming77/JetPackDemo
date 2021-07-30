package com.example.jetpackdemo.ui.home;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.jetpackdemo.UserManager;
import com.example.jetpackdemo.model.Feed;
import com.example.jetpackdemo.model.User;
import com.example.libcommon.AppGlobals;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallback;


import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/09 15:06
 * @类描述 ${TODO}
 */
public class InteractionPresenter {
    private static final String URL_TOGGLE_FEED_LIKE = "/ugc/toggleCommentLike";
    private static final String URL_TOGGLE_FEED_DIS = "/ugc/dissFeed";

    public static void toggleFeedLike(LifecycleOwner owner, Feed feed) {
        Log.e("smy","toggleFeedLike333338888");
        UserManager userManager = UserManager.getUserManager();
        if (!userManager.isLogin()) {
            LiveData<User> liveData = userManager.login(AppGlobals.getApplication());
            liveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        toggleFeedLikeInternal(feed);
                    }
                    liveData.removeObserver(this);
                }
            });
            return;
        }
        toggleFeedLikeInternal(feed);
    }

    public static void toggleFeedLikeInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_LIKE)
                .addParams("userId", UserManager.getUserManager().getUserId())
                .addParams("itemId", feed.id)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked").booleanValue();
                            //这里不能使用feed.ugc，要将ugc添加get方法并且@Bindable
                            feed.getUgc().setHasLiked(hasLiked);
                        }
                    }
                });
    }

    public static void toggleFeedDis(LifecycleOwner owner, Feed feed) {
        Log.e("smy","toggleFeedDis222");
        UserManager userManager = UserManager.getUserManager();
        if (!userManager.isLogin()) {
            LiveData<User> liveData = userManager.login(AppGlobals.getApplication());
            liveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        toggleFeedDisInternal(feed);
                    }
                    liveData.removeObserver(this);
                }
            });
            return;
        }
        toggleFeedDisInternal(feed);
    }

    public static void toggleFeedDisInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_DIS)
                .addParams("userId", UserManager.getUserManager().getUserId())
                .addParams("itemId", feed.id)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hadLiked = response.body.getBoolean("hasLiked").booleanValue();
                            //这里不能使用feed.ugc，要将ugc添加get方法并且@Bindable
                            feed.getUgc().setHasdiss(hadLiked);
                        }
                    }
                });
    }
}
