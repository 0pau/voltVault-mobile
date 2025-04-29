package hu.opau.voltvault.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.opau.voltvault.ProductViewActivity;
import hu.opau.voltvault.R;
import hu.opau.voltvault.Utils;
import hu.opau.voltvault.models.Product;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    List<Product> data;

    public ProductListAdapter(List<Product> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = data.get(position);
        ((TextView)holder.itemView.findViewById(R.id.name)).setText(p.getName());
        ((TextView)holder.itemView.findViewById(R.id.price)).setText(Utils.formatPrice(p.getPrice(), "Ft"));
        try {
            ((ImageView)holder.itemView.findViewById(R.id.image)).setImageBitmap(Utils.convertBase64(p.getImage()));
        } catch (Exception e) {
            ((ImageView)holder.itemView.findViewById(R.id.image)).setImageResource(R.drawable.mono);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(), ProductViewActivity.class);
            i.putExtra("id", p.getId());
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
