package com.example.bogatyrev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public DatabaseAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // User methods
    public long registerUser(String email, String password) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_EMAIL, email);
        cv.put(DatabaseHelper.COLUMN_PASSWORD, password);
        return database.insert(DatabaseHelper.TABLE_USERS, null, cv);
    }

    public boolean loginUser(String email, String password) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_USER_ID},
                DatabaseHelper.COLUMN_EMAIL + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                new String[]{email, password}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean userExists(String email) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_USER_ID},
                DatabaseHelper.COLUMN_EMAIL + " = ?",
                new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Item methods
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_ITEMS,
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            items.add(cursorToItem(cursor));
        }
        cursor.close();
        return items;
    }

    public List<Item> getFavoriteItems() {
        List<Item> items = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_ITEMS,
                null,
                DatabaseHelper.COLUMN_IS_FAVORITE + " = 1",
                null, null, null, null);
        while (cursor.moveToNext()) {
            items.add(cursorToItem(cursor));
        }
        cursor.close();
        return items;
    }

    public Item getItem(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_ITEMS,
                null,
                DatabaseHelper.COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
        Item item = null;
        if (cursor.moveToFirst()) {
            item = cursorToItem(cursor);
        }
        cursor.close();
        return item;
    }

    public long addItem(Item item) {
        ContentValues cv = itemToContentValues(item);
        return database.insert(DatabaseHelper.TABLE_ITEMS, null, cv);
    }

    public int updateItem(Item item) {
        ContentValues cv = itemToContentValues(item);
        return database.update(DatabaseHelper.TABLE_ITEMS, cv,
                DatabaseHelper.COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public int deleteItem(long id) {
        return database.delete(DatabaseHelper.TABLE_ITEMS,
                DatabaseHelper.COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int toggleFavorite(long id) {
        Item item = getItem(id);
        if (item != null) {
            item.setFavorite(!item.isFavorite());
            return updateItem(item);
        }
        return 0;
    }

    private Item cursorToItem(Cursor cursor) {
        return new Item(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_FAVORITE)) == 1
        );
    }

    private ContentValues itemToContentValues(Item item) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, item.getName());
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, item.getDescription());
        cv.put(DatabaseHelper.COLUMN_PRICE, item.getPrice());
        cv.put(DatabaseHelper.COLUMN_IS_FAVORITE, item.isFavorite() ? 1 : 0);
        return cv;
    }
}