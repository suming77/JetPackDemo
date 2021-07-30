package view;

import android.graphics.Point;
import android.net.MacAddress;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/11 18:34
 * @类描述 ${TODO}检查target是否在有效范围内，如果在则调用onActive方法，否则调用inActive方法
 */
public class PageListPlayDetector {
    //收集一个个的能够进行视频播放的 对象，面向接口
    private List<IPlayTarget> mIPlayTargets = new ArrayList<>();
    private RecyclerView mRecyclerView;
    //正在播放的那个
    private IPlayTarget mPlayingTarget;

    public void addTarget(IPlayTarget target) {
        mIPlayTargets.add(target);
    }

    public void removeTarget(IPlayTarget target) {
        mIPlayTargets.remove(target);
    }

    public PageListPlayDetector(LifecycleOwner owner, RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        //通过LifecycleOwner监听数组的生命周期，数组生命周期的变更都会回调到onStateChanged()里面
        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    recyclerView.getAdapter().unregisterAdapterDataObserver(mDataObserver);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });
        //怎么检查recyclerView有数据被添加时自动播放
        recyclerView.getAdapter().registerAdapterDataObserver(mDataObserver);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //当列表滚动停止之后，触发自动播放的逻辑检测
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    autoPlay();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //判断当列表不在滚动的时候，观察target是否还满足条件
//                if (mPlayingTarget != null && mPlayingTarget.isPlaying() && !isTargetInBounds(mPlayingTarget)) {
//                    mPlayingTarget.inActive();
//                }

                if (dx == 0 && dy == 0) {
                    //时序问题。当执行了AdapterDataObserver#onItemRangeInserted  可能还没有被布局到RecyclerView上。
                    //所以此时 recyclerView.getChildCount()还是等于0的。
                    //等childView 被布局到RecyclerView上之后，会执行onScrolled（）方法
                    //并且此时 dx,dy都等于0
                    postAutoPlay();
                } else {
                    //如果有正在播放的,且滑动时被划出了屏幕 则 停止他
                    if (mPlayingTarget != null && mPlayingTarget.isPlaying() && !isTargetInBounds(mPlayingTarget)) {
                        mPlayingTarget.inActive();
                    }
                }
            }
        });
    }


    private void postAutoPlay() {
        mRecyclerView.post(delayAutoPlay);
    }

    Runnable delayAutoPlay = new Runnable() {
        @Override
        public void run() {
            autoPlay();
        }
    };

    RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        //当有数据被添加到recyclerView之后就会回调onItemRangeInserted方法
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            postAutoPlay();
        }

    };


    private void autoPlay() {
        if (mIPlayTargets.isEmpty() || mRecyclerView.getChildCount() <= 0) {
            return;
        }

        //上一个target是否还在屏幕内，如果过在，并且还在播放则不需要开启行的target
        if (mPlayingTarget != null && mPlayingTarget.isPlaying() && isTargetInBounds(mPlayingTarget)) {
            return;
        }

//
//        for (int i = 0; i < mIPlayTargets.size(); i++) {
//            IPlayTarget  playTarget = mIPlayTargets.get(i);
//            if (playTarget!=null && playTarget.isPlaying()){
//                playTarget.inActive();
//            }
//        }

        IPlayTarget playTarget = null;
        //检测视频是否有一般的高度出于屏幕内，找到适合的target
        for (int i = 0; i < mIPlayTargets.size(); i++) {
            boolean inBounds = isTargetInBounds(mIPlayTargets.get(i));
            if (inBounds) {
                playTarget = mIPlayTargets.get(i);
                break;
            }
        }


        Log.e("ysl", " mIPlayTargets.size == "+mIPlayTargets.size());
        Log.e("ysl", "mPlayingTarget == "+mPlayingTarget);
        //全局变量保存,将上一个target释放
        if (playTarget != null) {
            if (mPlayingTarget != null && mPlayingTarget.isPlaying()) {
                mPlayingTarget.inActive();
            }
            Log.e("ysl", " playTarget == "+playTarget);
            mPlayingTarget = playTarget;
            playTarget.onActive();//新视频播放
        }
    }

    /**
     * 检测 IPlayTarget 所在的 viewGroup 是否至少还有一半的大小在屏幕内
     *
     * @param target
     * @return
     */
    private boolean isTargetInBounds(IPlayTarget target) {
        ViewGroup owner = target.getOwner();//所在容器

        recyclerViewLocation();
        if (!owner.isShown() || !owner.isAttachedToWindow()) {
            return false;
        }

        //记录target在屏幕中的位置
        int[] location = new int[2];

        owner.getLocationOnScreen(location);
        int centerY = location[1] + owner.getHeight() / 2;//所在中间Y值

        return centerY >= mPoint.x && centerY <= mPoint.y;
    }

    Point mPoint = null;

    private Point recyclerViewLocation() {
        if (mPoint == null) {
            int[] rvLocation = new int[2];
            mRecyclerView.getLocationOnScreen(rvLocation);
            int top = rvLocation[1];
            int bottom = rvLocation[1] + mRecyclerView.getHeight();
            mPoint = new Point(top, bottom);
        }
        return mPoint;
    }

    public void onResume() {
        if (mPlayingTarget != null) {
            mPlayingTarget.onActive();
        }
    }

    public void onPause() {
        if (mPlayingTarget != null) {
            mPlayingTarget.inActive();
        }
    }
}
