package hu.opau.voltvault.views;

import static android.view.View.GONE;
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.opau.voltvault.ProductViewActivity;
import hu.opau.voltvault.R;
import hu.opau.voltvault.Utils;
import hu.opau.voltvault.controller.BasketController;
import hu.opau.voltvault.controller.ProductController;
import hu.opau.voltvault.models.Product;
import hu.opau.voltvault.models.UserBasketItem;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {

    public enum ProductListType {
        COLUMNS, LIST
    }

    List<UserBasketItem> data;
    private ProductListType listType = ProductListType.COLUMNS;

    public BasketAdapter(List<UserBasketItem> data, ProductListType listType) {
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

        holder.itemView.setVisibility(GONE);
        UserBasketItem bi = data.get(position);

        ((TextView)holder.itemView.findViewById(R.id.quantity)).setText("× " + bi.getQuantity());
        holder.itemView.findViewById(R.id.quantity).setVisibility(VISIBLE);

        ProductController.getInstance().getProductById(bi.getId()).addOnSuccessListener(e->{
            Product p = e.toObject(Product.class);
            if (p.getImage() != null) {
                byte[] decodedString = Base64.decode(p.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ((ImageView) holder.itemView.findViewById(R.id.image)).setImageBitmap(decodedByte);
            }


            if (listType== ProductListType.COLUMNS && position != data.size()-1) {
                ((RecyclerView.LayoutParams) holder.itemView.getLayoutParams()).setMarginEnd(Utils.dpToPxInt(holder.itemView.getContext(), 15));
            }

            ((TextView) holder.itemView.findViewById(R.id.name)).setText(p.getName());

            ((TextView) holder.itemView.findViewById(R.id.price)).setText(String.format("%,d", bi.getPrice()).replace(",", " ") + " Ft");
            holder.itemView.setVisibility(VISIBLE);
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.list_item_appear));
        });


        //holder.itemView.findViewById(R.id.layout).getLayoutParams().width = Resources.getSystem().getDisplayMetrics().widthPixels/2;

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ProductViewActivity.class);
            i.putExtra("id", bi.getId());
            v.getContext().startActivity(i);
        });
        holder.itemView.setOnLongClickListener(v->{
            PopupMenu p = new PopupMenu(holder.itemView.getContext(), holder.itemView);
            p.getMenu().add("Eltávolítás a kosárból");
            p.setOnMenuItemClickListener(e->{
                BasketController.getInstance().deleteItem(bi.getId());
                this.notifyDataSetChanged();
                return false;
            });
            p.show();
            return false;
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
