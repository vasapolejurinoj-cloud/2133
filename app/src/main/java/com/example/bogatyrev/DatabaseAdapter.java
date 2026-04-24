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
    private SessionManager sessionManager;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context);
        sessionManager = new SessionManager(context);
    }

    public DatabaseAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Получение ID текущего пользователя
    private long getCurrentUserId() {
        String email = sessionManager.getUserEmail();
        if (email.isEmpty()) return -1;

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_USER_ID},
                DatabaseHelper.COLUMN_EMAIL + " = ?",
                new String[]{email}, null, null, null);

        long userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
        }
        cursor.close();
        return userId;
    }

    // User methods
    public long registerUser(String email, String password) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_EMAIL, email);
        cv.put(DatabaseHelper.COLUMN_PASSWORD, password);
        return database.insert(DatabaseHelper.TABLE_USERS, null, cv);
    }

    public long loginUser(String email, String password) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_USER_ID},
                DatabaseHelper.COLUMN_EMAIL + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                new String[]{email, password}, null, null, null);

        long userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
        }
        cursor.close();
        return userId;
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

        long userId = getCurrentUserId();

        while (cursor.moveToNext()) {
            Item item = cursorToItem(cursor);
            // Проверка - тем ли пользователем добавлен товар в избранные
            item.setFavorite(isFavorite(userId, item.getId()));
            items.add(item);
        }
        cursor.close();
        return items;
    }

    public List<Item> getFavoriteItems() {
        List<Item> items = new ArrayList<>();
        long userId = getCurrentUserId();
        if (userId == -1) return items;

        String query = "SELECT i.* FROM " + DatabaseHelper.TABLE_ITEMS + " i " +
                "INNER JOIN " + DatabaseHelper.TABLE_FAVORITES + " f " +
                "ON i." + DatabaseHelper.COLUMN_ITEM_ID + " = f." + DatabaseHelper.COLUMN_FAVORITE_ITEM_ID + " " +
                "WHERE f." + DatabaseHelper.COLUMN_FAVORITE_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            Item item = cursorToItem(cursor);
            item.setFavorite(true);
            items.add(item);
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
            long userId = getCurrentUserId();
            item.setFavorite(isFavorite(userId, id));
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

    // Методы для работы с избранным
    private boolean isFavorite(long userId, long itemId) {
        if (userId == -1) return false;

        Cursor cursor = database.query(DatabaseHelper.TABLE_FAVORITES,
                new String[]{DatabaseHelper.COLUMN_FAVORITE_ID},
                DatabaseHelper.COLUMN_FAVORITE_USER_ID + " = ? AND " +
                        DatabaseHelper.COLUMN_FAVORITE_ITEM_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(itemId)},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean toggleFavorite(long itemId) {
        long userId = getCurrentUserId();
        if (userId == -1) return false;

        if (isFavorite(userId, itemId)) {
            // Удаляем из избранного
            database.delete(DatabaseHelper.TABLE_FAVORITES,
                    DatabaseHelper.COLUMN_FAVORITE_USER_ID + " = ? AND " +
                            DatabaseHelper.COLUMN_FAVORITE_ITEM_ID + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(itemId)});
            return false;
        } else {
            // Добавляем в избранное
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_FAVORITE_USER_ID, userId);
            cv.put(DatabaseHelper.COLUMN_FAVORITE_ITEM_ID, itemId);
            database.insert(DatabaseHelper.TABLE_FAVORITES, null, cv);
            return true;
        }
    }

    private Item cursorToItem(Cursor cursor) {
        return new Item(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE)),
                false // isFavorite будет установлен отдельно
        );
    }

    private ContentValues itemToContentValues(Item item) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, item.getName());
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, item.getDescription());
        cv.put(DatabaseHelper.COLUMN_PRICE, item.getPrice());
        return cv;
    }
}