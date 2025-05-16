package hu.opau.voltvault;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hu.opau.voltvault.controller.BasketController;
import hu.opau.voltvault.controller.ProductController;
import hu.opau.voltvault.fragments.AccountFragment;
import hu.opau.voltvault.fragments.BasketFragment;
import hu.opau.voltvault.fragments.FavoritesFragment;
import hu.opau.voltvault.fragments.HomeScreen;
import hu.opau.voltvault.models.Product;
import hu.opau.voltvault.views.BottomTabBar;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;
    private int current = 0;
    private boolean init = false;

    private final Fragment[] fragments = {
            new HomeScreen(),
            new FavoritesFragment(),
            new BasketFragment(),
            new AccountFragment()
    };

    FirebaseAuth firebaseAuth;
    private String oldTheme = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkTheme(this);
        System.out.println(getPreferences(MODE_PRIVATE).getString("theme", "system"));

        if (!Utils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_main);
        instance = this;

        firebaseAuth = FirebaseAuth.getInstance();

        ((BottomTabBar)findViewById(R.id.bottomTabBar)).setOnTabItemSelectedListener(this::changeScreen);
        ((BottomTabBar)findViewById(R.id.bottomTabBar)).setMenuResource(R.menu.mainmenu);

        if (getIntent().hasExtra("screen")) {
            int screen = getIntent().getIntExtra("screen",0);
            ((BottomTabBar)findViewById(R.id.bottomTabBar)).setSelectedIndex(screen);
            changeScreen(screen);
        }

        BasketController.getInstance().refreshBasketFromDatabase();
        new NotificationHandler(this).cancel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 91);
        }
    }

    private void changeScreen(int index) {

        if (!fragments[index].isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragments[index]).commit();
        }

        if (init) {
            getSupportFragmentManager().beginTransaction().hide(fragments[current]).show(fragments[index]).commit();
        } else {
            getSupportFragmentManager().beginTransaction().hide(fragments[current]).show(fragments[index]).commit();
        }

        current = index;

        init = true;
    }

    public void goToUserScreen(View v) {
        goToLogin(v);
    }

    public void goToSearch(View v) {
        Intent i = new Intent(this, SearchActivity.class);
        startActivity(i);
    }

    public void goToLogin(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void goToBillingAddresses(View v) {
        Intent i = new Intent(this, BillingAddressManagerActivity.class);
        startActivity(i);
    }

    public void goToDarkThemePage(View v) {
        Intent i = new Intent(this, DarkThemeActivity.class);
        startActivity(i);
    }

    public void logout(View v) {
        firebaseAuth.signOut();
    }

    public void goToPlaceOrder(View v) {
        Intent i = new Intent(this, PlaceOrderActivity.class);
        startActivity(i);
    }

    public void goToMyOrders(View v) {
        Intent i = new Intent(this, MyOrdersActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String theme = Utils.checkTheme(this);
        if (oldTheme == null) {
            oldTheme = theme;
        }
        if (!oldTheme.equals(theme)) {
            Intent i = getIntent();
            finish();
            startActivity(i);
        }

        getSupportFragmentManager().beginTransaction().hide(fragments[current]).commit();
        getSupportFragmentManager().beginTransaction().show(fragments[current]).commit();

    }

    void setJobScheduler() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(getPackageName(), BasketReminderJob.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, componentName)
                .setOverrideDeadline(4000);
        scheduler.schedule(builder.build());
        BasketController.getInstance().setViewRefreshNeeded(true);
    }

    @Override
    protected void onDestroy() {
        if (!BasketController.getInstance().getBasketItems().isEmpty()) {
            setJobScheduler();
        }
        super.onDestroy();
    }
}