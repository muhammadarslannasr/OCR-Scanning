package com.theftfound.ocrscanning.DatabaseUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.theftfound.ocrscanning.Models.Product;

import java.util.ArrayList;

public class DatabaseRecordingHelper extends SQLiteOpenHelper {
    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RecordingDatabase";
    private static final String TABLE_PRODUCT = "product";
    private static final String KEY_ID = "id";

    private static final String CREATE_TABLE_INSTRUCTOR = "create table if not exists "
            + TABLE_PRODUCT
            + " (id integer primary key autoincrement,"
            + " product_code varchar(30)," + " product_recording_sound varchar(60)," + " product_sound varchar(60)," + " scan_time varchar(30), " + " scan_date varchar(30));";

    public DatabaseRecordingHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_TABLE_INSTRUCTOR);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);


        onCreate(db);

    }

    public void addProduct(Product product) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_code", product.getProductBarcodeNo());
        values.put("product_recording_sound", product.getSound_storeText());
        values.put("product_sound", product.getSongPath());
        values.put("scan_time", product.getScanTime());
        values.put("scan_date", product.getScanDate());
        db.insert(TABLE_PRODUCT, null, values);
        db.close();

    }


    public ArrayList<Object> getAllProduct() {
        ArrayList<Object> productArrayList = new ArrayList<Object>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " ORDER BY id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProductNo(cursor.getString(0));
                product.setSound_storeText(cursor.getString(1));
                product.setSongPath(cursor.getString(2));
                product.setProductBarcodeNo(cursor.getString(3));
                product.setScanTime(cursor.getString(4));
                product.setScanDate(cursor.getString(5));
                // Adding contact to list
                productArrayList.add(product);
            } while (cursor.moveToNext());
        }

        // return contact list
        return productArrayList;
    }

    //Delete Barcode
    public void deleteBarcode(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();

    }
}
