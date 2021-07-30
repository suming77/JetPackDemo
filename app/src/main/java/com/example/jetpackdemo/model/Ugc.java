package com.example.jetpackdemo.model;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/27 14:43
 * @类描述 ${TODO}BaseObservable数据作出变更的时候，那个View使用了这个字段就会重新执行绑定
 */
public class Ugc extends BaseObservable implements Serializable {
    /**
     * likeCount : 153
     * shareCount : 0
     * commentCount : 4454
     * hasFavorite : false
     * hasLiked : true
     * hasdiss:false
     */

    public int likeCount;
    public boolean hasLiked;

    //Bindable注解应该应用于Observable类的任何getter访问器方法。Bindable将在BR类中生成一个字段来标识已更改的字段。
    @Bindable
    public boolean isHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        if (this.hasLiked == hasLiked) return;
        if (hasLiked) {
            likeCount = likeCount + 1;
            setHasdiss(false);
        } else {
            likeCount = likeCount - 1;
        }
        this.hasLiked = hasLiked;
        //数据有改变，重新执行数据绑定
        //通知监听器一个特定的属性已经改变。改变属性的getter应该用Bindable标记，以在BR中生成一个字段，作为fieldId。
        notifyPropertyChanged(BR._all);
    }

    public int shareCount;
    public int commentCount;
    public boolean hasFavorite;
    public boolean hasdiss;

    @Bindable
    public boolean isHasdiss() {
        return hasdiss;
    }

    public void setHasdiss(boolean hasdiss) {
        if (this.hasdiss == hasdiss) return;
        if (hasdiss) {
            setHasLiked(false);
        }
        this.hasdiss = hasdiss;
        notifyPropertyChanged(BR._all);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof Ugc))
            return false;
        Ugc newUgc = (Ugc) obj;
        return likeCount == newUgc.likeCount
                && shareCount == newUgc.shareCount
                && commentCount == newUgc.commentCount
                && hasFavorite == newUgc.hasFavorite
                && hasLiked == newUgc.hasLiked
                && hasdiss == newUgc.hasdiss;
    }

}
