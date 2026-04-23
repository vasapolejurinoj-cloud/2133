package com.example.bogatyrev;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyText;
    private ItemAdapter adapter;
    private DatabaseAdapter dbAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.favoritesRecyclerView);
        emptyText = view.findViewById(R.id.emptyText);
        dbAdapter = new DatabaseAdapter(requireContext());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ItemAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnFavoriteClickListener((item, position) -> {
            dbAdapter.open();
            dbAdapter.toggleFavorite(item.getId());
            dbAdapter.close();
            loadFavorites();
        });

        adapter.setOnItemClickListener(item -> {
            // Optional: navigate to detail
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        dbAdapter.open();
        List<Item> items = dbAdapter.getFavoriteItems();
        dbAdapter.close();
        adapter.updateItems(items);
        emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }
}