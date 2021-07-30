package view;

import android.view.ViewGroup;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/11 18:44
 * @类描述 ${TODO}
 */
public interface IPlayTarget {
    //view所在容器在哪里，
    ViewGroup getOwner();

    //活跃状态 位置是否满足自动播放
    void onActive();

    ////非活跃状态，位置滚动出播放位置，暂停它
    void inActive();

    //获取当前target是否正在播放
    boolean isPlaying();
}
