package com.example.jetpackdemo.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/01 15:22
 * @类描述 ${TODO}
 * <p>
 * DataSource(类型)
 * <p>
 * DataSource<Key, Value>数据源：key对应加载数据的条件信息，value对应数据实体类
 * <p>
 * PageKeyedDataSource<Key, Value>:适用于目标数据根据页信息请求数据的场景
 * ItemKeyedDataSource<Key, Value>:适用于目标数据的加载依赖特定item的信息
 * PositionalDataSource<T>:适用于目标数据总数固定，通过特定的位置加载数据。
 */
public abstract class AbsViewModel<T> extends ViewModel {

    private final LiveData<PagedList<T>> mLiveData;
    private DataSource mDataSource;
    private MutableLiveData<Boolean> mBoundaryLiveData = new MutableLiveData<>();
    protected final PagedList.Config mConfig;

    public AbsViewModel() {
        mConfig = new PagedList.Config.Builder()
                .setPageSize(10)//每次分页需要加载的数量
                .setInitialLoadSizeHint(12)//每次初始化加载的时候的加载数量
                //.setMaxSize(100)//这个列表总共有多少条数据
//                .setEnablePlaceholders(true)//数据没加载出来的用默认占位符展示
                //还有多少数据到屏幕地步的时候开始加载数据，即预加载。如果没有设置则默认是PageSize，
                // 如果你不想首次加载数据后进行预加载，可以将setInitialLoadSizeHint的值设置比PageSize大即可
//                .setPrefetchDistance(20)
                .build();

        mLiveData = new LivePagedListBuilder(factory, mConfig)
                .setInitialLoadKey(0)//加载数据的key
//        .setFetchExecutor()
                .setBoundaryCallback(mBoundaryCallback)//能监听PagedList加载的状态
                .build();

//        mLiveData.observeForever(new Observer<PagedList<T>>() {
//            @Override
//            public void onChanged(PagedList<T> list) {
//                adapter.submitList(list);
//            }
//        });
    }

    //PagedList数据被加载 情况的边界回调callback
    //但 不是每一次分页 都会回调这里，具体请看 ContiguousPagedList#mReceiver#onPageResult
    //deferBoundaryCallbacks
    PagedList.BoundaryCallback<T> mBoundaryCallback = new PagedList.BoundaryCallback<T>() {
        @Override
        public void onZeroItemsLoaded() {//新提交的PagedList中没有数据
            mBoundaryLiveData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull T itemAtFront) {//新提交的PagedList中第一条数据被加载到列表上
            mBoundaryLiveData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull T itemAtEnd) {//新提交的PagedList中最后一条数据被加载到列表上
//            super.onItemAtEndLoaded(itemAtEnd);
        }
    };

    DataSource.Factory factory = new DataSource.Factory() {

        @NonNull
        @Override
        public DataSource create() {
            if (mDataSource == null || mDataSource.isInvalid()) {
            mDataSource = createDataSource();
            }
            return mDataSource;
        }
    };

    public abstract DataSource createDataSource();

    public LiveData<PagedList<T>> getLiveData() {
        return mLiveData;
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public MutableLiveData<Boolean> getBoundaryLiveData() {
        return mBoundaryLiveData;
    }
}
