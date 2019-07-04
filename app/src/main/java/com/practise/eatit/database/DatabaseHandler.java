package com.practise.eatit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.practise.eatit.model.Order;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ordermanager";
    private static final String TABLE_CARTS = "carts";
    private static final String KEY_ID = "product_id";
    private static final String KEY_NAME = "product_name";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_PRICE = "price";
    private static final String KEY_DISCOUNT = "discount";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CARTS_TABLE = "CREATE TABLE " + TABLE_CARTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_QUANTITY + " TEXT," + KEY_PRICE + " TEXT,"
                + KEY_DISCOUNT + " TEXT" + ")";
        db.execSQL(CREATE_CARTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addCart(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, order.getProductId());
        values.put(KEY_NAME, order.getProductName());
        values.put(KEY_PRICE, order.getPrice());
        values.put(KEY_QUANTITY, order.getQuantity());
        values.put(KEY_DISCOUNT, order.getDiscount());

        // Inserting Row
        db.insert(TABLE_CARTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    // code to get all contacts in a list view
    public List<Order> getAllOrders() {
        List<Order> contactList = new ArrayList<Order>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CARTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setProductId(cursor.getString(0));
                order.setProductName(cursor.getString(1));
                order.setPrice(cursor.getString(2));
                order.setQuantity(cursor.getString(3));
                order.setDiscount(cursor.getString(4));

                // Adding orders to list
                contactList.add(order);
            } while (cursor.moveToNext());
        }

        // return orders list
        return contactList;
    }

    public void deleteCarts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_CARTS);
    }

}
