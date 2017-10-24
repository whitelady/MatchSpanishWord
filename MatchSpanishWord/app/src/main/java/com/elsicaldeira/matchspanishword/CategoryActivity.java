package com.elsicaldeira.matchspanishword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton category1;
    ImageButton category2;
    ImageButton category3;

    ArrayList<Palabra> rowItems;
    ProgressDialog progressDialog;
    String content;

    String OBJETOS_TAG = "objetos";
    String OBJETO_TAG = "objeto";
    String ID_TAG = "id";
    String TEXTO_TAG = "texto";
    String IMAGEN_TAG = "imagen";
    int resourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) // Habilitar up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category1 = (ImageButton)findViewById(R.id.category1);
        category2 = (ImageButton)findViewById(R.id.category2);
        category3 = (ImageButton)findViewById(R.id.category3);
        category1.setImageResource(R.drawable.animales_perro);
        category2.setImageResource(R.drawable.fruta_banana);
        category3.setImageResource(R.drawable.appliances_cafetera);
        category1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        category2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        category3.setScaleType(ImageView.ScaleType.FIT_CENTER);

        category1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                resourceId = getResources().getIdentifier("animals", "raw", getPackageName());
                openJSON(resourceId);
            }
        });

        category2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                resourceId = getResources().getIdentifier("fruits", "raw", getPackageName());
                openJSON(resourceId);
            }
        });

        category3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                resourceId = getResources().getIdentifier("appliances", "raw", getPackageName());
                openJSON(resourceId);
            }
        });
    }

    private void openJSON(int id) {
        progressDialog = new ProgressDialog(CategoryActivity.this,R.style.AppTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog = ProgressDialog.show(CategoryActivity.this, "", "");
        try
        {
            InputStream fileObj =
                    getResources().openRawResource(id);

            content = readStream(fileObj);
            readJSON(content);
            fileObj.close();
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            // Create a Bundle and Put Bundle in to it
            Bundle bundleObject = new Bundle();
            bundleObject.putSerializable("key", rowItems);
            // Put Bundle in to Intent and call start Activity
            intent.putExtras(bundleObject);
            startActivity(intent);
            finish();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
            finish();
        }
    }

    private void readJSON(String content){
        JSONArray obj = null;
        try {
            JSONObject json = new JSONObject(content);
            String objeto = json.getString(OBJETOS_TAG);
            Log.i("readJSON", objeto);
            JSONObject pal = json.getJSONObject(OBJETOS_TAG);
            obj = pal.getJSONArray(OBJETO_TAG);
            // looping through All albums
            rowItems = new ArrayList<Palabra>();
            for (int i = 0; i < obj.length(); i++) {
                JSONObject al = obj.getJSONObject(i);
                String id = al.getString(ID_TAG);
                String texto = al.getString(TEXTO_TAG);
                String imageName = al.getString(IMAGEN_TAG);
                Log.d("readJSON", id + " " +texto + " " + imageName);
                // get resource id by image name
                //final int resourceId = resources.getIdentifier(imageName, "drawable", context.getPackageName());

                int resId = getResources().getIdentifier(imageName, "drawable" , getPackageName());
                Palabra items = new Palabra(id,resId,texto);
                rowItems.add(items);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    // progressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_instruction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
