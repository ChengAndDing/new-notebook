package com.example.chenghao.newnotebook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenghao on 2017/11/8.
 */

public class DomainSearchMode extends Activity {

    String domain_search = "a";
    private WordDatabaseAssist words_db_helper;
    private ListView lv_fm_words_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.domain_list);

        words_db_helper = new WordDatabaseAssist(this);
        lv_fm_words_list = (ListView) findViewById(R.id.dm_list);

        //receive the domain you want to search from the MainActivity
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        domain_search = bundle.getString("domain");

        //search the domain from database and display the result of search
          setWordsListView(getDomainSearch(domain_search));
    }

    //search function of the domain
    public List<Map<String,String>> getDomainSearch(String domain) {

        List<Map<String, String>> items = new LinkedList<>();
        SQLiteDatabase sqldb = words_db_helper.getReadableDatabase();
        String command_search = "SELECT * FROM words where domain = ?";
        Cursor c = sqldb.rawQuery(command_search,new String[] {domain});

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

    public void setWordsListView(List<Map<String,String>> items){
        SimpleAdapter adapter;
        //定义单词列表竖屏时的显示方式
        adapter = new SimpleAdapter(this,items,R.layout.words_display_port,new String[]{
                WordDatabaseInfo.Word.COLUMN_NAME_ENG,
                WordDatabaseInfo.Word.COLUMN_NAME_FRE,
                WordDatabaseInfo.Word.COLUMN_NAME_DOMAIN   },
                new int[]{R.id.tv_fm_words_details_eng,R.id.tv_fm_words_details_fre,R.id.tv_fm_words_details_domain});
        lv_fm_words_list.setAdapter(adapter);
    }
}
