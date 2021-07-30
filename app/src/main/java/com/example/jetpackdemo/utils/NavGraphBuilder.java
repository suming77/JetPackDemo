package com.example.jetpackdemo.utils;

import android.content.ComponentName;

import com.example.jetpackdemo.model.Destination;
import com.example.jetpackdemo.navigator.FixFragmentNavigator;
import com.example.libcommon.AppGlobals;

import java.util.HashMap;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/23 11:35
 * @类描述 ${TODO}
 */
public class NavGraphBuilder {
    //NavGraphNavigator也是页面路由导航器的一种，只不过他比较特殊。
    //它只为默认的展示页提供导航服务,但真正的跳转还是交给对应的navigator来完成的
    public static void build(NavController controller, FragmentActivity activity, int containerId) {
        NavigatorProvider provider = controller.getNavigatorProvider();

//        FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        //fragment的导航此处使用我们定制的FixFragmentNavigator，底部Tab切换时 使用hide()/show(),而不是replace()
        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity, activity.getSupportFragmentManager(), containerId);
        provider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);
        NavGraph graph = new NavGraph(new NavGraphNavigator(provider));

        HashMap<String, Destination> desConfig = AppConfig.getDesConfig();
        for (Destination value : desConfig.values()) {
            if (value.isFragment) {
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(value.className);
                destination.setId(value.id);
                destination.addDeepLink(value.pageUrl);
                graph.addDestination(destination);
            } else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(value.id);
                destination.addDeepLink(value.pageUrl);
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(), value.className));
                graph.addDestination(destination);
            }

            //给APP页面导航结果图 设置一个默认的展示页的id
            if (value.asStarter) {
                graph.setStartDestination(value.id);
            }
        }
        controller.setGraph(graph);
    }
}
