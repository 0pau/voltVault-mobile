package hu.opau.voltvault.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextClock;
import android.widget.TextView;

import javax.annotation.Nullable;

import hu.opau.voltvault.R;
import hu.opau.voltvault.Utils;

public class BottomTabBar extends LinearLayout {

    private LinearLayout layout;
    private int menuResource;

    private OnTabItemSelectedListener onTabItemSelectedListener;

    public BottomTabBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout = new LinearLayout(context);
        layout.setLayoutParams(params);
        int p = Utils.dpToPxInt(getContext(), 5);
        layout.setPadding(p,0,p,0);
        layout.setOrientation(HORIZONTAL);
        layout.setBackgroundColor(Utils.getColor(getContext(), R.attr.activeBackground));

        addView(layout);
    }

    public void setMenuResource(int menuResource) {
        this.menuResource = menuResource;

        Menu menu = new PopupMenu(getContext(), null).getMenu();

        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(menuResource, menu);

        for (int i = 0; i < menu.size(); i++) {

            MenuItem item = menu.getItem(i);
            TabBarItem tbi = new TabBarItem(getContext(), item);
            tbi.setTag(i);

            tbi.setOnClickListener(e->{
                handleItemClick((int)tbi.getTag());
            });

            layout.addView(tbi);
        }

        handleItemClick(0);
    }

    private void handleItemClick(int index) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (i != index) {
                v.setSelected(false);
            } else {
                v.setSelected(true);
            }
        }

        if (onTabItemSelectedListener != null) {
            onTabItemSelectedListener.onSelected(index);
        }
    }

    public static class TabBarItem extends LinearLayout {

        private Context context;
        private TextView tv;
        private ImageView iv;
        public TabBarItem(Context c, MenuItem item) {
            super(c, null);
            context = c;

            LayoutParams lparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutParams wrapParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.weight = 1;
            setLayoutParams(lparams);

            setGravity(Gravity.CENTER);
            setOrientation(VERTICAL);
            setBackgroundResource(R.drawable.bottom_tab_bar_item_bg);
            int padding = Utils.dpToPxInt(getContext(), 10);
            setPadding(padding, padding, padding, padding);

            iv = new ImageView(getContext());
            iv.setImageDrawable(item.getIcon());

            addView(iv);

            tv = new TextView(getContext());
            tv.setLayoutParams(wrapParams);
            tv.setTextSize(Utils.spToPx(c, 5));
            tv.setTypeface(Typeface.create(null, 600,false));
            tv.setText(item.getTitle());
            setSelected(false);
            addView(tv);

        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);

            int color = R.attr.disabledText;
            if (selected) {
                color = android.R.attr.colorAccent;
            }
            iv.setColorFilter(Utils.getColor(getContext(), color));
            tv.setTextColor(Utils.getColor(getContext(), color));

        }
    }

    public void setOnTabItemSelectedListener(OnTabItemSelectedListener onTabItemSelectedListener) {
        this.onTabItemSelectedListener = onTabItemSelectedListener;
    }

    public interface OnTabItemSelectedListener {
        void onSelected(int index);
    }
}
