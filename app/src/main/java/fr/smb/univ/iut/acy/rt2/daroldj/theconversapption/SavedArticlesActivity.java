package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SavedArticlesActivity extends AppCompatActivity {

    Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSavedArticles);
        toolbar.setTitle(R.string.savedArticlesActivity);
        toolbar.collapseActionView();
        setSupportActionBar(toolbar);

        final ListView listView = findViewById(R.id.list_savedArticles);

        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> mapItem;

        Map<String, String> mapSavedArticles = loadMap();
        for (Map.Entry<String,String> entry : mapSavedArticles.entrySet()) {
            mapItem = new HashMap<String, String>();
            mapItem.put("title_article", entry.getValue());
            mapItem.put("link", entry.getKey());
            listItem.add(mapItem);
        }

        SimpleAdapter sAdapter = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.list_articles,
                new String[] {"title_article", "link"}, new int[] {R.id.title_article, R.id.link});

        listView.setItemsCanFocus(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);

                Intent intent_ReadingArticle = new Intent(SavedArticlesActivity.this, ReadingArticleActivity.class);
                intent_ReadingArticle.putExtra("url", map.get("link"));
                (SavedArticlesActivity.this).startActivity(intent_ReadingArticle);
            }
        });

        listView.setAdapter(sAdapter);
        result = DrawerCreator.getDrawer(this, toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        result.setSelection(DrawerCreator.SAVED_ARTICLES_DRAWER_ID);
    }

    private void saveMap(Map<String,String> inputMap){
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("SavedArticles", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.putString("SavedArticles", jsonString);
            editor.commit();
        }
    }

    private Map<String,String> loadMap(){
        Map<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("SavedArticles", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("SavedArticles", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }
}