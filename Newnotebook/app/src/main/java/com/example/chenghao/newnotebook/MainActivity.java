package com.example.chenghao.newnotebook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

import static com.example.chenghao.newnotebook.R.layout.search_domain;

public class  MainActivity extends AppCompatActivity {

    private InteractiveInterface interactiveInterface;
    public static WordDatabaseRunner dbOperator;
    private ListView lv_fm_words_list;
    private char init;
    private TextToSpeech textToSpeech;
    private String path;
    public static WordDatabaseAssist words_db_helper;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定添加器
        interactiveInterface = new InteractiveInterface(this);
        //绑定数据库
        dbOperator = WordDatabaseRunner.getDBOperator(this,new WordDatabaseAssist(this));

        words_db_helper = new WordDatabaseAssist(this);

        //绑定列表
        lv_fm_words_list = (ListView) findViewById(R.id.lv_fm_words_list);
//        //设置初始界面的标签
        lv_fm_words_list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "Edit");
                menu.add(0, 1, 0, "Delete");
                menu.add(0, 2, 0,"Read");
            }
        });

        //初始化朗读类
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    //初始化成功
                }else{
                    //初始化失败
                }
            }
        });

        ListView list1 = (ListView) findViewById(R.id.list1);
        String arr1[] = {"all","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.array_item, arr1);
        list1.setAdapter(adapter1);
        setWordsListView(dbOperator.getAll());

        list1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    setWordsListView(dbOperator.getAll());
                }
                else{init = (char)('a'+ position - 1);
                setWordsListView(dbOperator.getNeed(init));
                }
            }
        });
    }

    //create the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }

    //menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            //添加单词
            case R.id.menu_add:
                interactiveInterface.interfaceADD();
                break;
            //考试模式
            case R.id.menu_exam:
                Intent intent = new Intent(MainActivity.this,ExamMode.class);
               // Intent intent = new Intent(MainActivity.this,DomainSearchMode.class);
                startActivity(intent);
                //finish();
                break;
            case R.id.menu_domains_search:
                //interactiveInterface.inflateSearchDomainDialog();
                final View domain_search_view = (MainActivity.this).getLayoutInflater().inflate(search_domain,null);
               final EditText domain_input = (EditText) domain_search_view.findViewById(R.id.sd_input);
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setView(domain_search_view).setNegativeButton("cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){

                    }
                }).setPositiveButton("search", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface,int i){
                       // EditText domain_input = (EditText) search_domain.findViewById(R.id.sd_input);
                        String domain_search = domain_input.getText().toString();
                        Intent intent = new Intent(MainActivity.this,DomainSearchMode.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("domain",domain_search);
                        intent.putExtra("data",bundle);
                        startActivity(intent);
                    }

                }).show();
                break;
            case R.id.menu_import:

//                menu_import();

                verifyStoragePermissions(MainActivity.this);
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("*/*");//文件限制
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent1, 1);
                break;

            case R.id.menu_export:

                verifyStoragePermissions(MainActivity.this);
                File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file_export = new File(exportDir, "export.csv");
                if(file_export.exists()){
                    file_export.delete();
                }

                try {
                    file_export.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file_export));
                    SQLiteDatabase sql_db = words_db_helper.getReadableDatabase();
                    String order = WordDatabaseInfo.Word.COLUMN_NAME_ENG + " ASC";
                    Cursor curCSV = sql_db.query(WordDatabaseInfo.Word.TABLE_NAME, new String[]{WordDatabaseInfo.Word.COLUMN_NAME_ENG, WordDatabaseInfo.Word.COLUMN_NAME_FRE, WordDatabaseInfo.Word.COLUMN_NAME_DOMAIN}, null, null, null, null, order);
                    while(curCSV.moveToNext())
                    {
                        //Which column you want to export you can add over here...
                        String arrStr[] ={curCSV.getString(0), curCSV.getString(1),curCSV.getString(2)};
                        csvWrite.writeNext(arrStr);
                    }
                    Toast toastex = Toast.makeText(MainActivity.this,"Export Successfully",Toast.LENGTH_SHORT);
                    toastex.show();

                    csvWrite.close();
                    curCSV.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String id = String.valueOf(info.id);
        switch (item.getItemId()) {
            case 0:
                String[] oldword = selectedWord(item);
                String old_eng = oldword[0];
                String old_fre = oldword[1];
                String old_domain = oldword[2];
                interactiveInterface.interfaceEDIT(old_eng,old_fre,old_domain);
                //this.setWordsListView(dbOperator.getNeed(init));//刷新界面
                return true;

            case 1:
                //从数据库删除单词
                deleteSelectedWord(item);
                return true;

            case 2:
                //朗读函数
                readWord(item);

            default:

                return super.onContextItemSelected(item);
        }
    }

    //delete selected word
    private void deleteSelectedWord(MenuItem item){
        String str_delete_word;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View view = info.targetView;
        str_delete_word = ((TextView)view.findViewById(R.id.tv_fm_words_details_eng)).getText().toString();
        dbOperator.deleteWords(str_delete_word);
        setWordsListView(dbOperator.getNeed(init));
    }

    //update the word
    private String[] selectedWord(MenuItem item){
        String str_edit_word_eng;
        String str_edit_word_fre;
        String str_edit_word_domain;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View view = info.targetView; //得到选定项目的具体信息
        str_edit_word_eng = ((TextView)view.findViewById(R.id.tv_fm_words_details_eng)).getText().toString();
        str_edit_word_fre = ((TextView)view.findViewById(R.id.tv_fm_words_details_fre)).getText().toString();
        str_edit_word_domain = ((TextView)view.findViewById(R.id.tv_fm_words_details_domain)).getText().toString();
        String[] editword = {str_edit_word_eng,str_edit_word_fre,str_edit_word_domain};
        return editword;
    }

    //display the word list
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

    //read word function
    public void readWord(MenuItem item){
        String word_to_read;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View view = info.targetView; //得到选定项目的具体信息
        word_to_read = ((TextView)view.findViewById(R.id.tv_fm_words_details_eng)).getText().toString();

        if(word_to_read.isEmpty()){
            textToSpeech.speak("nothing", TextToSpeech.QUEUE_ADD,null,"1");
        }
        else{
            textToSpeech.speak(word_to_read,TextToSpeech.QUEUE_ADD,null,"2");
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},//
                null, null, null);
        if (cursor == null) result = contentURI.getPath();
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Toast toast = Toast.makeText(MainActivity.this, " No Data ", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
        path = getRealPathFromURI(MainActivity.this,uri);
        String line = "";
        try {
            FileReader file = new FileReader(path);
            BufferedReader buffer = new BufferedReader(file);
            try {
                while ((line = buffer.readLine()) != null){
                    String[] word = line.split(",");
                    dbOperator.insertWords(word[0],word[1],word[2]);
                }
                Toast toast2 = Toast.makeText(MainActivity.this, " Import Successfully ", Toast.LENGTH_SHORT);
                toast2.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }
    }
}
