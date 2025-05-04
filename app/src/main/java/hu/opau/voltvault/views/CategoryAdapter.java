package hu.opau.voltvault.views;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import hu.opau.voltvault.R;
import hu.opau.voltvault.SearchActivity;
import hu.opau.voltvault.Utils;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    ArrayList<Map<String, String>> data;

    public CategoryAdapter(ArrayList<Map<String, String>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position != data.size()-1) {
            ((RecyclerView.LayoutParams) holder.itemView.getLayoutParams()).setMarginEnd(Utils.dpToPxInt(holder.itemView.getContext(), 15));
        }

        ((TextView)holder.itemView.findViewById(R.id.category_name)).setText(data.get(position).get("name"));
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(), SearchActivity.class);
            i.putExtra("conditions", new String[]{"category == "+data.get(position).get("id")});
            holder.itemView.getContext().startActivity(i);
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
