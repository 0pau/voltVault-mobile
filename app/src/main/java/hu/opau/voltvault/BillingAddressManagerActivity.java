package hu.opau.voltvault;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import hu.opau.voltvault.models.BillingAddress;
import hu.opau.voltvault.models.UserData;

public class BillingAddressManagerActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    BillingAddressAdapter adapter;
    private boolean loadingDone = false;
    private boolean listModified = false;
    private BillingAddressEditorSheet editorSheet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.checkTheme(this);

        setContentView(R.layout.activity_billing_address_manager);

        adapter = new BillingAddressAdapter();
        ((RecyclerView)findViewById(R.id.content)).setLayoutManager(new LinearLayoutManager(this));
        ((RecyclerView)findViewById(R.id.content)).setAdapter(adapter);
        adapter.setOnItemSelectedListener(index->{
            if (!getIntent().getBooleanExtra("selector", false)) {
                showEditSheet(adapter.data.get(index), false);
            } else {
                Intent i = new Intent();
                BillingAddress item = adapter.data.get(index);
                i.putExtra("addressInfo", new String[]{
                   item.getName(),
                   item.getPhone(),
                        Integer.toString(item.getPostalCode()),item.getCountry(),item.getCity(),item.getAddress()
                });
                setResult(999, i);
                finish();
            }
        });
        adapter.setOnItemRemovedListener(()->{
            listModified = true;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
        }
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("userData").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                loadingDone = true;
                if (task.isSuccessful()) {
                    findViewById(R.id.loadingIndicator).setVisibility(GONE);
                    List<BillingAddress> data = task.getResult().toObject(UserData.class).getBillingAddresses();
                    if (data == null) {
                        findViewById(R.id.error).setVisibility(VISIBLE);
                        return;
                    }
                    adapter.setData(data);
                }
            }
        });
    }

    public void showEditSheet(BillingAddress address, boolean isNewItem) {
        editorSheet = new BillingAddressEditorSheet(this, address);
        editorSheet.setOnDismissListener(e->{

            if (!editorSheet.hasResult) {
                return;
            }

            String fullName = ((EditText)editorSheet.findViewById(R.id.fullName)).getText().toString();
            String phone = ((EditText)editorSheet.findViewById(R.id.phone)).getText().toString();
            String country = ((EditText)editorSheet.findViewById(R.id.country)).getText().toString();
            int postalCode = Integer.parseInt(((EditText)editorSheet.findViewById(R.id.postalCode)).getText().toString());
            String city = ((EditText)editorSheet.findViewById(R.id.city)).getText().toString();
            String addr = ((EditText)editorSheet.findViewById(R.id.address)).getText().toString();
            boolean isPrimary = ((CheckBox)editorSheet.findViewById(R.id.setAsPrimary)).isChecked();

            if (fullName.isEmpty()||phone.isEmpty()||country.isEmpty()||postalCode==0||city.isEmpty()||addr.isEmpty()) {
                //TODO: Show warning!
                e.cancel();
                return;
            }

            BillingAddress a = new BillingAddress(fullName, phone, isPrimary, postalCode, country, city, addr);
            int indexToChange = adapter.data.size();
            if (isNewItem) {
                adapter.data.add(a);
                adapter.notifyItemInserted(indexToChange);
            } else {
                indexToChange = adapter.data.indexOf(address);
                adapter.data.set(indexToChange, a);
                adapter.notifyItemChanged(indexToChange);
            }

            listModified = true;

            if (a.isPrimary()) {
                for (int i = 0; i < adapter.data.size(); i++) {
                    if (i != indexToChange) {
                        adapter.data.get(i).setPrimary(false);
                        adapter.notifyItemChanged(i);
                    }
                }
            }
        });
        editorSheet.show();
    }

    public void showAddSheet(View v) {
        showEditSheet(new BillingAddress(), true);
    }

    public void back(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (loadingDone && listModified) {
            firestore.collection("userData").document(firebaseAuth.getUid()).update("billingAddresses", adapter.data).addOnCompleteListener(e -> {
                if (!e.isSuccessful()) {
                    Log.e(getClass().getSimpleName(), e.getException().getMessage());
                }
            });
        }
        super.onDestroy();
    }

    public static class BillingAddressAdapter extends RecyclerView.Adapter<BillingAddressAdapter.ViewHolder> {
        private List<BillingAddress> data = new ArrayList<>();
        private OnItemSelectedListener onItemSelectedListener;
        private OnItemRemovedListener onItemRemovedListener;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.billing_info_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BillingAddress item = data.get(position);
            ((TextView)holder.itemView.findViewById(R.id.name)).setText(item.getName().toString());
            ((TextView)holder.itemView.findViewById(R.id.rating)).setText(item.getPhone().toString());

            String address = item.getCity() + "\n" + item.getAddress() + "\n" + item.getPostalCode() + ", " + item.getCountry();
            ((TextView)holder.itemView.findViewById(R.id.ratingText)).setText(address);
            if (item.isPrimary()) {
                holder.itemView.findViewById(R.id.isPrimaryBadge).setVisibility(VISIBLE);
            }
            holder.itemView.setOnClickListener(e->{
                invokeSelectEvent(position);
            });
            holder.itemView.setOnLongClickListener(e->{
                PopupMenu p = new PopupMenu(holder.itemView.getContext(), holder.itemView);
                p.getMenu().add("Törlés");
                p.getMenu().getItem(0).setOnMenuItemClickListener(i->{
                    if (onItemRemovedListener != null) {
                        onItemRemovedListener.onEvent();
                    }
                    data.remove(position);
                    notifyItemRemoved(position);
                    return true;
                });
                p.show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        public List<BillingAddress> getData() {
            return data;
        }

        public void setData(List<BillingAddress> data) {
            this.data = data;
            Log.i(getClass().getName(), data.size()+"");
            notifyDataSetChanged();
        }

        public void invokeSelectEvent(int index) {
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onEvent(index);
            }
        }

        public interface OnItemSelectedListener {
            void onEvent(int index);
        }

        public interface OnItemRemovedListener {
            void onEvent();
        }

        public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener;
        }

        public void setOnItemRemovedListener(OnItemRemovedListener onItemRemovedListener) {
            this.onItemRemovedListener = onItemRemovedListener;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);

        if (requestCode == 444 && grantResults[0]==0) {
            editorSheet.getLocation();
        }
    }
}