package com.example.bogatyrev;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> items = new ArrayList<>();
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteListener;
    private DecimalFormat df = new DecimalFormat("#.##");

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Item item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteListener = listener;
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, listener, favoriteListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descText, priceText;
        ImageButton favoriteButton;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.productNameText);
            descText = itemView.findViewById(R.id.productDescText);
            priceText = itemView.findViewById(R.id.productPriceText);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }

        void bind(Item item, OnItemClickListener listener, OnFavoriteClickListener favoriteListener) {
            nameText.setText(item.getName());
            descText.setText(item.getDescription());
            priceText.setText(String.format("₽ %.2f", item.getPrice()));
            favoriteButton.setImageResource(item.isFavorite() ?
                    android.R.drawable.btn_star_big_on :
                    android.R.drawable.btn_star_big_off);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });

            favoriteButton.setOnClickListener(v -> {
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(item, getAdapterPosition());
                }
            });
        }
    }
}