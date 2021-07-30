package com.example.jetpackdemo.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jetpackdemo.BR;
import com.example.jetpackdemo.databinding.LayoutFeedTypeImageBinding;
import com.example.jetpackdemo.databinding.LayoutFeedTypeVideoBinding;
import com.example.jetpackdemo.model.Feed;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import view.ListPlayerView;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/30 19:06
 * @类描述 ${TODO}每一个继承PagedListAdapter的必须复写两个构造方法
 * PagedListAdapter的核心能力都交给了AsyncPagedListDiffer来处理。
 * <p>
 * AsyncPagedListDiffer.ItemCallback
 * <p>
 * AsyncPagedListDiffer构造方法AdapterListUpdateCallback，把RecyclerView的adapter传了进去，数据的insert,remove,change等操作
 * AsyncPagedListDiffer主要有两个能力：
 * 1：处理数据的差分异，pagding框架在处理下拉刷新的时候处理是比较细腻的，不是直接notifyChange粗暴全屏刷新，
 * 而是把新数据集合旧数据集进行差分异比对，比对之后，如果屏幕上有需要指定额item需要刷新的则指定它去刷新，
 * 有需要删除的则指定它去删除。差分异的入口在submitList().
 * <p>
 * submitList()->config开启一个线程池->computeDiff把新老数据都传了进去，计算两个数据之间的差异，返回一个result，
 * 然后再通过线程latchPagedList()分发这个差分已的结果，dispatchDiff（）通过mUpdateCallback来接收。
 * <p>
 * 2.监听数据集的变更， submitList()->pagedList.addWeakCallback添加 mPagedListCallback监听，
 * 当有数据改变通过mPagedListCallback通知adapter执行响应的方法来变更数据。
 */
public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private String mCategory;

    //给数据插分页，做数据比对的时候的回调，一般复写第一个
    protected FeedAdapter(Context context, String category) {
        //复写两个方法
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override//比较item是否相同,通过id比较
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override//比较内容是否相同
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }

    //在做数据插分页的配置
    protected FeedAdapter(@NonNull AsyncDifferConfig<Feed> config) {
        super(config);
    }

    @Override
    public int getItemViewType(int position) {
        Feed item = getItem(position);
        return item.itemType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;

        //DataBinding加载布局
        if (viewType == Feed.TYPE_IMAGE_TEXT) {
            binding = LayoutFeedTypeImageBinding.inflate(mInflater);
        } else {
            binding = LayoutFeedTypeVideoBinding.inflate(mInflater);
        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding mBinding;
        private ListPlayerView mListPlayerView;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Feed item) {
            mBinding.setVariable(BR.feed, item);
            mBinding.setVariable(BR.lifeCycleOwner, mContext);
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                imageBinding.setFeed(item);
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
                imageBinding.setLifecycleOwner((LifecycleOwner) mContext);
            } else {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.setFeed(item);
                videoBinding.lpvVideo.bindData(mCategory, item.width, item.height, item.cover, item.url);
                Log.e("smy","mContext"+mContext);
                videoBinding.setLifecycleOwner((LifecycleOwner) mContext);
                mListPlayerView = videoBinding.lpvVideo;
            }
        }

        public boolean isVideoItem(){
            return mBinding instanceof  LayoutFeedTypeVideoBinding;
        }

        public ListPlayerView getListPlayerView(){
            return mListPlayerView;
        }
    }
}
