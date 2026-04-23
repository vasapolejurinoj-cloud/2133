package com.example.bogatyrev;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditItemActivity extends AppCompatActivity {
    private TextInputEditText nameInput, descriptionInput, priceInput;
    private Button saveButton, deleteButton;
    private DatabaseAdapter dbAdapter;
    private long itemId = -1;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);
        initViews();
        setupToolbar();
        checkForEdit();
    }

    private void initViews() {
        nameInput = findViewById(R.id.nameInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        priceInput = findViewById(R.id.priceInput);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        dbAdapter = new DatabaseAdapter(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void checkForEdit() {
        itemId = getIntent().getLongExtra("item_id", -1);
        if (itemId != -1) {
            setTitle("Редактировать товар");
            deleteButton.setVisibility(Button.VISIBLE);
            loadItemData();
        } else {
            setTitle("Добавить товар");
            deleteButton.setVisibility(Button.GONE);
        }

        saveButton.setOnClickListener(v -> saveItem());
        deleteButton.setOnClickListener(v -> deleteItem());
    }

    private void loadItemData() {
        dbAdapter.open();
        currentItem = dbAdapter.getItem(itemId);
        dbAdapter.close();

        if (currentItem != null) {
            nameInput.setText(currentItem.getName());
            descriptionInput.setText(currentItem.getDescription());
            priceInput.setText(String.valueOf(currentItem.getPrice()));
        }
    }

    private void saveItem() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Введите название");
            return;
        }
        if (priceStr.isEmpty()) {
            priceInput.setError("Введите цену");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            priceInput.setError("Введите корректную цену");
            return;
        }

        dbAdapter.open();
        if (itemId == -1) {
            Item newItem = new Item(name, description, price);
            long result = dbAdapter.addItem(newItem);
            if (result != -1) {
                Toast.makeText(this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
            }
        } else {
            currentItem.setName(name);
            currentItem.setDescription(description);
            currentItem.setPrice(price);
            int result = dbAdapter.updateItem(currentItem);
            if (result > 0) {
                Toast.makeText(this, "Товар обновлен", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
            }
        }
        dbAdapter.close();
    }

    private void deleteItem() {
        dbAdapter.open();
        int result = dbAdapter.deleteItem(itemId);
        dbAdapter.close();
        if (result > 0) {
            Toast.makeText(this, "Товар удален", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}