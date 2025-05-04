package hu.opau.voltvault.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.opau.voltvault.FirestoreBatchExecutor;
import hu.opau.voltvault.R;
import hu.opau.voltvault.SearchActivity;
import hu.opau.voltvault.controller.ProductController;
import hu.opau.voltvault.models.Ad;
import hu.opau.voltvault.models.Product;
import hu.opau.voltvault.views.CategoryAdapter;
import hu.opau.voltvault.views.ProductAdapter;

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
        executor
            .add(ProductController.getInstance().query(5, 1, null), e -> {
                List<Product> data = e.getResult().toObjects(Product.class);
                LinearLayoutManager lman = new LinearLayoutManager(getContext());
                lman.setOrientation(LinearLayoutManager.HORIZONTAL);
                ((RecyclerView)layout.findViewById(R.id.newProductsRecycler)).setLayoutManager(lman);
                ((RecyclerView)layout.findViewById(R.id.newProductsRecycler)).setAdapter(
                        new ProductAdapter(data, ProductAdapter.ProductListType.COLUMNS)
                );
            })
            .add(firestore.collection("products").whereNotEqualTo("discount", 0), e->{
                List<Product> data = e.getResult().toObjects(Product.class);
                LinearLayoutManager lman = new LinearLayoutManager(getContext());
                lman.setOrientation(LinearLayoutManager.HORIZONTAL);
                ((RecyclerView)layout.findViewById(R.id.discountedProductsRecycler)).setLayoutManager(lman);
                ((RecyclerView)layout.findViewById(R.id.discountedProductsRecycler)).setAdapter(
                        new ProductAdapter(data, ProductAdapter.ProductListType.COLUMNS)
                );
            })
            .add(firestore.collection("productCategories").limit(10), e->{
                ArrayList<Map<String, String>> data = new ArrayList<>();

                for (var item : e.getResult().getDocuments()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("name", (String) item.get("name"));
                    m.put("id", item.getId());
                    data.add(m);
                }

                LinearLayoutManager lman = new LinearLayoutManager(getContext());
                lman.setOrientation(LinearLayoutManager.HORIZONTAL);
                ((RecyclerView)layout.findViewById(R.id.categories_recycler)).setLayoutManager(lman);
                ((RecyclerView)layout.findViewById(R.id.categories_recycler)).setAdapter(
                        new CategoryAdapter(data)
                );

            })
            .add(firestore.collection("ads").orderBy("text").limit(1), e -> {
                Ad adToShow = e.getResult().toObjects(Ad.class).get(0);
                ((TextView)layout.findViewById(R.id.adTitle)).setText(adToShow.getTitle());
                ((TextView)layout.findViewById(R.id.adText)).setText(adToShow.getText());
                layout.findViewById(R.id.currentAdCard).setOnClickListener((v)->{
                    Intent i = new Intent(this.getContext(), SearchActivity.class);
                    i.putExtra("adCampaignTitle", adToShow.getTitle());
                    i.putExtra("adCampaignConditions", adToShow.getConditionsAsArray());
                    startActivity(i);
                });
            })
            .runBatch(FirestoreBatchExecutor.RunOptions.ABORT_ON_ERROR);
        executor.setOnFirestoreBatchCompleteListener(() -> {
            layout.findViewById(R.id.progressBar3).setVisibility(GONE);
            layout.findViewById(R.id.scrollView2).setVisibility(VISIBLE);
        });
    }

}