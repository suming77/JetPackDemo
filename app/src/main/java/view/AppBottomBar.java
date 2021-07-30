package view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;

import com.example.jetpackdemo.R;
import com.example.jetpackdemo.model.BottomBar;
import com.example.jetpackdemo.model.Destination;
import com.example.jetpackdemo.utils.AppConfig;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/23 12:18
 * @类描述 ${TODO}
 */
public class AppBottomBar extends BottomNavigationView {
    private static int[] sIcons = new int[]{R.mipmap.icon_tab_home, R.mipmap.icon_tab_sofa, R.mipmap.icon_tab_publish, R.mipmap.icon_tab_find, R.mipmap.icon_tab_mine};

    public AppBottomBar(@NonNull Context context) {
        this(context, null);
    }

    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BottomBar bottomBar = AppConfig.getBottomBarConfig();
        List<BottomBar.Tab> tabs = bottomBar.tabs;

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        int[] colors = new int[]{Color.parseColor(bottomBar.activeColor), Color.parseColor(bottomBar.inActiveColor)};
        ColorStateList stateList = new ColorStateList(states, colors);
        setItemIconTintList(stateList);
        setItemTextColor(stateList);
        //LABEL_VISIBILITY_LABELED:设置按钮的文本为一直显示模式
        //LABEL_VISIBILITY_AUTO:当按钮个数小于三个时一直显示，或者当按钮个数大于3个且小于5个时，被选中的那个按钮文本才会显示
        //LABEL_VISIBILITY_SELECTED：只有被选中的那个按钮的文本才会显示
        //LABEL_VISIBILITY_UNLABELED:所有的按钮文本都不显示
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        setSelectedItemId(bottomBar.selectTab);

        for (BottomBar.Tab tab : tabs) {
            if (!tab.enable) continue;
            int id = getId(tab.pageUrl);
            if (id < 0) {
                continue;
            }

            MenuItem item = getMenu().add(0, id, tab.index, tab.title);
            item.setIcon(sIcons[tab.index]);
        }

        //此处给按钮icon设置大小
        int index = 0;
        for (BottomBar.Tab tab : bottomBar.tabs) {
            if (!tab.enable) continue;
            int id = getId(tab.pageUrl);
            if (id < 0) {
                continue;
            }

            int iconSize = dp2px(tab.size);
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(tab.index);
            itemView.setIconSize(iconSize);
            if (TextUtils.isEmpty(tab.title)) {
                int tintColor = TextUtils.isEmpty(tab.tintColor) ? Color.parseColor("#ff678f") : Color.parseColor(tab.tintColor);
                itemView.setIconTintList(ColorStateList.valueOf(tintColor));
                //禁止掉点按时 上下浮动的效果
                itemView.setShifting(false);

                /**
                 * 如果想要禁止掉所有按钮的点击浮动效果。
                 * 那么还需要给选中和未选中的按钮配置一样大小的字号。
                 *
                 *  在MainActivity布局的AppBottomBar标签增加如下配置，
                 *  @style/active，@style/inActive 在style.xml中
                 *  app:itemTextAppearanceActive="@style/active"
                 *  app:itemTextAppearanceInactive="@style/inActive"
                 */
            }
            index++;
        }

        //底部导航栏默认选中项
        if (bottomBar.selectTab != 0) {
            BottomBar.Tab selectTab = bottomBar.tabs.get(bottomBar.selectTab);
            if (selectTab.enable) {
                int itemId = getId(selectTab.pageUrl);
                //这里需要延迟一下 再定位到默认选中的tab
                //因为 咱们需要等待内容区域,也就NavGraphBuilder解析数据并初始化完成，
                //否则会出现 底部按钮切换过去了，但内容区域还没切换过去
                post(() -> setSelectedItemId(itemId));
            }
        }
    }

    private int dp2px(int size) {
        float value = getContext().getResources().getDisplayMetrics().density * size + 0.5f;
        return (int) value;
    }

    private int getId(String pageUrl) {
        Destination destination = AppConfig.getDesConfig().get(pageUrl);
        if (destination == null) {
            return -1;
        }
        return destination.id;
    }
}
