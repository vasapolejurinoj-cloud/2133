package com.example.bogatyrev;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shop.db";
    private static final int SCHEMA = 3; // Увеличил версию для обновления

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    // Items table
    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ITEM_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";

    // Favorites table (новая таблица)
    public static final String TABLE_FAVORITES = "favorites";
    public static final String COLUMN_FAVORITE_ID = "_id";
    public static final String COLUMN_FAVORITE_USER_ID = "user_id";
    public static final String COLUMN_FAVORITE_ITEM_ID = "item_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT);");

        // Create items table (убрал is_favorite)
        db.execSQL("CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_PRICE + " REAL);");

        // Create favorites table
        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FAVORITE_USER_ID + " INTEGER, " +
                COLUMN_FAVORITE_ITEM_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_FAVORITE_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_FAVORITE_ITEM_ID + ") REFERENCES " +
                TABLE_ITEMS + "(" + COLUMN_ITEM_ID + ") ON DELETE CASCADE, " +
                "UNIQUE(" + COLUMN_FAVORITE_USER_ID + ", " + COLUMN_FAVORITE_ITEM_ID + "));");

        // Add sample items
        addSampleItems(db);
    }

    private void addSampleItems(SQLiteDatabase db) {
        String[] names = {"Смартфон Galaxy", "Ноутбук Pro", "Наушники Wireless", "Power Bank", "Смарт-часы"};
        String[] descs = {"Отличный смартфон с большим экраном", "Мощный ноутбук для работы и игр", "Беспроводные наушники с шумоподавлением", "Внешний аккумулятор 20000mAh", "Умные часы с отслеживанием здоровья"};
        double[] prices = {29999, 69999, 4999, 1999, 12999};

        for (int i = 0; i < names.length; i++) {
            db.execSQL("INSERT INTO " + TABLE_ITEMS + " (" + COLUMN_NAME + ", " +
                    COLUMN_DESCRIPTION + ", " + COLUMN_PRICE + ") VALUES ('" +
                    names[i] + "', '" + descs[i] + "', " + prices[i] + ");");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }
}