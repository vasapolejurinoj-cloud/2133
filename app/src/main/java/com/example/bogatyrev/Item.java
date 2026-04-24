package com.example.bogatyrev;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private long id;
    private String name;
    private String description;
    private double price;
    private boolean isFavorite;

    // Конструктор по умолчанию
    public Item() {}

    // Конструктор для создания нового товара (без ID)
    public Item(String name, String description, double price) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isFavorite = false;
    }

    // Конструктор для загрузки из БД (со всеми полями)
    public Item(long id, String name, String description, double price, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isFavorite = isFavorite;
    }

    // Геттеры и сеттеры
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(
                    in.readLong(),
                    in.readString(),
                    in.readString(),
                    in.readDouble(),
                    in.readByte() == 1
            );
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}