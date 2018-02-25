package com.elsicaldeira.matchspanishword;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class FeedbackActivity extends AppCompatActivity {
    private AdView mAdView;
    public static final String EXTRA_LIFE = "life";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_WIN = "win";

    boolean lifeFeed;
    boolean timeFeed;
    boolean winFeed;
    TextView feedbackText;
    Button playAgain;
    FloatingActionButton shareBtn;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
       mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

        });
        feedbackText = (TextView)findViewById(R.id.feedbackView);
        playAgain = (Button)findViewById(R.id.playagainBtn);
        shareBtn = (FloatingActionButton)findViewById(R.id.share);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lifeFeed = extras.getBoolean(EXTRA_LIFE);
            timeFeed = extras.getBoolean(EXTRA_TIME);
            winFeed = extras.getBoolean(EXTRA_WIN);
           showFeedback();
        }
        //play again
        playAgain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.i("GAME","click en share");
                createShareIntent();
            }
        });
    }

    private void showFeedback(){
        if(lifeFeed) {
            feedbackText.setText(getResources().getString(R.string.feedback_neg_lifes));
        }
        if(winFeed) {
            feedbackText.setText(getResources().getString(R.string.feedback_pos));
        }
        if(timeFeed) {
            feedbackText.setText(getResources().getString(R.string.feedback_neg_time));
        }
    }

    // Create and return the Share Intent
    private void createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String header= getResources().getString(R.string.share_title);

        String shareText = header + getResources().getString(R.string.share_app);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent,""));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
