package hu.opau.voltvault.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.PathInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import hu.opau.voltvault.R;
import hu.opau.voltvault.Utils;
import hu.opau.voltvault.WaitForBasketTask;
import hu.opau.voltvault.controller.BasketController;
import hu.opau.voltvault.views.BasketAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BasketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasketFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View layout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BasketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BasketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BasketFragment newInstance(String param1, String param2) {
        BasketFragment fragment = new BasketFragment();
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
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    void refresh() {

        if (FirebaseAuth.getInstance().getUid() == null) {
            layout.findViewById(R.id.progressBar6).setVisibility(GONE);
            layout.findViewById(R.id.basketSummary).setVisibility(GONE);
            layout.findViewById(R.id.basketRecycler).setVisibility(GONE);
            layout.findViewById(R.id.emptyBasketTextView).setVisibility(GONE);
            layout.findViewById(R.id.favoriteHint).setVisibility(VISIBLE);
            return;
        } else {
            layout.findViewById(R.id.favoriteHint).setVisibility(GONE);
        }

        if (!BasketController.getInstance().isViewRefreshNeeded()) {
            return;
        }

        layout.findViewById(R.id.progressBar6).setVisibility(VISIBLE);
        layout.findViewById(R.id.basketSummary).setVisibility(GONE);
        layout.findViewById(R.id.basketRecycler).setVisibility(GONE);
        WaitForBasketTask task = new WaitForBasketTask(result -> {
            if (result == WaitForBasketTask.BasketAsyncResult.TIMEOUT) {
                return;
            }
            ((RecyclerView)layout.findViewById(R.id.basketRecycler)).setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            ((RecyclerView)layout.findViewById(R.id.basketRecycler)).setAdapter(new BasketAdapter(BasketController.getInstance().getBasketItems(), BasketAdapter.ProductListType.LIST));
            ((RecyclerView)layout.findViewById(R.id.basketRecycler)).addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> checkPrice());
            layout.findViewById(R.id.basketRecycler).setVisibility(VISIBLE);
            layout.findViewById(R.id.progressBar6).setVisibility(GONE);

            BasketController.getInstance().setViewRefreshNeeded(false);
            checkPrice();
        });
        task.execute();
        Animation summaryAnim = AnimationUtils.loadAnimation(getContext(), R.anim.basket_summary_anim);
        PathInterpolator interpolator = new PathInterpolator(0.850f, 0.125f, 0.000f, 1.000f);
        summaryAnim.setInterpolator(interpolator);
        layout.findViewById(R.id.basketSummary).startAnimation(summaryAnim);
    }

    void checkPrice() {
        ((TextView)layout.findViewById(R.id.wholePrice)).setText(Utils.formatPrice(BasketController.getInstance().getWholePrice(), "Ft"));
        if (BasketController.getInstance().getBasketItems().size() == 0) {
            layout.findViewById(R.id.basketSummary).setVisibility(GONE);
            layout.findViewById(R.id.emptyBasketTextView).setVisibility(VISIBLE);
        } else {
            layout.findViewById(R.id.basketSummary).setVisibility(VISIBLE);
            layout.findViewById(R.id.emptyBasketTextView).setVisibility(GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_basket, container, false);
        return layout;
    }
}