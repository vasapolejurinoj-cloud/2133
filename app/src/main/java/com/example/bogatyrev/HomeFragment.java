package com.example.bogatyrev;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyText;
    private FloatingActionButton addButton;
    private ItemAdapter adapter;
    private DatabaseAdapter dbAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupListeners();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.homeRecyclerView);
        emptyText = view.findViewById(R.id.emptyText);
        addButton = view.findViewById(R.id.addButton);
        dbAdapter = new DatabaseAdapter(requireContext());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ItemAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(requireContext(), AddEditItemActivity.class);
            intent.putExtra("item_id", item.getId());
            startActivity(intent);
        });

        adapter.setOnFavoriteClickListener((item, position) -> {
            dbAdapter.open();
            dbAdapter.toggleFavorite(item.getId());
            dbAdapter.close();
            loadItems();
        });
    }

    private void setupListeners() {
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditItemActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        dbAdapter.open();
        List<Item> items = dbAdapter.getAllItems();
        dbAdapter.close();
        adapter.updateItems(items);
        emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }
}