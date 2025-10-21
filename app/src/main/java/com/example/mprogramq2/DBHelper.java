package com.example.mprogramq2;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "product_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_SERIAL = "serial";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_PRICE = "price";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL UNIQUE, "
                + COL_SERIAL + " TEXT, "
                + COL_DESCRIPTION + " TEXT, "
                + COL_PRICE + " REAL"
                + ")";
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // add product
    public long addProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, p.getName());
        cv.put(COL_SERIAL, p.getSerial());
        cv.put(COL_DESCRIPTION, p.getDescription());
        cv.put(COL_PRICE, p.getPrice());
        long id = db.insert(TABLE_PRODUCTS, null, cv);
        db.close();
        return id;
    }

    // update description and/or price by id
    public int updateProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DESCRIPTION, p.getDescription());
        cv.put(COL_PRICE, p.getPrice());
        int rows = db.update(TABLE_PRODUCTS, cv, COL_ID + "=?", new String[]{String.valueOf(p.getId())});
        db.close();
        return rows;
    }

    // remove product by id
    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // get product by id
    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_PRODUCTS, null, COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        Product p = null;
        if (c != null && c.moveToFirst()) {
            p = new Product(
                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_SERIAL)),
                    c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_PRICE))
            );
            c.close();
        }
        db.close();
        return p;
    }

    // return all products
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String select = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COL_NAME + " COLLATE NOCASE";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(select, null);
        if (c.moveToFirst()) {
            do {
                Product p = new Product(
                        c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COL_SERIAL)),
                        c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_PRICE))
                );
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    // return only names (strings)
    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        String select = "SELECT " + COL_NAME + " FROM " + TABLE_PRODUCTS + " ORDER BY " + COL_NAME + " COLLATE NOCASE";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(select, null);
        if (c.moveToFirst()) {
            do {
                names.add(c.getString(c.getColumnIndexOrThrow(COL_NAME)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return names;
    }

    // optional: get product by name
    public Product getProductByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_PRODUCTS, null, COL_NAME + "=?", new String[]{name}, null, null, null);
        Product p = null;
        if (c != null && c.moveToFirst()) {
            p = new Product(
                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_SERIAL)),
                    c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_PRICE))
            );
            c.close();
        }
        db.close();
        return p;
    }
}
