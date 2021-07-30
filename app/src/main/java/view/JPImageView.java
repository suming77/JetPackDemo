package view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jetpackdemo.utils.PixUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/27 15:10
 * @类描述 ${TODO}
 */
public class JPImageView extends AppCompatImageView {
    public JPImageView(Context context) {
        super(context);
    }

    public JPImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JPImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //公共静态方法以及要设置的值,第一个参数必须是控件本身，
    // requireAll = true 默认，是否每个属性都被复制绑定一个表达式
    @BindingAdapter(value = {"image_url", "isCircle"}, requireAll = true)
    public static void setImageViewUrl(JPImageView view, String image_url, boolean isCircle) {
        RequestBuilder<Drawable> builder = Glide.with(view).load(image_url);
        if (isCircle) {
            builder.transform(new CircleCrop());//圆型
        }

        //防止图片很大，而需要的很小造成资源浪费，复写override方法
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null && layoutParams.height > 0 && layoutParams.width > 0) {
            builder.override(layoutParams.width, layoutParams.height);
        }
        builder.into(view);
    }

    public void bindData(int widthPx, int heightPx, int marginLeft, String imageUrl) {
        bindData(widthPx, heightPx, marginLeft, PixUtils.getScreenWidth(), PixUtils.getScreenWidth(), imageUrl);
    }

    public void bindData(int widthPx, int heightPx, int marginLeft, int maxWidth, int maxHeight, String imageUrl) {
        if (widthPx <= 0 || heightPx <= 0) {
            Glide.with(this).load(imageUrl).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    int height = resource.getIntrinsicHeight();
                    int width = resource.getIntrinsicWidth();
                    setSize(width, height, marginLeft, maxWidth, maxHeight);
                    setImageDrawable(resource);
                }
            });
            return;
        }

        setSize(widthPx, heightPx, marginLeft, maxWidth, maxHeight);
        setImageViewUrl(this, imageUrl, false);
    }

    public void setSize(int width, int height, int marginLeft, int maxWidth, int maxHeight) {
        int finalWidth, finalHeight;
        if (width > height) {
            finalWidth = maxWidth;
            finalHeight = (int) (height / (width * 1.0f / finalWidth));
        } else {
            finalHeight = maxHeight;
            finalWidth = (int) (width / (height *1.0f / finalHeight));
        }

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(finalWidth, finalHeight);
        params.leftMargin = width > height ? 0 : PixUtils.dp2px(marginLeft);
        setLayoutParams(params);
    }
}
