package com.example.jetpackdemo.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import view.PageListPlayDetector;

import com.bumptech.glide.load.model.ModelLoader;
import com.example.jetpackdemo.R;
import com.example.jetpackdemo.model.Feed;
import com.example.jetpackdemo.ui.AbsListFragment;
import com.example.jetpackdemo.ui.MutableDataSource;
import com.example.libnavannotation.FragmentDestination;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {

    private PageListPlayDetector mDetector;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //缓存数据
        mViewModel.getCacheLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);
            }
        });

        mDetector = new PageListPlayDetector(this, mRecyclerView);
    }

    @Override
    public PagedListAdapter getAdapter() {
        String feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        Log.e("smy", "getAdapter == " + feedType);
        return new FeedAdapter(getContext(), feedType) {
            //当有VIew进入和移出屏幕的时候
            @Override
            public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                if (holder.isVideoItem()) {
                    mDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                mDetector.removeTarget(holder.getListPlayerView());
            }
        };
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        final PagedList<Feed> currentList = mAdapter.getCurrentList();
        if (currentList == null || currentList.size() <= 0) {
            finishRefresh(false);
            return;
        }
        //如果有一次返回空数据就不会再做分页逻辑
        //自己手动触发
        Feed feed = currentList.get(mAdapter.getItemCount() - 1);//获取最后一条数据
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = currentList.getConfig();
                if (data != null && !data.isEmpty()) {
                    //这里 咱们手动接管 分页数据加载的时候 使用MutableItemKeyedDataSource也是可以的。
                    //由于当且仅当 paging不再帮我们分页的时候，我们才会接管。所以 就不需要ViewModel中创建的DataSource继续工作了，所以使用
                    //MutablePageKeyedDataSource也是可以的
                    MutableDataSource dataSource = new MutableDataSource();

                    //这里要把列表上已经显示的先添加到dataSource.data中
                    //而后把本次分页回来的数据再添加到dataSource.data中
                    dataSource.mList.addAll(data);
                    PagedList pagedList = dataSource.buildPageList(config);
                    submitList(pagedList);
                }
            }
        });

    }

    //invalidate 之后Paging会重新创建一个DataSource 重新调用它的loadInitial方法加载初始化数据
    //详情见：LivePagedListBuilder#compute方法
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        Log.e("smy", "onRefresh == " + refreshLayout.getState());
        mViewModel.getDataSource().invalidate();//刷新数据
    }

    @Override
    public void onPause() {
        mDetector.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mDetector.onResume();
        super.onResume();
    }
}