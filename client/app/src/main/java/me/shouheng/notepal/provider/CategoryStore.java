package me.shouheng.notepal.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.model.Category;
import me.urakalee.next2.model.Note;
import me.shouheng.notepal.model.enums.Portrait;
import me.shouheng.notepal.model.enums.ItemStatus;
import me.shouheng.notepal.provider.schema.BaseSchema;
import me.shouheng.notepal.provider.schema.CategorySchema;
import me.shouheng.notepal.provider.schema.NoteSchema;
import me.shouheng.notepal.util.LogUtils;
import me.shouheng.notepal.viewmodel.CategoryViewModel;

/**
 * Created by wangshouheng on 2017/8/19. */
public class CategoryStore extends BaseStore<Category> {

    private static CategoryStore sInstance = null;

    public static CategoryStore getInstance(Context context){
        if (sInstance == null){
            synchronized (CategoryStore.class) {
                if (sInstance == null) {
                    sInstance = new CategoryStore(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }
    
    private CategoryStore(Context context) {
        super(context);
    }

    @Override
    protected void afterDBCreated(SQLiteDatabase db) {}

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void fillModel(Category model, Cursor cursor) {
        model.setName(cursor.getString(cursor.getColumnIndex(CategorySchema.NAME)));
        model.setColor(cursor.getInt(cursor.getColumnIndex(CategorySchema.COLOR)));
        model.setPortrait(Portrait.getPortraitById(cursor.getInt(cursor.getColumnIndex(CategorySchema.PORTRAIT))));
        model.setCategoryOrder(cursor.getInt(cursor.getColumnIndex(CategorySchema.CATEGORY_ORDER)));

        int cntIndex = cursor.getColumnIndex(CategorySchema.COUNT);
        if (cntIndex != -1) model.setCount(cursor.getInt(cntIndex));
    }

    @Override
    protected void fillContentValues(ContentValues values, Category model) {
        values.put(CategorySchema.NAME, model.getName());
        values.put(CategorySchema.COLOR, model.getColor());
        values.put(CategorySchema.PORTRAIT, model.getPortrait().id);
        values.put(CategorySchema.CATEGORY_ORDER, model.getCategoryOrder());
    }

    @Override
    public synchronized List<Category> get(String whereSQL, String orderSQL, ItemStatus status, boolean exclude) {
        Cursor cursor = null;
        List<Category> models;
        SQLiteDatabase database = getWritableDatabase();
        try {
            cursor = database.rawQuery(" SELECT *, " + getNotesCount(status)
                            + " FROM " + tableName
                            + " WHERE " + BaseSchema.USER_ID + " = " + userId
                            + (TextUtils.isEmpty(whereSQL) ? "" : " AND " + whereSQL)
                            + " AND " + BaseSchema.STATUS + " != " + ItemStatus.DELETED.id
                            + (TextUtils.isEmpty(orderSQL) ? "" : " ORDER BY " + orderSQL),
                    new String[]{});
            models = getList(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }
        return models;
    }

    public synchronized void updateOrders(List<Category> categories){
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            int size = categories.size();
            for (int i = 0; i < size; i++){
                database.execSQL(" UPDATE " + tableName +
                        " SET " + CategorySchema.CATEGORY_ORDER + " = " + i +
                        " WHERE " + CategorySchema.CODE + " = " + categories.get(i).getCode());
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDatabase(database);
        }
    }

    /**
     * Get labels of note.
     *
     * @param note the note
     * @return categories of note
     */
    public synchronized List<Category> getCategories(Note note) {
        if (note == null || TextUtils.isEmpty(note.getTags())) return new LinkedList<>();

        /**
         * Get 'In' String SQL. {@Example: '(12321321313, 213213213213, 12313213213)'} */
        String[] codes = note.getTags().split(CategoryViewModel.CATEGORY_SPLIT);
        StringBuilder sb = new StringBuilder(" ( ");
        int len = codes.length;
        for (int i=0; i<len; i++) {
            sb.append(codes[i]);
            if (i != len - 1) sb.append(",");
        }
        sb.append(" ) ");
        LogUtils.d(sb.toString());

        SQLiteDatabase database = getWritableDatabase();

        List<Category> categories;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(" SELECT * "
                            + " FROM " + tableName
                            + " WHERE " + CategorySchema.USER_ID + " = ? "
                            + " AND " + CategorySchema.CODE + " IN " + sb.toString()
                            + " AND " + CategorySchema.STATUS + " = " + ItemStatus.NORMAL.id,
                    new String[]{String.valueOf(userId)});
            categories = getList(cursor);
        } finally {
            closeCursor(cursor);
            closeDatabase(database);
        }

        return categories;
    }

    /**
     * Get the notes count of given category.
     *
     * @param status the status of category
     * @return the count sql of getting notes
     */
    private String getNotesCount(ItemStatus status) {
        return " (SELECT COUNT(*) FROM " + NoteSchema.TABLE_NAME + " AS t1 "
                + " WHERE t1." + NoteSchema.TAGS + " LIKE '%'||" + tableName + "." + CategorySchema.CODE + "||'%'"
                + " AND t1." + CategorySchema.USER_ID + " = " + userId
                + " AND t1." + CategorySchema.STATUS + " = " + (status == null ? ItemStatus.NORMAL.id : status.id) + " ) "
                + " AS " + CategorySchema.COUNT;
    }
}
