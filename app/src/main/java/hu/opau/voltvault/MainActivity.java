package hu.opau.voltvault;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
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
        setContentView(R.layout.activity_main);
        instance = this;

        firebaseAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction().add(R.id.content, fragments[0]).commit();

        ((BottomTabBar)findViewById(R.id.bottomTabBar)).setOnTabItemSelectedListener(this::changeScreen);
        ((BottomTabBar)findViewById(R.id.bottomTabBar)).setMenuResource(R.menu.mainmenu);
    }

    private void changeScreen(int index) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragments[index]).commit();
    }

    public void goToUserScreen(View v) {

        if (firebaseAuth.getCurrentUser() == null) {
            goToLogin(v);
        } else {
            //
        }

    }

    public void goToLogin(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void goToBillingAddresses(View v) {
        Intent i = new Intent(this, BillingAddressManagerActivity.class);
        startActivity(i);
    }

    public void logout(View v) {
        firebaseAuth.signOut();
    }


}