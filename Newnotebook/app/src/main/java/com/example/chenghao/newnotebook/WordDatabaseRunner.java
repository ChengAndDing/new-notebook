package com.example.chenghao.newnotebook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenghao on 2017/10/25.
 */

public class WordDatabaseRunner {

    private static WordDatabaseAssist words_db_helper;
    private Context context;
    private static WordDatabaseRunner dbOperator;

    private WordDatabaseRunner(Context context, WordDatabaseAssist words_db_helper){
        this.context = context;
        this.words_db_helper = words_db_helper;
    }

    public static WordDatabaseRunner getDBOperator(Context context, WordDatabaseAssist dbHelper){
        return dbOperator == null?new WordDatabaseRunner(context,dbHelper):dbOperator;
    }

    //insert the new word to database
    public void insertWords(String eng,String fre,String domain){
        Log.d("Debug","insert:"+eng);
        String sql = "insert into words(eng,fre,domain)values(?,?,?)";
        SQLiteDatabase db = words_db_helper.getWritableDatabase();
        db.execSQL(sql,new String[]{eng,fre,domain});
    }

    //delete word from the database
    public void deleteWords(String name){
        String sql = "delete from words where eng = ?";
        SQLiteDatabase db = words_db_helper.getWritableDatabase();
        db.execSQL(sql,new String[]{name});
    }

    //update the word in database
    public void editWords(String eng,String fre,String domain,String old_eng){
        String sql = "update words set eng=?, fre=?, domain=? where eng=?";
        SQLiteDatabase db = words_db_helper.getWritableDatabase();
        db.execSQL(sql, new String[]{eng,fre,domain,old_eng});
    }

/**
 * 从数据库查询到需要的数据，并且封装在List<Map>中 select the required word and then put them in the List<Map>
 */

    public List<Map<String,String>> getNeed(char init) {

        List<Map<String, String>> items = new LinkedList<>();
        SQLiteDatabase sqldb = words_db_helper.getReadableDatabase();
        String order = WordDatabaseInfo.Word.COLUMN_NAME_ENG + " ASC";
        Cursor c = sqldb.query(WordDatabaseInfo.Word.TABLE_NAME, new String[]{WordDatabaseInfo.Word.COLUMN_NAME_ENG, WordDatabaseInfo.Word.COLUMN_NAME_FRE, WordDatabaseInfo.Word.COLUMN_NAME_DOMAIN}, "eng like ?", new String[]{init+"%"}, null, null, order);

        String str_eng = "";
        String str_fre = "";
        String str_domain = "";
        Map<String, String> map_item;
        while (c.moveToNext()) {
            map_item = new HashMap<>();
            str_eng = c.getString(c.getColumnIndex(WordDatabaseInfo.Word.COLUMN_NAME_ENG));
            str_fre = c.getString(c.getColumnIndex(WordDatabaseInfo.Word.COLUMN_NAME_FRE));
            str_domain = c.getString(c.getColumnIndex(WordDatabaseInfo.Word.COLUMN_NAME_DOMAIN));
            map_item.put(WordDatabaseInfo.Word.COLUMN_NAME_ENG, str_eng);
            map_item.put(WordDatabaseInfo.Word.COLUMN_NAME_FRE, str_fre);
            map_item.put(WordDatabaseInfo.Word.COLUMN_NAME_DOMAIN, str_domain);
            items.add(map_item);
        }
        return items;
    }

    //get the all words from database and put them in to the List
    public List<Map<String,String>> getAll(){
        List<Map<String, String>> items = new LinkedList<>();
        SQLiteDatabase sqldb = words_db_helper.getReadableDatabase();
        String order = WordDatabaseInfo.Word.COLUMN_NAME_ENG + " ASC";
        Cursor c = sqldb.query(WordDatabaseInfo.Word.TABLE_NAME, new String[]{WordDatabaseInfo.Word.COLUMN_NAME_ENG, WordDatabaseInfo.Word.COLUMN_NAME_FRE, WordDatabaseInfo.Word.COLUMN_NAME_DOMAIN}, null, null, null, null, order);

        String str_eng = "";
        String str_fre = "";
        String str_domain = "";
        Map<String, String> map_item;
        while (c.moveToNext()) {
            map_item = new HashMap<>();
            str_eng = c.getString(c.getColumnIndex(WordDatabaseInfo.Word.COLUMN_NAME_ENG));
            str_fre = c.getString(c.getColumnIndex(WordDatabaseInfo.Word.COLUMN_NAME_FRE));
            str_domain = c.getString(c.getColumnIndex(WordDatabaseInfo.Word.COLUMN_NAME_DOMAIN));
            map_item.put(WordDatabaseInfo.Word.COLUMN_NAME_ENG, str_eng);
            map_item.put(WordDatabaseInfo.Word.COLUMN_NAME_FRE, str_fre);
            map_item.put(WordDatabaseInfo.Word.COLUMN_NAME_DOMAIN, str_domain);
            items.add(map_item);
        }
        return items;
    }
}

