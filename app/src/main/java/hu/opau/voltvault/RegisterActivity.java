package hu.opau.voltvault;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utils.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void startRegister(View v) {
        String userEmail = ((EditText)findViewById(R.id.userEmailET)).getText().toString();
        String userPassword = ((EditText)findViewById(R.id.userPasswordET)).getText().toString();
        String userPassword2 = ((EditText)findViewById(R.id.userPasswordConfirmET)).getText().toString();

        //TODO: Regex!
        if (!userPassword.equals(userPassword2)) {
            return;
        }

        LoadingDialog d = new LoadingDialog(this);
        d.show();
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
            d.cancel();
            if (task.isSuccessful()) {
                Intent data = new Intent();
                data.putExtra("email", userEmail);
                data.putExtra("password", userPassword);
                setResult(420, data);
                finish();
            }
        });

    }
}