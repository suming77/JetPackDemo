package com.example.jetpackdemo.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jetpackdemo.R;
import com.example.jetpackdemo.databinding.LayoutRefreshViewBinding;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import view.EmptyView;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/30 18:35
 * @类描述 ${TODO}
 */
public abstract class AbsListFragment<T, M extends AbsViewModel<T>> extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    private LayoutRefreshViewBinding mBinding;
    public RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private EmptyView mEmptyView;
    protected PagedListAdapter<T, RecyclerView.ViewHolder> mAdapter;
    private boolean hasData;
    protected M mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = LayoutRefreshViewBinding.inflate(inflater, container, false);
        mRecyclerView = mBinding.recyclerView;
        mRefreshLayout = mBinding.refreshLayout;
        mEmptyView = mBinding.emptyView;

        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        mAdapter = getAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecyclerView.addItemDecoration(decoration);

        genericViewModel();
//        afterCreateView();
        return mBinding.getRoot();
    }

//    protected abstract void afterCreateView();

    //利用 子类传递的 泛型参数实例化出absViewModel 对象。
    public void genericViewModel() {
        Log.e("smy", "onViewCreated");
        //获取泛型参数的第二个参数
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] arguments = type.getActualTypeArguments();
        if (arguments.length > 1) {
            Type argument = arguments[1];
            Class aClass = ((Class) argument).asSubclass(AbsViewModel.class);
            mViewModel = (M) ViewModelProviders.of(this).get(aClass);
            //主动触发初始化数据加载后的绑定
            mViewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<T>>() {
                @Override
                public void onChanged(PagedList<T> list) {
                    submitList(list);
                    Log.e("smy", "submitList");
                }
            });

            //监听数据加载的边界回调,监听分页时有无更多数据,以决定是否关闭上拉加载的动画
            mViewModel.getBoundaryLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    //控制空布局的现实和隐藏
                    finishRefresh(aBoolean);
                    Log.e("smy", "getBoundaryLiveData");
                }
            });
        }
    }

    public void submitList(PagedList<T> list) {
        //只有当新数据集合大于0 的时候，才调用adapter.submitList
        //否则可能会出现 页面----有数据----->被清空-----空布局
        if (list.size() > 0) {
            mAdapter.submitList(list);
        }

        finishRefresh(list.size() > 0);
    }

    public void finishRefresh(boolean hasData) {
        PagedList<T> currentList = mAdapter.getCurrentList();
        hasData = hasData || currentList != null && currentList.size() > 0;

        RefreshState state = mRefreshLayout.getState();
        Log.e("smy", "hasData:" + hasData + " | isFooter:" + state.isFooter + " | isHeader:" + state.isHeader + " | isOpening:" + state.isOpening);
        if (state.isFooter && state.isOpening) {
            mRefreshLayout.finishLoadMore();
        } else if (state.isHeader && state.isOpening) {
            mRefreshLayout.finishRefresh();
        }

        if (hasData) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public abstract PagedListAdapter<T, RecyclerView.ViewHolder> getAdapter();
}
