package view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jetpackdemo.R;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/30 18:10
 * @类描述 ${TODO}
 */
public class EmptyView extends LinearLayout {

    private MaterialButton mBtnEmpty;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;

    public EmptyView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_empty_view, this, false);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        mIvEmpty = findViewById(R.id.iv_empty);
        mTvEmpty = findViewById(R.id.tv_empty);
        mBtnEmpty = findViewById(R.id.btn_empty);
    }

    public void setImageIcon(@DrawableRes int res) {
        mIvEmpty.setImageResource(res);
    }

    public void setEmptyText(String content) {
        if (TextUtils.isEmpty(content)) {
            mTvEmpty.setVisibility(View.GONE);
        } else {
            mTvEmpty.setText(content);
            mTvEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void setEmptyText(String title, View.OnClickListener listener) {
        if (TextUtils.isEmpty(title)) {
            mBtnEmpty.setVisibility(View.GONE);
        } else {
            mBtnEmpty.setText(title);
            mBtnEmpty.setVisibility(View.VISIBLE);
            mBtnEmpty.setOnClickListener(listener);
        }
    }
}
