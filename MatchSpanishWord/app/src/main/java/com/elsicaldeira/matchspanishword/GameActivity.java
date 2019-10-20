package com.elsicaldeira.matchspanishword;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;


public class GameActivity extends AppCompatActivity {
    private ArrayList<Palabra> palabraList; //Guarda las palabras
    private ArrayList<Integer> numeros; //control para mostrar las palabras y no repertirlas
    ImageButton tarjeta1;
    ImageButton tarjeta2;
    ImageButton tarjeta3;
    ImageView feed;
    TextView palabraMostrar; //palabra que se muestra
    TextView contadorTxt;
    TextView lifeTxt;
    TextView timeTxt;
    private int cantOportunidad;
    private int contadorObjeto;
    private int totalMatches;
    int usado1;
    int usado2;
    int usado3;
    Palabra palActual;
    ArrayList<String> palabrasArray;
    int ultimaPal;
    int timePalabra;
    int timeJuego;
    int timeCheck;
    private boolean win;
    private boolean life;
    private boolean time;


    //sound
    AudioManager audioManager;

    // Maximumn sound stream.
    private static final int MAX_STREAMS = 5;

    // Stream type.
    private static final int streamType = AudioManager.STREAM_MUSIC;

    private boolean loaded;

    private float volume;
    SoundPool soundPool;
    int CORRECT = 0;
    int WRONG = 0;


    //timer to show palabra
    Handler paltimerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int randomPos;
            paltimerHandler.removeCallbacks(timerRunnable);
            //timerTextView.setText(String.format("%d:%02d", minutes, seconds));
            feed.setImageResource(getResources().getIdentifier("empty", "drawable", getPackageName()));
            if ((cantOportunidad > 0) && (contadorObjeto < totalMatches)) {
                randomPos = getRandomNumber(palabrasArray.size());
                ultimaPal = randomPos;
                palActual = palabraList.get(ultimaPal);
                palabraMostrar.setText(palabrasArray.get(ultimaPal));
                paltimerHandler.postDelayed(this, timePalabra);
            } else if ((cantOportunidad == 0) && (contadorObjeto < totalMatches)) {
                paltimerHandler.removeCallbacks(timerRunnable);
                life = true;
                callFeedback();
            } else if ((cantOportunidad >= 0) && (contadorObjeto == totalMatches)) {
                paltimerHandler.removeCallbacks(timerRunnable);
                win = true;
                callFeedback();
            }
        }
    };

    //timer para jugar el juego
    Handler gametimerHandler = new Handler();
    Runnable gametimerRunnable = new Runnable() {
        @Override
        public void run() {
            gametimerHandler.removeCallbacks(gametimerRunnable);
            if ((timeJuego > 1000) && (cantOportunidad > 0) && (contadorObjeto < totalMatches)) {
                timeJuego = timeJuego-timeCheck;
                timeTxt.setText(getString(R.string.seconds,timeJuego / 1000));
                gametimerHandler.postDelayed(this, timeCheck);
            } else if ((timeJuego == 1000) && (cantOportunidad > 0) && (contadorObjeto < totalMatches)) {
                timeJuego = timeJuego-timeCheck;
                timeTxt.setText(getString(R.string.seconds,timeJuego / 1000));
                gametimerHandler.removeCallbacks(gametimerRunnable);
                time = true;
                callFeedback();
            }  else if ((timeJuego > 0) && (cantOportunidad == 0) && (contadorObjeto < totalMatches)) {
                gametimerHandler.removeCallbacks(gametimerRunnable);
                life = true;
                callFeedback();
            } else if ((timeJuego > 1000) && (cantOportunidad > 0) && (contadorObjeto == totalMatches)) {
                gametimerHandler.removeCallbacks(gametimerRunnable);
                win = true;
                callFeedback();
            }
        }
    };

    //Call the feedback activity
    private void callFeedback() {
        Intent intent;
        intent = new Intent(getApplicationContext(),FeedbackActivity.class);
        intent.putExtra(FeedbackActivity.EXTRA_LIFE, life);
        intent.putExtra(FeedbackActivity.EXTRA_TIME, time);
        intent.putExtra(FeedbackActivity.EXTRA_WIN, win);
        startActivity(intent);
        finish();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initSounds(this.getApplicationContext());
        tarjeta1 = (ImageButton)findViewById(R.id.imagen1);
        tarjeta2 = (ImageButton)findViewById(R.id.imagen2);
        tarjeta3 = (ImageButton)findViewById(R.id.imagen3);
        feed = (ImageView)findViewById(R.id.feed);
        palabraMostrar = (TextView)findViewById(R.id.palabra);
        contadorTxt = (TextView)findViewById(R.id.contador);
        lifeTxt = (TextView)findViewById(R.id.lifeBox);
        timeTxt = (TextView)findViewById(R.id.timeBox);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Get ArrayList Bundle
            palabraList = (ArrayList<Palabra>) getIntent().getSerializableExtra("key");
            if (palabraList.size() > 0) {
                totalMatches = palabraList.size();
            }
            palabrasArray = new ArrayList<>();
            numeros = new ArrayList<>();
            for (int i=0;i<totalMatches;i++) {
                numeros.add(i);
            }
            initGame();
        }
        tarjeta1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (verifyPalabra(tarjeta1.getContentDescription().toString())) {
                    showNextImage(tarjeta1);
                }
            }
        });

        tarjeta2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (verifyPalabra(tarjeta2.getContentDescription().toString())) {
                    showNextImage(tarjeta2);
                }
            }
        });
        tarjeta3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (verifyPalabra(tarjeta3.getContentDescription().toString())) {
                    //mostrar la proxima imagen
                    showNextImage(tarjeta3);
                }
            }
        });

    }

    private void initGame(){
        int randomPos;

        contadorObjeto = 0;
        cantOportunidad = 3;
        timePalabra=1200; //tiempo en miliseconds para mostrar la siguiente palabra
        timeJuego=40000; //tiempo para jugar el juego 40 seg
        timeCheck=1000;
        win = false;
        life = false;
        time = false;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        contadorTxt.setText(getString(R.string.contador,contadorObjeto,totalMatches));
        lifeTxt.setText(String.valueOf(cantOportunidad));
        timeTxt.setText(getString(R.string.seconds, timeJuego/1000));
        randomPos = getRandomNumber(numeros.size());
        usado1 = numeros.get(randomPos);
        numeros.remove(randomPos);
        palActual = palabraList.get(usado1);
        palabrasArray.add(palActual.getName());
        tarjeta1.setImageResource(palActual.getImg());
        tarjeta1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tarjeta1.setContentDescription(palActual.getName());
        randomPos = getRandomNumber(numeros.size());
        usado2 = numeros.get(randomPos);
        numeros.remove(randomPos);
        palActual = palabraList.get(usado2);
        palabrasArray.add(palActual.getName());
        tarjeta2.setImageResource(palActual.getImg());
        tarjeta2.setContentDescription(palActual.getName());
        tarjeta2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        randomPos = getRandomNumber(numeros.size());
        usado3 = numeros.get(randomPos);
        numeros.remove(randomPos);
        palActual = palabraList.get(usado3);
        palabrasArray.add(palActual.getName());
        tarjeta3.setImageResource(palActual.getImg());
        tarjeta3.setContentDescription(palActual.getName());
        tarjeta3.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ultimaPal = getRandomNumber(palabrasArray.size());
        palabraMostrar.setText(palabrasArray.get(ultimaPal));
        //inicia timer para mostrar la palabra
        paltimerHandler.postDelayed(timerRunnable, timePalabra);
        //inicia timer para el juego
        gametimerHandler.postDelayed(gametimerRunnable, timeCheck);
    }


    //getRandom number
    private int getRandomNumber(int num){
        Random r = new Random();
        return r.nextInt(num);
    }

    private boolean verifyPalabra(String pal){
        String palActual;

        palActual = palabraMostrar.getText().toString();

        if (pal.equalsIgnoreCase(palActual)) {
            contadorObjeto++;
            playSound(CORRECT);
            feed.setImageResource(getResources().getIdentifier("correct", "drawable", getPackageName()));
            feed.setScaleType(ImageView.ScaleType.FIT_XY);
            contadorTxt.setText(getString(R.string.contador,contadorObjeto,totalMatches));
            return true;

        } else {
            cantOportunidad--;
            feed.setImageResource(getResources().getIdentifier("wrong", "drawable" , getPackageName()));
            feed.setScaleType(ImageView.ScaleType.FIT_XY);
            playSound(WRONG);
            lifeTxt.setText(String.valueOf(cantOportunidad));
            return false;
        }
    }

    private void showNextImage(ImageButton tarjeta) {
        int palabraCorrecta = ultimaPal;
        int randomPos;
        if ((numeros.size() > 0 ) && (numeros.size() <= totalMatches)){
            randomPos = getRandomNumber(numeros.size());
            usado1 = numeros.get(randomPos);
            numeros.remove(randomPos);
            palActual = palabraList.get(usado1);
            palabrasArray.set(palabraCorrecta,palActual.getName());
            tarjeta.setImageResource(palActual.getImg());
            tarjeta.setScaleType(ImageView.ScaleType.FIT_CENTER);
            tarjeta.setContentDescription(palActual.getName());
        } else {
            palabrasArray.remove(palabraCorrecta);
            tarjeta.setImageResource(getResources().getIdentifier("vacio", "drawable" , getPackageName()));
        }
    }

    //Funciones para manejar el sonido
    /** Populate the SoundPool*/

    public void initSounds(Context context) {
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // Suggests an audio stream whose volume should be changed by
        // the hardware volume controls.
        this.setVolumeControlStream(streamType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
            Log.i("GAME", "initSounds: new sound");
        } else {
            createOldSoundPool();
            Log.i("GAME", "initSounds: old sound");
        }

        // When Sound Pool load complete.
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        // Load sound file (destroy.wav) into SoundPool.
        CORRECT = soundPool.load(this, R.raw.correct,1);

        // Load sound file (gun.wav) into SoundPool.
        WRONG = soundPool.load(this, R.raw.error,1);

    }

    protected void createNewSoundPool(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(5)
                    .build();
        }

    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        soundPool = new SoundPool(5,AudioManager.STREAM_MUSIC,0);
    }



    private void playSound(int soundID) {

       if (loaded) {
           // Current volumn Index of particular stream type.
           float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);

           // Get the maximum volume index for a particular stream type.
           float maxVolumeIndex  = (float) audioManager.getStreamMaxVolume(streamType);

           // Volumn (0 --> 1)
           this.volume = currentVolumeIndex / maxVolumeIndex;
           float leftVolumn = volume;
           float rightVolumn = volume;
           // Play sound of gunfire. Returns the ID of the new stream.
           this.soundPool.play(soundID, leftVolumn, rightVolumn, 1, 0, 1f);
       }
    }

    @Override
    public void onPause() {
        super.onPause();
        paltimerHandler.removeCallbacks(timerRunnable);
        gametimerHandler.removeCallbacks(gametimerRunnable);
        if (soundPool != null){
            soundPool.release();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        paltimerHandler.postDelayed(timerRunnable, timePalabra);
        gametimerHandler.postDelayed(gametimerRunnable, timeCheck);
    }

    @Override
    public void onDestroy(){
        paltimerHandler.removeCallbacks(timerRunnable);
        gametimerHandler.removeCallbacks(gametimerRunnable);
        if (soundPool != null){
            soundPool.release();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_game, menu);
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
