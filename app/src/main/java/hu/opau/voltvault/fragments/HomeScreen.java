package hu.opau.voltvault.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import hu.opau.voltvault.FirestoreBatchExecutor;
import hu.opau.voltvault.ProductViewActivity;
import hu.opau.voltvault.R;
import hu.opau.voltvault.Utils;
import hu.opau.voltvault.controller.ProductController;
import hu.opau.voltvault.logic.Condition;
import hu.opau.voltvault.models.Product;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseFirestore firestore;
    private View layout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeScreen newInstance(String param1, String param2) {
        HomeScreen fragment = new HomeScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firestore = FirebaseFirestore.getInstance();
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_home, container, false);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void refresh() {
        FirestoreBatchExecutor executor = new FirestoreBatchExecutor();
        executor.add(ProductController.getInstance().query(5, 1, new Condition[]{}), e -> {
            List<Product> data = e.getResult().toObjects(Product.class);
            LinearLayoutManager lman = new LinearLayoutManager(getContext());
            lman.setOrientation(LinearLayoutManager.HORIZONTAL);
            ((RecyclerView)layout.findViewById(R.id.newProductsRecycler)).setLayoutManager(lman);
            ((RecyclerView)layout.findViewById(R.id.newProductsRecycler)).setAdapter(
                    new ProductAdapter(data)
            );
        }).runBatch(FirestoreBatchExecutor.RunOptions.ABORT_ON_ERROR);
        executor.setOnFirestoreBatchCompleteListener(() -> {
            layout.findViewById(R.id.progressBar3).setVisibility(GONE);
            layout.findViewById(R.id.scrollView2).setVisibility(VISIBLE);
        });
    }

    public static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        List<Product> data;

        public ProductAdapter(List<Product> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Product p = data.get(position);

            byte[] decodedString = Base64.decode(p.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            ((RecyclerView.LayoutParams)holder.itemView.getLayoutParams()).setMarginEnd(Utils.dpToPxInt(holder.itemView.getContext(), 15));
            //((RecyclerView.LayoutParams)holder.itemView.getLayoutParams()).setMarginEnd(30);

            ((ImageView)holder.itemView.findViewById(R.id.image)).setImageBitmap(decodedByte);
            ((TextView)holder.itemView.findViewById(R.id.name)).setText(p.getName());
            ((TextView)holder.itemView.findViewById(R.id.price)).setText(String.format("%,d", p.getPrice()).replace(",", " ")+" Ft");

            holder.itemView.findViewById(R.id.layout).getLayoutParams().width = Resources.getSystem().getDisplayMetrics().widthPixels/2;

            holder.itemView.setOnClickListener(v->{
                Intent i = new Intent(v.getContext(), ProductViewActivity.class);
                i.putExtra("id", p.getId());
                v.getContext().startActivity(i);
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

    }
}