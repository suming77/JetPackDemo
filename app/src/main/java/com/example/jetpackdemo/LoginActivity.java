package com.example.jetpackdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jetpackdemo.model.User;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallback;
import com.google.android.material.button.MaterialButton;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/06 18:31
 * @类描述 ${TODO}
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mClose;
    private MaterialButton mLogin;
    private Tencent mTencent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_login);
        mClose = findViewById(R.id.action_close);
        mLogin = findViewById(R.id.action_login);


        mLogin.setOnClickListener(this);
        mClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_close:
                finish();
                break;
            case R.id.action_login:
                login();
                break;
        }
    }

    private void login() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance("101794421", this);
        }

        mTencent.login(this, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject json = (JSONObject) o;

                try {
                    String openid = json.getString("openid");
                    String access_token = json.getString("access_token");
                    String expires_in = json.getString("expires_in");
                    long expires_time = json.getLong("expires_time");

                    mTencent.setAccessToken(access_token, expires_in);
                    mTencent.setOpenId(openid);
                    QQToken qqToken = mTencent.getQQToken();
                    getUserInfo(qqToken, expires_time, openid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(getApplication(), "登录失败:reason == " + uiError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "登录取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo(QQToken qqToken, long expires_time, String openid) {
        UserInfo userInfo = new UserInfo(getApplication(), qqToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject json = (JSONObject) o;
                try {
                    String nickname = json.getString("nickname");
                    String avatar = json.getString("figureurl_2");
                    saveUserInfo(nickname, avatar, expires_time, openid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(getApplication(), "登录失败:reason == " + uiError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "登录取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfo(String nickname, String avatar, long expires_time, String openid) {
        ApiService.get("/user/insert")
                .addParams("name", nickname)
                .addParams("avatar", avatar)
                .addParams("qqOpenId", openid)
                .addParams("expires_time", expires_time)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        if (response.body != null) {
                            UserManager.getUserManager().saveUser(response.body);
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            runOnUiThread(() -> Toast.makeText(getApplication(), "登录失败!", Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        runOnUiThread(() -> Toast.makeText(getApplication(), "登录失败! msg:" + response.message, Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
