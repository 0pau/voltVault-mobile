package hu.opau.voltvault;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void login(View v) {
        String email = ((EditText)findViewById(R.id.userEmailET)).getText().toString();
        String password = ((EditText)findViewById(R.id.userPasswordET)).getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            showLoginError("Adja meg a bejelentkezéshez szükséges adatokat");
            return;
        }

        //TODO: Regex check
        doLogin(email, password, false);
    }

    public void doLogin(String email, String password, boolean newUser) {
        LoadingDialog d = new LoadingDialog(this);
        d.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            d.cancel();
            if (task.isSuccessful()) {

                if (newUser) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    HashMap<String, Object> template = new HashMap<>();
                    template.put("billingAddresses", new ArrayList<>());
                    template.put("firstName", "TODO");
                    template.put("lastName", "TODO");
                    firestore.collection("userData").document(firebaseAuth.getCurrentUser().getUid()).set(template);
                    firestore.collection("userFavorites").document(firebaseAuth.getCurrentUser().getUid()).set(new HashMap<>());
                    firestore.collection("userBaskets").document(firebaseAuth.getCurrentUser().getUid()).set(new HashMap<>());
                }

                finish();
            } else {
                showLoginError(task.getException().getMessage());
            }
        });
    }

    public void showLoginError(String message) {
        //TODO: Better dialog!
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Bejelentkezési hiba");
        alert.setMessage(message);
        alert.show();
    }

    public void googleLogin(View v) {

        showLoginError("A Google-bejelentkezés átmenetileg nem elérhető.");

    }

    public void showRegister(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        registerLauncher.launch(i);
    }

    ActivityResultLauncher<Intent> registerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            o -> {
                if (o.getResultCode() == 420 && o.getData() != null) {
                    String email = o.getData().getExtras().getString("email");
                    String password = o.getData().getExtras().getString("password");
                    doLogin(email,password, true);
                }
            }
    );
}