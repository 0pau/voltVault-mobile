package hu.opau.voltvault;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hu.opau.voltvault.models.Order;

public class MyOrdersActivity extends AppCompatActivity {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        getData();
    }

    public void getData() {
        firestore.collection("orders").whereEqualTo("user", auth.getUid()).get().addOnSuccessListener(e->{
            List<Order> orders = new ArrayList<>();

            for (var itm : e.getDocuments()) {
                Order o = itm.toObject(Order.class);
                if (o == null) {
                    continue;
                }
                o.setId(itm.getId());
                orders.add(o);
            }

            ((RecyclerView)findViewById(R.id.orderRecycler)).setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            ((RecyclerView)findViewById(R.id.orderRecycler)).setAdapter(new OrderItemAdapter(orders));

        });

    }

    public void back(View view) {
        finish();
    }

    public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

        List<Order> data;
        public OrderItemAdapter(List<Order> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String dateFormatted = new SimpleDateFormat("yyyy. MM. dd. HH:mm").format(data.get(position).getDate());

            ((TextView)holder.itemView.findViewById(R.id.date)).setText(dateFormatted);

            String statusText = "Ismeretlen";
            switch (data.get(position).getStatus()) {
                case 0:
                    statusText = "Beérkezett";
                    break;
                case 1:
                    statusText = "Feldolgozás alatt";
                    break;
                case 2:
                    statusText = "Kiszállítás alatt";
                    break;
                case 3:
                    statusText = "Teljesítve";
                    break;
            }
            ((TextView)holder.itemView.findViewById(R.id.status)).setText(statusText);
            ((TextView)holder.itemView.findViewById(R.id.itemCount)).setText(data.get(position).getItems().size() + " termék");
            ((TextView)holder.itemView.findViewById(R.id.price)).setText(Utils.formatPrice(data.get(position).getPrice(), "Ft"));

            holder.itemView.setOnLongClickListener(e->{

                PopupMenu pop = new PopupMenu(holder.itemView.getContext(), holder.itemView);
                pop.getMenu().add("Törlés");
                pop.setOnMenuItemClickListener(item->{
                    deleteOrder(data.get(position).getId());
                    return false;
                });
                pop.show();

                return false;
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

    }

    private void deleteOrder(String id) {
        firestore.document("orders/"+ id).delete();
        getData();
    }
}