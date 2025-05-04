package hu.opau.voltvault;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DarkThemeActivity extends AppCompatActivity {

    private String oldTheme = "";
    private String newTheme = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oldTheme = getSharedPreferences("user-prefs", MODE_PRIVATE).getString("theme", "system");
        newTheme = oldTheme;

        switch (oldTheme) {
            case "light":
                setTheme(R.style.Base_Theme_VoltVault_Light);
                break;
            case "dark":
                setTheme(R.style.Base_Theme_VoltVault_Dark);
                break;
        }
        setContentView(R.layout.activity_dark_theme);
        switch (getSharedPreferences("user-prefs", MODE_PRIVATE).getString("theme", "system")) {
            case "system":
                ((RadioButton)findViewById(R.id.useSystem)).setChecked(true);
                break;
            case "light":
                ((RadioButton)findViewById(R.id.light)).setChecked(true);
                break;
            case "dark":
                ((RadioButton)findViewById(R.id.dark)).setChecked(true);
                break;
        }
        findViewById(R.id.useSystem).setOnClickListener(e->setTheme("system"));
        findViewById(R.id.light).setOnClickListener(e->setTheme("light"));
        findViewById(R.id.dark).setOnClickListener(e->setTheme("dark"));
    }

    public void back(View v) {
        finish();
    }

    public void setTheme(String variant) {
        newTheme = variant;
        getSharedPreferences("user-prefs", MODE_PRIVATE).edit().putString("theme", variant).apply();
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}