package hu.opau.voltvault.views;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.opau.voltvault.ProductViewActivity;
import hu.opau.voltvault.R;
import hu.opau.voltvault.Utils;
import hu.opau.voltvault.fragments.HomeScreen;
import hu.opau.voltvault.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public enum ProductListType {
        COLUMNS, LIST
    }

    List<Product> data;
    private ProductListType listType = ProductListType.COLUMNS;

    public ProductAdapter(List<Product> data, ProductListType listType) {
        this.data = data;
        this.listType = listType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        switch (listType) {
            case COLUMNS:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
                break;
            case LIST:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_list, parent, false);
                break;
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product p = data.get(position);

        byte[] decodedString = Base64.decode(p.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        if (listType == ProductListType.COLUMNS && position != data.size()-1) {
            ((RecyclerView.LayoutParams) holder.itemView.getLayoutParams()).setMarginEnd(Utils.dpToPxInt(holder.itemView.getContext(), 15));
        }

        ((ImageView) holder.itemView.findViewById(R.id.image)).setImageBitmap(decodedByte);
        ((TextView) holder.itemView.findViewById(R.id.name)).setText(p.getName());

        if (p.getDiscount() != 0) {
            holder.itemView.findViewById(R.id.discount).setVisibility(VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.discount)).setText(String.format("%,d", p.getPrice()).replace(",", " ") + " Ft");
            ((TextView) holder.itemView.findViewById(R.id.price)).setText(String.format("%,d", p.getDiscountedPrice()).replace(",", " ") + " Ft");
        } else {
            ((TextView) holder.itemView.findViewById(R.id.price)).setText(String.format("%,d", p.getPrice()).replace(",", " ") + " Ft");
        }


        //holder.itemView.findViewById(R.id.layout).getLayoutParams().width = Resources.getSystem().getDisplayMetrics().widthPixels/2;

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ProductViewActivity.class);
            i.putExtra("id", p.getId());
            v.getContext().startActivity(i);
        });

        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.list_item_appear));
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
