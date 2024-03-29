package com.example.pro1121_gr.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DBhelper extends SQLiteOpenHelper {
    private static final String DatabaseName = "QuickChat";
    private static final int DatabaseVersion = 12;
    private static final String TABLE_USAGE = "usage_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_USAGE_TIME = "usage_time";
    private long startTime = 0;

    private static DBhelper instance;

    private DBhelper(Context context) {
        super(context, DatabaseName, null, DatabaseVersion);
    }


    // "synchronized" để đảm bảo an toàn đa luồng khi tạo đối tượng
    public static synchronized DBhelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBhelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_USAGE = "CREATE TABLE " + TABLE_USAGE +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_USAGE_TIME + " INTEGER" +
                ")";
        sqLiteDatabase.execSQL(CREATE_TABLE_USAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            String dropTable = "drop table if exists " + TABLE_USAGE;
            sqLiteDatabase.execSQL(dropTable);
            onCreate(sqLiteDatabase);
        }
    }


    // Thêm dữ liệu thời gian sử dụng mới
    public void addUsageInfo(String date, long usageTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_USAGE_TIME, usageTime + getUsageTimeToday());

        // Kiểm tra xem có bản ghi nào với ngày tương tự chưa
        Cursor cursor = db.query(TABLE_USAGE, new String[]{COLUMN_ID}, COLUMN_DATE + "=?", new String[]{date}, null, null, null);

        if (cursor.getCount() > 0) {
            // Nếu đã tồn tại bản ghi với ngày tương tự, cập nhật lại giá trị
            db.update(TABLE_USAGE, values, COLUMN_DATE + "=?", new String[]{date});
        } else {
            // Nếu chưa tồn tại, thêm mới
            db.insert(TABLE_USAGE, null, values);
        }

        cursor.close();
    }


    // Lấy thời gian sử dụng của ngày hôm nay
    public long getUsageTimeToday() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_USAGE_TIME + ") FROM " + TABLE_USAGE +
                " WHERE " + COLUMN_DATE + " = ?";
        String[] selectionArgs = new String[]{getCurrentDate()};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        long usageTime = 0;
        if (cursor.moveToFirst()) {
            usageTime = cursor.getLong(0);
        }

        cursor.close();
        return usageTime;
    }

    // Hàm trả về ngày hiện tại dưới định dạng dd/MM/yyyy
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }


    // Bắt đầu sử dụng ứng dụng
    public void startUsageTracking() {
        Calendar calendar = Calendar.getInstance();
        startTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    // Kết thúc sử dụng ứng dụng và cập nhật thời gian vào cơ sở dữ liệu
    public void endUsageTracking() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        long usageTime = (endTime - startTime);

        // Lấy ngày hiện tại
        String currentDate = getCurrentDate();

        // Cập nhật vào cơ sở dữ liệu
        addUsageInfo(currentDate, usageTime);
    }


    public long getUsageTimeForDay(int daysAgo) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy ngày cần kiểm tra
        String targetDate = getTargetDate(daysAgo);

        String query = "SELECT " + COLUMN_USAGE_TIME + " FROM " + TABLE_USAGE +
                " WHERE " + COLUMN_DATE + " = ?";

        String[] selectionArgs = new String[]{targetDate};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        long usageTime = 0;
        if (cursor.moveToFirst()) {
            usageTime = cursor.getLong(0);
        }

        cursor.close();
        return usageTime;
    }

    // Hàm lấy ngày cách đây một số ngày
    public String getTargetDate(int daysAgo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();

        // Giảm số ngày
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo);

        return dateFormat.format(calendar.getTime());
    }


}
