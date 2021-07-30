package com.example.jetpackdemo.ui.publish;

import android.os.Bundle;

import com.example.libnavannotation.ActivityDestination;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/23 15:55
 * @类描述 ${TODO}
 */
@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
