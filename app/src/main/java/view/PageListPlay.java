package view;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;

import com.example.jetpackdemo.R;
import com.example.libcommon.AppGlobals;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/10 17:11
 * @类描述 ${TODO}
 */
public class PageListPlay {

    public SimpleExoPlayer mExoPlayer;
    public PlayerView mPlayView;
    public PlayerControlView mControlView;
    public String playUrl;

    public PageListPlay() {
        //使用google推荐方法，视屏画面渲染工厂类，语音选择器，缓存控制器

        Application application = AppGlobals.getApplication();
        //创建exoplayer播放器实例
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(application,
                //视频每一这的画面如何渲染,实现默认的实现类
                new DefaultRenderersFactory(application),
                //视频的音视频轨道如何加载,使用默认的轨道选择器
                new DefaultTrackSelector(),
                //视频缓存控制逻辑,使用默认的即可
                new DefaultLoadControl());

        //加载咱们布局层级优化之后的能够展示视频画面的View
        mPlayView = (PlayerView) LayoutInflater.from(application).inflate(R.layout.layout_exo_player_view, null, false);
        //加载咱们布局层级优化之后的视频播放控制器
        mControlView = (PlayerControlView) LayoutInflater.from(application).inflate(R.layout.layout_exo_player_contorller_view, null, false);

        //别忘记 把播放器实例 和 playerView，controlView相关联
        //如此视频画面才能正常显示,播放进度条才能自动更新
        mPlayView.setPlayer(mExoPlayer);
        mControlView.setPlayer(mExoPlayer);

    }

    public void release() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.stop(true);
            mExoPlayer.release();
            mExoPlayer = null;
        }

        if (mPlayView != null) {
            mPlayView.setPlayer(null);
            mPlayView = null;
        }

        if (mControlView != null) {
            mControlView.setPlayer(null);
            mControlView.setVisibilityListener(null);
            mControlView = null;
        }
    }
}
