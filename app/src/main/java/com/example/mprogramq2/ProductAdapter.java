package com.example.mprogramq2;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
        void onItemLongClick(Product product, View anchor);
    }

    private List<Product> products;
    private final OnItemClickListener listener;

    public ProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void setProducts(List<Product> products){
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Product p = products.get(position);
        holder.name.setText(p.getName());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(p));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(p, v);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name;
        VH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvProductName);
        }
    }
}
