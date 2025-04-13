package hu.opau.voltvault.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import hu.opau.voltvault.FirestoreBatchExecutor;
import hu.opau.voltvault.ProductViewActivity;
import hu.opau.voltvault.R;
import hu.opau.voltvault.Utils;
import hu.opau.voltvault.models.Product;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private View layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(l ->{
            checkLoginState();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_favorites, container, false);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        //checkLoginState();
    }

    private void checkLoginState() {
        int s = View.GONE;
        if (auth.getCurrentUser() == null) {
            s = View.VISIBLE;
        } else {
            loadFavorites();
        }
        layout.findViewById(R.id.favoriteHint).setVisibility(s);
    }

    private void loadFavorites() {
        layout.findViewById(R.id.progressBar4).setVisibility(View.VISIBLE);
        firestore.collection("userFavorites").document(auth.getUid()).get().addOnCompleteListener(e->{
            if (e.isSuccessful()) {
                List<String> d = (List<String>)  e.getResult().get("items");
                if (d == null) {
                    layout.findViewById(R.id.progressBar4).setVisibility(View.GONE);
                    return;
                }

                RecyclerView r = layout.findViewById(R.id.favoriteList);

                List<Product> products = new ArrayList<>();
                FirestoreBatchExecutor fbe = new FirestoreBatchExecutor();

                for (var x : d) {
                    fbe.add(firestore.collection("products").whereEqualTo("id", x), event->{
                        if (event.isSuccessful() && event.getResult().size() == 1) {
                            products.add(event.getResult().toObjects(Product.class).get(0));
                        }
                    });
                }

                fbe.runBatch(FirestoreBatchExecutor.RunOptions.ABORT_ON_ERROR);
                fbe.setOnFirestoreBatchCompleteListener(()->{
                    r.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    r.setAdapter(new FavoriteProductAdapter(products));
                    layout.findViewById(R.id.progressBar4).setVisibility(View.GONE);
                });

                //
            }
        });
    }

    public static class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.ViewHolder> {
        List<Product> data;
        public FavoriteProductAdapter(List<Product> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public FavoriteProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ((TextView)holder.itemView.findViewById(R.id.name)).setText(data.get(position).getName());
            ((TextView)holder.itemView.findViewById(R.id.price)).setText(String.format("%,d", data.get(position).getPrice()).replace(",", " ")+" Ft");
            ((ImageView)holder.itemView.findViewById(R.id.image)).setImageBitmap(Utils.convertBase64(data.get(position).getImage()));
            holder.itemView.setOnClickListener(e->{
                Intent i = new Intent(holder.itemView.getContext(), ProductViewActivity.class);
                i.putExtra("id", data.get(position).getId());
                holder.itemView.getContext().startActivity(i);
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