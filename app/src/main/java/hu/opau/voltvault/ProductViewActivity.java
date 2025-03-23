package hu.opau.voltvault;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.AggregateField;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import hu.opau.voltvault.models.Product;
import hu.opau.voltvault.models.ProductReview;

public class ProductViewActivity extends AppCompatActivity {

    private String id;
    private Product p;
    private int basketQuantity = 1;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        firestore = FirebaseFirestore.getInstance();
        id = getIntent().getStringExtra("id");

        firestore.collection("products").document(id).get().addOnCompleteListener(e->{

            findViewById(R.id.progressBar2).setVisibility(GONE);
            findViewById(R.id.scroller).setVisibility(View.VISIBLE);

            getReviews();

            if (e.isSuccessful()) {
                p = e.getResult().toObject(Product.class);
            }

            ((ImageView)findViewById(R.id.imageView4)).setImageBitmap(Utils.convertBase64(p.getImage()));
            ((TextView)findViewById(R.id.productName)).setText(p.getName());
            ((TextView)findViewById(R.id.manufacturer)).setText(p.getManufacturer());
            ((TextView)findViewById(R.id.productPrice)).setText(String.format("%,d", p.getPrice()).replace(",", " ")+" Ft");
            ((TextView)findViewById(R.id.productDesc)).setText(p.getDescription());

            TableLayout tl = findViewById(R.id.specTable);

            if (p.getProperties() == null) {
                return;
            }

            for (var i : p.getProperties().values()) {
                TableRow tr = new TableRow(this);

                TextView n = new TextView(this);
                n.setText(i.get("name"));

                TextView v = new TextView(this);
                v.setText(i.get("value")+" "+i.get("metric"));

                tr.addView(n);
                tr.addView(v);

                tl.addView(tr);
            }
            tl.forceLayout();
        });
    }

    public void back(View view) {
        finish();
    }

    public void incrementQuantity(View v) {
        basketQuantity++;
        ((TextView)findViewById(R.id.quantity)).setText(Integer.toString(basketQuantity));
    }

    public void decrementQuantity(View v) {
        if (basketQuantity > 1) {
            basketQuantity--;
            ((TextView)findViewById(R.id.quantity)).setText(Integer.toString(basketQuantity));
        }
    }

    public void getReviews() {
        firestore.collection("productReviews").whereEqualTo("product", id).count().get(AggregateSource.SERVER).addOnCompleteListener(e->{
            int reviewCount = (int) e.getResult().getCount();
            ((TextView)findViewById(R.id.reviewCount)).setText(reviewCount+" vélemény");
            if (reviewCount < 6) {
                findViewById(R.id.showAllReviewsBtn).setVisibility(GONE);
            }
        });
        firestore.collection("productReviews").whereEqualTo("product", id).aggregate(AggregateField.average("value")).get(AggregateSource.SERVER).addOnCompleteListener(e->{
            try {
                double avg = e.getResult().getDouble(AggregateField.average("value"));
                ((TextView) findViewById(R.id.averageRating)).setText(Double.toString(avg));
            } catch (Exception ex) {
                ((TextView) findViewById(R.id.averageRating)).setText("?");
            }
        });
        firestore.collection("productReviews").whereEqualTo("product", id).limit(5).get().addOnCompleteListener(e->{
            List<ProductReview> data = e.getResult().toObjects(ProductReview.class);

            if (!data.isEmpty()) {
                findViewById(R.id.noReviewsTextView).setVisibility(GONE);
                findViewById(R.id.reviewLayout).setVisibility(View.VISIBLE);
                RecyclerView rv = findViewById(R.id.reviewRecycler);
                rv.setLayoutManager(new LinearLayoutManager(this));
                rv.setAdapter(new ReviewAdapter(data));
            }
        });
    }

    public static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

        List<ProductReview> data;

        public ReviewAdapter(List<ProductReview> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProductReview review = data.get(position);
            ((TextView)holder.itemView.findViewById(R.id.name)).setText(review.getName());
            ((TextView)holder.itemView.findViewById(R.id.rating)).setText(Float.toString(review.getValue()));
            ((TextView)holder.itemView.findViewById(R.id.ratingText)).setText(review.getText());
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

    }


}