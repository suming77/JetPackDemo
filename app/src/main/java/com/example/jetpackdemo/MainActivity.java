package com.example.jetpackdemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.jetpackdemo.model.Destination;
import com.example.jetpackdemo.model.User;
import com.example.jetpackdemo.utils.AppConfig;
import com.example.jetpackdemo.utils.NavGraphBuilder;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.GetRequest;
import com.example.libnetwork.JsonCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * Navigation组件的目地就是方便开发者在一个Activity中管理多个Fragment
 * <p>
 * NavController：页面导航，页面跳转的关键类，页面的切换能力委托给了NavController
 * <p>
 * Room数据库
 * Room是谷歌为了简化旧式的SQLite操作专门提供的；
 * 1.拥有SQLite的所有功能
 * 2.使用简单，类似retrofit,通过注解的方式实现相关功能。编译时自动生成实现类IMPL
 * 3.LifeCycle,LiveData,Paging天然融合，支持
 * <p>
 * databinding
 * 1.databinding在绑定数据的时候不是里面绑定，延迟有一帧，像需要计算图片宽高来显示则不需要使用databinding来显示，使用原生来显示
 * <p>
 * 初始化数据加载和逻辑是通过liveModel.paging来触发的，
 * 来看看liveData特点:
 * 1.确保UI符合数据状态，因为livedata在设置的时候就遵循观察者模式，当数组的生命周期发生改变的时候，
 * liveData会向它里面注册的observer发出通知，那么我们可以在onChange()方法里面更改UI,来保持数据最新化
 * <p>
 * 2.不需要手动处理声命周期，因为我们在调用liveData.observer注册一个观察者的时候，liveData会与数组的
 * 生命周期进行绑定，当数组被销毁的时候，liveData会移除(反注册)这个观察者。
 * <p>
 * 3.始终保持最新数据，当数组的声明周期变为非活跃时，在再次变为活跃时，能够接收到最新的数据，比如前台到后台，后台到前台
 * 4.事件总线liveDataBus
 * <p>
 * 来看看liveData工作原理:
 * mVersion:同步标志位，在做数据分发的时候，通过mVersion来比对，本次是否进行数据的分发，发了送一次数据，observer接收到两次三数据是不行的
 * observe：注册观察者对象，会把当前数组和observe相关联，LifecycleOwner和Observer包装成具有声明周期的边界对象，保存在hashmap;
 * lifecycleRegistry->addObserver:判断当前数组生命周期状态，与observer包装起来，保存在hashmap，方便分发每个生命周期给observer
 * dispatchEvent()->onStateChanged(),每个生命周期状态的改变都回回调到LifecycleEventObserver的onStateChanged中，
 * 然后回到LifecycleBoundObserver，它实现了LifecycleEventObserver
 * <p>
 * 首次注册观察者对象的时候，执行onActive()，可以做初始化操作；最后一个观察者被移除时，执行onInactive()，可以做反注册操作
 * dispatchingValue（）数据分发-》considerNotify()：判断是否进行数据的分发-》activeStateChanged，状态改变
 * <p>
 * observeForever()这里注册的observer，不会自动反注册，需要自己手动移除
 * postValue:如果我们的环境在子线程当中，必须用postValue,不能用setValue
 * setValue:只能用在主线程里面
 * hasObservers：是否有观察者，是否有活跃状态的观察者：hasActiveObservers
 * <p>
 * 实现类
 * MutableLiveData；复写postValue和setValue，公开，为了规避数据混淆问题
 * MediatorLiveData：众多liveData的聚合管理者，注册观察数据进行中转。
 * ComputableLiveData并不是liveData的子类，
 * <p>
 * LivePagedListBuilder 如何分发数据
 * LivePagedListBuilder通过build构建mLiveData，入参，分页相关数据 -》ComputableLiveData-》onActive()->
 * mExecutor.execute(mRefreshRunnable)刷新数据-》compute()创建mDataSource-》注册监听DataSource首次被置为无效的监听mCallback
 * -》invalidate()遍历所有执行-》每一个callback的onInvalidated（）-》LivePagedListBuilder的mCallback的invalidate()方法
 * -》mInvalidationRunnable寻找活跃的observer-》mExecutor.execute(mRefreshRunnable)刷新数据
 * <p>
 * <p>
 * PagedList:在LivePagedListBuilder中创建-》list的build-》create()-》可连续的dataSource->类ContiguousPagedList
 * dispatchLoadInitial()-》HomeViewModel的loadInitial()方法，初始化就会被执行了。
 * PagedStorage：一页一页数据的存储器，每一页数据都会存储在这里，即内部的ArrayList<List<T>> mPages
 * <p>
 * paging怎么触发分页逻辑：
 * AsyncPagedListDiffer,每次上划都会调用getItem()方法-》loadAround()->loadAroundInternal()
 * 实现类ContiguousPagedList，prependItems还需要往前填充多少item->schedulePrepend()->
 * 异步线程池执行dispatchLoadBefore(),即HomeViewModel的loadBefore()方法
 * appendItems:还需要多少向后产生的数据-》scheduleAppend()->异步线程池执行dispatchLoadAfter(),
 * 即HomeViewModel的loadBefore()方法.
 * <p>
 * dispatchLoadAfter()还传递了mReceiver-》onPageResult() ->根据情况是初始化还是追加数据或者预加载数据。
 * appendPage()->onPageAppended()->notifyChanged()notifyInserted()->遍历callback-》mcallBack被添加
 * addWeakCallback()-》AsyncPagedListDiffer的submitList()。
 * notifyChanged()notifyInserted()有数据改变都会回调到AsyncPagedListDiffer的mPagedListCallback里面，
 * 再通过mUpdateCallback中转，然后AdapterListUpdateCallback的方法中实现mAdapter.notifyItem……
 * <p>
 * <p>
 * ContiguousPagedList的onEmptyPrepend()执行返回数据为空时，状态变为DONE_FETCHING，则不会再做上拉加载任务。
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController mNavController;
    private BottomNavigationView mNavView;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        mNavController = NavHostFragment.findNavController(fragment);
//        NavigationUI.setupActionBarWithNavController(this, mNavController, appBarConfiguration);
//        NavigationUI.setupWithNavController(mNavView, mNavController);

        NavGraphBuilder.build(mNavController, this, fragment.getId());

        mNavView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.e("smy", "id:" + item.getItemId());
/*        HashMap<String, Destination> desConfig = AppConfig.getDesConfig();
        Iterator<Map.Entry<String, Destination>> iterator = desConfig.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Destination> next = iterator.next();
            Destination value = next.getValue();
            Log.e("smy", "value:" + value.id + ", needLogin:" + value.needLogin + ", login:" + UserManager.getUserManager().isLogin());
            if (value != null && value.needLogin && value.id == item.getItemId() && !UserManager.getUserManager().isLogin()) {
                UserManager.getUserManager().login(this).observeForever(new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        Log.e("smy", "登錄成功:" + user.id);
//                        mNavView.setSelectedItemId(item.getItemId());
//                        mNavController.navigate(item.getItemId());
                    }
                });
                return false;
            }
        }*/
        mNavController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }
}