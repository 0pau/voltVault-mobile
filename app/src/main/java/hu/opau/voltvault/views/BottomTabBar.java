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
import android.widget.TextView;

import javax.annotation.Nullable;

import hu.opau.voltvault.R;
import hu.opau.voltvault.Utils;

public class BottomTabBar extends LinearLayout {

    private LinearLayout layout;
    private String style = "phone";
    private int menuResource;

    private OnTabItemSelectedListener onTabItemSelectedListener;

    public BottomTabBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        assert attrs != null;

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            if (attrs.getAttributeName(i).equals("tabBarStyle")) {
                style = attrs.getAttributeValue(i);
                break;
            }
        }


        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout = new LinearLayout(context);

        int ph = Utils.dpToPxInt(getContext(), 15);
        int pv = Utils.dpToPxInt(getContext(), 5);
        layout.setPadding(ph,pv,ph,pv);

        switch (style) {
            case "phone":
                layout.setOrientation(HORIZONTAL);
                break;
            case "tablet-land":
                layout.setPadding(ph,ph,ph,ph);
                layout.setOrientation(VERTICAL);
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                break;
            case "tablet":
                layout.setPadding(ph,ph,ph,ph);
                layout.setOrientation(VERTICAL);
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                break;
        }

        layout.setLayoutParams(params);
        layout.setBackgroundColor(Utils.getColor(getContext(), R.attr.activeBackground));

        addView(layout);
    }

    public void setSelectedIndex(int index) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            v.setSelected(i == index);
        }
    }

    public void setMenuResource(int menuResource) {
        this.menuResource = menuResource;

        Menu menu = new PopupMenu(getContext(), null).getMenu();

        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(menuResource, menu);

        for (int i = 0; i < menu.size(); i++) {

            MenuItem item = menu.getItem(i);
            TabBarItem tbi = new TabBarItem(getContext(), item, style);
            tbi.setTag(i);

            tbi.setOnClickListener(e->{
                handleItemClick((int)tbi.getTag());
            });

            layout.addView(tbi);
        }

        handleItemClick(0);
    }

    private void handleItemClick(int index) {
        setSelectedIndex(index);

        if (onTabItemSelectedListener != null) {
            onTabItemSelectedListener.onSelected(index);
        }
    }

    public static class TabBarItem extends LinearLayout {

        private Context context;
        private TextView tv;
        private ImageView iv;
        private String style;

        public TabBarItem(Context c, MenuItem item) {
            this(c,item, "phone");
        }
        public TabBarItem(Context c, MenuItem item, String style) {
            super(c, null);
            context = c;
            this.style = style;

            LayoutParams lparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.weight = 1;

            if (style.equals("tablet") || style.equals("tablet-land")) {
                lparams.weight = 0;
            }

            LayoutParams wrapParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutParams(lparams);


            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setBackgroundResource(R.drawable.bottom_tab_bar_item_bg);

            int padding = Utils.dpToPxInt(getContext(), 10);
            int paddingPhoneVertical = Utils.dpToPxInt(getContext(), 5);
            setPadding(padding, padding, padding, padding);

            if (style.equals("phone")) {
                setPadding(padding, paddingPhoneVertical, padding, paddingPhoneVertical);
            }



            iv = new ImageView(getContext());
            iv.setImageDrawable(item.getIcon());

            if (style.equals("tablet-land")) {
                setOrientation(HORIZONTAL);
                setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.rightMargin = Utils.dpToPxInt(c, 10);
                iv.setLayoutParams(lp);
            }

            addView(iv);

            tv = new TextView(getContext());
            tv.setLayoutParams(wrapParams);
            if (!style.equals("tablet-land")) {
                tv.setTextSize(Utils.spToPx(c, 5));
            }
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
