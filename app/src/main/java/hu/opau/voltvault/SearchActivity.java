package hu.opau.voltvault;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import hu.opau.voltvault.adapters.ProductListAdapter;
import hu.opau.voltvault.controller.ProductController;
import hu.opau.voltvault.logic.Condition;
import hu.opau.voltvault.models.Product;
import hu.opau.voltvault.views.FilterSpinner;

public class SearchActivity extends AppCompatActivity {

    private int page = 1;
    private ArrayList<Condition> conditions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getPreferences(MODE_PRIVATE).getString("theme", "system")) {
            case "light":
                setTheme(R.style.Base_Theme_VoltVault_Light);
                break;
            case "dark":
                setTheme(R.style.Base_Theme_VoltVault_Dark);
                break;
        }

        setContentView(R.layout.activity_search);

        if (getIntent().hasExtra("conditions")) {
            for (String s : getIntent().getStringArrayExtra("conditions")) {
                conditions.add(Condition.fromString(s));
            }
            query("");
        }

        if (!getIntent().hasExtra("conditions") && !getIntent().hasExtra("adCampaignTitle")) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            findViewById(R.id.queryEditText).requestFocus();
        }

        if (getIntent().hasExtra("adCampaignTitle")) {
            findViewById(R.id.normalSearchUI).setVisibility(GONE);
            findViewById(R.id.adCampaignTitle).setVisibility(VISIBLE);
            ((TextView)findViewById(R.id.adCampaignTitle)).setText(getIntent().getStringExtra("adCampaignTitle"));
            for (String s : getIntent().getStringArrayExtra("adCampaignConditions")) {
                conditions.add(Condition.fromString(s));
            }
            query("");
        }

        ((EditText)findViewById(R.id.queryEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    findViewById(R.id.textCancelBtn).setVisibility(GONE);
                } else {
                    findViewById(R.id.textCancelBtn).setVisibility(VISIBLE);
                }
            }
        });
        ((EditText)findViewById(R.id.queryEditText)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    findViewById(R.id.queryEditText).clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(findViewById(R.id.queryEditText).getWindowToken(), 0);
                    query(((EditText)findViewById(R.id.queryEditText)).getText().toString());
                }
                return false;
            }
        });

        ((RecyclerView)findViewById(R.id.result_recycler)).setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public void back(View v) {
        finish();
    }

    public void deleteQuery(View v) {
        ((EditText)findViewById(R.id.queryEditText)).setText("");
    }

    public void query(String q) {
        findViewById(R.id.search_layout).setVisibility(GONE);
        findViewById(R.id.progressBar5).setVisibility(VISIBLE);
        q = q.toLowerCase(Locale.ROOT);
        ProductController.getInstance().query(20, page, conditions)
                .whereGreaterThanOrEqualTo("lowercase_name", q)
                .whereLessThanOrEqualTo("lowercase_name",q+"~")
                .get()
                .addOnFailureListener(e->{
                    throw new RuntimeException(e);
                })
                .addOnSuccessListener(e->{
                    List<Product> productList = e.toObjects(Product.class);
                    System.out.println(productList.size());
                    ((RecyclerView)findViewById(R.id.result_recycler)).setAdapter(new ProductListAdapter(productList));
                    findViewById(R.id.search_layout).setVisibility(VISIBLE);
                    findViewById(R.id.progressBar5).setVisibility(GONE);
                });
    }

}