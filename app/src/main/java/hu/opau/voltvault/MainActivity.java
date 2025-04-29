package hu.opau.voltvault;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

        System.out.println(getPreferences(MODE_PRIVATE).getString("theme", "system"));

        if (!Utils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_main);
        instance = this;

        firebaseAuth = FirebaseAuth.getInstance();

        //getSupportFragmentManager().beginTransaction().add(R.id.content, fragments[0]).commit();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            for (int i=0; i<fragments.length; i++) {
                if (fragments[i].isVisible()) {
                    current = i;
                    break;
                }
            }
            ((BottomTabBar)findViewById(R.id.bottomTabBar)).setSelectedIndex(current);
        });

        ((BottomTabBar)findViewById(R.id.bottomTabBar)).setOnTabItemSelectedListener(this::changeScreen);
        ((BottomTabBar)findViewById(R.id.bottomTabBar)).setMenuResource(R.menu.mainmenu);
    }

    private void changeScreen(int index) {

        if (!fragments[index].isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragments[index]).commit();
        }

        if (init) {
            getSupportFragmentManager().beginTransaction().hide(fragments[current]).show(fragments[index]).addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction().hide(fragments[current]).show(fragments[index]).commit();
        }

        current = index;

        init = true;
        //getSupportFragmentManager().beginTransaction().replace(R.id.content, fragments[index]).commit();
    }

    public void goToUserScreen(View v) {

        if (firebaseAuth.getCurrentUser() == null) {
            goToLogin(v);
        } else {
            //
        }

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


}