package hu.opau.voltvault.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import hu.opau.voltvault.R;

public class FilterSpinner<T> extends LinearLayout {

    private Spinner sp;
    private LinearLayout layout;
    private OnClickListener listener;
    private List<SpinnerItem> items;
    private TextView selectedValueTextView;

    public FilterSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        sp = new Spinner(context, Spinner.MODE_DIALOG);
        layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.spinner_layout, this, false);
        selectedValueTextView = layout.findViewById(R.id.spinnerValue);

        if (attrs != null) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                if (attrs.getAttributeName(i).equals("spinnerTitle")) {
                    ((TextView)layout.findViewById(R.id.spinnerTitle)).setText(attrs.getAttributeValue(i));
                    break;
                }
            }
        }
        layout.addView(sp);
        LayoutParams lp = new LinearLayout.LayoutParams(0,0);
        sp.setLayoutParams(lp);
        //sp.setVisibility(GONE);

        this.addView(layout);
        layout.setOnClickListener((v)->{
            if (listener != null) {
                listener.onClick(v);
            } else {
                sp.performClick();
            }
        });
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        listener = l;
    }

    public void setItems(List<SpinnerItem> items) {
        this.items = items;
        this.sp.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, this.items));

        this.sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValueTextView.setText(items.get(position).title);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public String getSelectedValue() {
        if (sp == null) {
            return null;
        }
        return ((SpinnerItem)sp.getSelectedItem()).id;
    }

    public static class SpinnerItem {
        private String id;
        private String title;

        public SpinnerItem(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        @NonNull
        @Override
        public String toString() {
            return getTitle();
        }
    }
}
