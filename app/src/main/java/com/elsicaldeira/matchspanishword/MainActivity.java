package com.elsicaldeira.matchspanishword;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    Button instructions;
    Button playGame;
    String content;
    ArrayList<Palabra> rowItems;
    ProgressDialog progressDialog;

    private final String OBJETOS_TAG = "objetos";
    private final String OBJETO_TAG = "objeto";
    private final String ID_TAG = "id";
    private final String TEXTO_TAG = "texto";
    private final String IMAGEN_TAG = "imagen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playGame = (Button)findViewById(R.id.playBtn);
        instructions = (Button)findViewById(R.id.instrBtn);
        //call the game
        playGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                // Create a Bundle and Put Bundle in to it
                Bundle bundleObject = new Bundle();
                bundleObject.putSerializable("key", rowItems);
                // Put Bundle in to Intent and call start Activity
                intent.putExtras(bundleObject);
                startActivity(intent);
            }
        });
        //call instructions
        instructions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intentInst = new Intent(getApplicationContext(), InstructionActivity.class);
                startActivity(intentInst);
            }
        });
        openJSON();
    }

    private void openJSON() {
        progressDialog = ProgressDialog.show(MainActivity.this, "", "");
        try
        {
            InputStream fileObj =
                    getResources().openRawResource(R.raw.objetos);

            content = readStream(fileObj);
            readJSON(content);
            fileObj.close();
            progressDialog.dismiss();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
