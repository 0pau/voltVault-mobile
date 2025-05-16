package hu.opau.voltvault;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import hu.opau.voltvault.controller.BasketController;
import hu.opau.voltvault.models.BillingAddress;
import hu.opau.voltvault.models.Order;

public class PlaceOrderActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    BillingAddress selectedBillingAddr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        ((TextView)findViewById(R.id.wholePrice)).setText(Utils.formatPrice(BasketController.getInstance().getWholePrice(), "Ft"));

        LinearLayout item_list = findViewById(R.id.all_items_list);

        for (var basketItem : BasketController.getInstance().getBasketItems()) {
            LinearLayout l = new LinearLayout(this);
            l.setOrientation(LinearLayout.HORIZONTAL);
            TextView itemNameTV = new TextView(this);
            itemNameTV.setText(basketItem.getId());
            itemNameTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            l.addView(itemNameTV);

            TextView itemQPTV = new TextView(this);
            itemQPTV.setText(basketItem.getQuantity() + " × " + Utils.formatPrice(basketItem.getPrice(), "Ft"));
            l.addView(itemQPTV);

            item_list.addView(l);
        }

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        firestore.collection("userData").document(auth.getUid()).get().addOnSuccessListener(e->{
            for (var i: (ArrayList<Map<String, Object>>) e.get("billingAddresses")) {
                if ((Boolean) i.get("primary")) {
                    selectedBillingAddr = new BillingAddress(
                            (String)i.get("name"),
                            (String)i.get("phone"),
                            true,
                            ((Long)i.get("postalCode")).intValue(),
                            (String)i.get("country"),
                            (String)i.get("city"),
                            (String)i.get("address")
                    );
                    setBillingDataCard(selectedBillingAddr);
                    break;
                }
            }
        });

        findViewById(R.id.billingAddressSelector).setOnClickListener(e->{
            Intent i = new Intent(this, BillingAddressManagerActivity.class);
            i.putExtra("selector", true);
            startActivityForResult(i, 999);
        });

    }

    public void setBillingDataCard(BillingAddress item) {
        findViewById(R.id.textView26).setVisibility(GONE);
        findViewById(R.id.billingAddressInfo).setVisibility(VISIBLE);
        ((TextView)findViewById(R.id.name)).setText(item.getName().toString());
        ((TextView)findViewById(R.id.rating)).setText(item.getPhone().toString());

        String address = item.getCity() + "\n" + item.getAddress() + "\n" + item.getPostalCode() + ", " + item.getCountry();
        ((TextView)findViewById(R.id.ratingText2)).setText(address);
    }

    public void back(View v) {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);
        if (resultCode == 999) {
            String[] d = data.getStringArrayExtra("addressInfo");
            selectedBillingAddr = new BillingAddress(
                    d[0],d[1],true, Integer.parseInt(d[2]),d[3],d[4],d[5]
            );
            setBillingDataCard(selectedBillingAddr);
        }
    }

    public void placeOrder(View v) {

        if (!((CheckBox)findViewById(R.id.consent)).isChecked()) {
            Toast.makeText(this, "Kérjük, fogadja el a feltételeket.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedBillingAddr == null) {
            Toast.makeText(this, "Válasszon szállítási címet", Toast.LENGTH_SHORT).show();
            return;
        }

        Order newOrder = new Order(auth.getUid(), BasketController.getInstance().getWholePrice(), BasketController.getInstance().getBasketItems(), selectedBillingAddr);

        LoadingDialog ld = new LoadingDialog(this);
        firestore.collection("orders").add(newOrder).addOnSuccessListener(e->{
            BasketController.getInstance().clearAndCommit();
            ld.cancel();
            Intent i = new Intent(this, OrderSuccess.class);
            startActivity(i);
            finish();
        });
        ld.show();
    }
}