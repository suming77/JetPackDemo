package com.example.jetpackdemo;

import android.content.Context;
import android.content.Intent;

import com.example.jetpackdemo.model.User;
import com.example.libnetwork.cache.CacheManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/06 19:03
 * @类描述 ${TODO}
 */
public class UserManager {
    private static final String KEY_CACHE_USER = "cache_user";
    private User mUser;
    private static UserManager mUserManager = new UserManager();
    private MutableLiveData<User> mUserLiveData = new MutableLiveData<>();

    public static UserManager getUserManager() {
        return mUserManager;
    }

    private UserManager() {
        User cache = (User) CacheManager.getCache(KEY_CACHE_USER);
        if (cache != null && cache.expires_time < System.currentTimeMillis()) {
            mUser = cache;
        }
    }

    public void saveUser(User user) {
        mUser = user;
        CacheManager.save(KEY_CACHE_USER, user);
        if (mUserLiveData.hasObservers()) {
            //有可能主线程有可能子线程，用post
            mUserLiveData.postValue(user);
        }
    }

    public LiveData<User> login(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return mUserLiveData;
    }

    public boolean isLogin() {
        return mUser == null ? false : mUser.expires_time < System.currentTimeMillis();
    }

    public User getUser() {
        return isLogin() ? mUser : null;
    }

    public long getUserId() {
        return isLogin() ? mUser.id : 0;
    }


}
