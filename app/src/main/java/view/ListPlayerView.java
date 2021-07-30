package view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jetpackdemo.R;
import com.example.jetpackdemo.utils.PixUtils;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/30 12:18
 * @类描述 ${TODO}全局列表只有一个播放器只有一个进度条，动态添加进去
 */
public class ListPlayerView extends FrameLayout implements IPlayTarget, PlayerControlView.VisibilityListener, Player.EventListener {

    private ProgressBar mBuffer;
    private ImageView mIvBlur, mIvPlay;
    private JPImageView mIvCover;
    private String mCategory;
    private String mVideoUrl;
    private boolean mIsPlaying;

    public ListPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true);
//缓冲转圈圈的view
        mBuffer = findViewById(R.id.pb_buffer);
        //封面view
        mIvCover = findViewById(R.id.cover);
        //高斯模糊背景图,防止出现两边留嘿
        mIvBlur = findViewById(R.id.blur_background);
        //播放盒暂停的按钮
        mIvPlay = findViewById(R.id.iv_play);

        mIvPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("smy", "isPlaying() == " +isPlaying());
                if (isPlaying()) {
                    inActive();
                } else {
                    onActive();
                }
            }
        });
        this.setTransitionName("listPlayerView");
    }

    //点击的时候让底部的控制器显示出来
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击该区域时 我们诸主动让视频控制器显示出来
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        pageListPlay.mControlView.show();
        return true;
    }

    //通过原生数据进行绑定，不建议使用databinding进行绑定
    //每一个页面都会有视频播放，多是独立的播放器和进度条，需要和页面先关联起来
    public void bindData(String category, int widthPx, int heightPx, String coverUrl, String videoUrl) {
        mCategory = category;
        mVideoUrl = videoUrl;
        mIvCover.setImageViewUrl(mIvCover, coverUrl, false);

        //如果该视频的宽度小于高度,则高斯模糊背景图显示出来
        if (widthPx < heightPx) {
            setBlurImageUrl(coverUrl, 10);
            mIvBlur.setVisibility(View.VISIBLE);
        } else {
            mIvBlur.setVisibility(View.INVISIBLE);
        }
        setCoverSize(widthPx, heightPx);
    }

    private void setCoverSize(int widthPx, int heightPx) {
        //这里主要是做视频宽大与高,或者高大于宽时  视频的等比缩放
        int maxWidth = PixUtils.getScreenWidth();
        int maxHeight = maxWidth;

        int layoutWidth = maxWidth;
        int layoutHeight = 0;

        int coverWidth;
        int coverHeight;

        if (widthPx < heightPx) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPx / (widthPx * 1.0f / maxWidth));
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (heightPx * 1.0f / maxHeight));
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = layoutWidth;
        layoutParams.height = layoutHeight;
        setLayoutParams(layoutParams);

        ViewGroup.LayoutParams blurLayoutParams = mIvBlur.getLayoutParams();
        blurLayoutParams.width = layoutWidth;
        blurLayoutParams.height = layoutHeight;
        mIvBlur.setLayoutParams(blurLayoutParams);

        FrameLayout.LayoutParams coverLayoutParams = (LayoutParams) mIvCover.getLayoutParams();
        coverLayoutParams.width = coverWidth;
        coverLayoutParams.height = coverHeight;
        coverLayoutParams.gravity = Gravity.CENTER;
        mIvCover.setLayoutParams(coverLayoutParams);

        FrameLayout.LayoutParams playLayoutParams = (LayoutParams) mIvPlay.getLayoutParams();
        playLayoutParams.gravity = Gravity.CENTER;
        mIvPlay.setLayoutParams(playLayoutParams);

    }

    private void setBlurImageUrl(String coverUrl, int radius) {
        Glide.with(this).load(coverUrl).override(radius).transform(new BlurTransformation())
                .dontAnimate()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        setBackground(resource);//setBackground()否则不会充满组件的宽和高
                    }
                });
    }

    @Override
    public ViewGroup getOwner() {
        return this;
    }

    //视频播放,或恢复播放
    @Override
    public void onActive() {
        //通过该View所在页面的mCategory(比如首页列表tab_all,沙发tab的tab_video,标签帖子聚合的tag_feed) 字段，
        //取出管理该页面的Exoplayer播放器，ExoplayerView播放View,控制器对象PageListPlay

        //在PageListPlayManager里面得到ListPlayerView当前所在页面的ListPlayerView对象
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        PlayerView playView = pageListPlay.mPlayView;
        SimpleExoPlayer exoPlayer = pageListPlay.mExoPlayer;
        PlayerControlView controlView = pageListPlay.mControlView;

        //此处我们需要主动调用一次 switchPlayerView，把播放器Exoplayer和展示视频画面的View ExoplayerView相关联
        //为什么呢？因为在列表页点击视频Item跳转到视频详情页的时候，详情页会复用列表页的播放器Exoplayer，
        // 然后和新创建的展示视频画面的View ExoplayerView相关联，达到视频无缝续播的效果
        //如果 我们再次返回列表页，则需要再次把播放器和ExoplayerView相关联
//        pageListPlay.switchPlayerView(playerView, true);

        //如果父容器不等于this,则把playView添加进去
        ViewParent parent = playView.getParent();
        if (parent != this) {
            //parent如果不为空则是被添加到别的容器中了，需要移除
            if (parent != null) {
                ((ViewGroup) parent).removeView(playView);
                //还应该暂停掉列表上正在播放的那个
                ((ListPlayerView) parent).inActive();
            }
            //高斯模糊作为第一背景图，添加在高斯模糊上面，并且需要指定宽高，这里和封面图一致
            ViewGroup.LayoutParams layoutParams = mIvCover.getLayoutParams();
            this.addView(playView, 1, layoutParams);
        }

        ViewParent ctrParent = controlView.getParent();

        if (ctrParent != this) {
            if (ctrParent != null) {
                ((ViewGroup) ctrParent).removeView(controlView);
            }

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.BOTTOM;
            this.addView(controlView, layoutParams);

        }

        //如果是同一个视频资源,则不需要从重新创建mediaSource。
        //但需要onPlayerStateChanged 否则不会触发onPlayerStateChanged()

        //判断当前播放的和即将播放的是否同一个媒体资源
        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {

        } else {
            MediaSource mediaSource = PageListPlayManager.createMediaSource(mVideoUrl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);//无限循环
            pageListPlay.playUrl = mVideoUrl;
        }
        //显示进度条和控制器
        controlView.show();

        //监听显示隐藏，将暂停和播放按钮显示出来
        controlView.setVisibilityListener(this);
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);//资源缓冲好后立马播放
    }

    @Override
    public void inActive() {
        //暂停视频的播放并让封面图和 开始播放按钮 显示出来
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
//        if (pageListPlay.mExoPlayer == null || pageListPlay.mControlView == null || pageListPlay.mPlayView == null)
//            return;
        pageListPlay.mExoPlayer.setPlayWhenReady(false);
        pageListPlay.mControlView.setVisibilityListener(null);
        pageListPlay.mExoPlayer.removeListener(this);
        mIvPlay.setVisibility(VISIBLE);
        mIvCover.setVisibility(VISIBLE);
        mIvPlay.setImageResource(R.drawable.icon_video_play);
    }

    //状态重置

//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        mIsPlaying = false;
//        mBuffer.setVisibility(GONE);
//        mIvCover.setVisibility(VISIBLE);
//        mIvPlay.setVisibility(VISIBLE);
//        mIvPlay.setImageResource(R.drawable.icon_video_play);
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsPlaying = false;
        mBuffer.setVisibility(GONE);
        mIvCover.setVisibility(VISIBLE);
        mIvPlay.setVisibility(VISIBLE);
        mIvPlay.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public boolean isPlaying() {
        return mIsPlaying;
    }

    @Override
    public void onVisibilityChange(int visibility) {
        mIvPlay.setVisibility(visibility);
        mIvPlay.setImageResource(mIsPlaying ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        //判断当前视屏是否已经准备好
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        SimpleExoPlayer exoPlayer = pageListPlay.mExoPlayer;
        if (playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady) {
            mIvCover.setVisibility(INVISIBLE);
            mBuffer.setVisibility(INVISIBLE);
        } else if (playbackState == Player.STATE_BUFFERING) {
            mBuffer.setVisibility(VISIBLE);
        }

        mIsPlaying = playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady;
        mIvPlay.setImageResource(mIsPlaying ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }
}
