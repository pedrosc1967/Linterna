package com.spc.acrux.linterna;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    // Remove the below line after defining your own ad unit ID.
    //private static final String TOAST_TEXT = "Test ads are being shown. "
    //        + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private static final int START_LEVEL = 1;
    private int mLevel;
    private Button mNextLevelButton;
    private InterstitialAd mInterstitialAd;
    private TextView mLevelTextView;
    private Camera camera;
    //flag to detect flash is on or off
    private boolean isLighOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Mostrar un anuncio
       AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Crear el boton ON / OFF
        Button holaPlayerBtn = (Button)findViewById(R.id.buttononoff);


        Context context = this;
        PackageManager pm = context.getPackageManager();

        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
            return;
        }

        camera = Camera.open();
        try {
            camera.setPreviewTexture(new SurfaceTexture(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Parameters p = camera.getParameters();

        // Create the next level button, which tries to show an interstitial when clicked.
        mNextLevelButton = ((Button) findViewById(R.id.next_level_button));
        mNextLevelButton.setEnabled(false);
        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });

        // Create the text view to show the level number.
        mLevelTextView = (TextView) findViewById(R.id.level);
        mLevel = START_LEVEL;

        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mNextLevelButton.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mNextLevelButton.setEnabled(true);
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel();
            }
        });
        loadInterstitial();

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        //Toast.makeText(this, TOAST_TEXT, LENGTH_LONG).show();

        //Lo que hace el botón "Hola" que se usa para encender y apagar la linterna
        holaPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String TEXTO = getString(R.string.pulsaste);
                    Toast.makeText(MainActivity.this, TEXTO, Toast.LENGTH_LONG).show();

                    //Aqui hay que activar el flash y la cámara
                    if (isLighOn) {

                        Log.i("info", "torch is turn off!");

                        p.setFlashMode(Parameters.FLASH_MODE_OFF);
                        camera.setParameters(p);
                        camera.stopPreview();
                        isLighOn = false;

                    } else {

                        Log.i("info", "torch is turn on!");

                        p.setFlashMode(Parameters.FLASH_MODE_TORCH);

                        camera.setParameters(p);
                        camera.startPreview();
                        isLighOn = true;

                    }

                    }

                   catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.Otras_apps:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:\"Pedro Santangelo\"") ) );
                return true;
            case R.id.salir:
               // FlurryAgent.onEndSession(this);
            {
                if (camera != null) {
                    camera.release();
                }

                this.finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showInterstitial() {
        // Show the ad if it's ready. Oth<string name="otras_apps">Otras Aplicaciones</string>oast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, getString(R.string.AdError), Toast.LENGTH_SHORT).show();
            goToNextLevel();
        }
    }

    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        mNextLevelButton.setEnabled(false);
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.
        mLevelTextView.setText("Level " + (++mLevel));
        loadInterstitial();

    }

    //public void activar() {
    //Activar o desactivar la linterna... no se usa ya.
    //    final String TEXTO = "Pulsaste ON / OFF ";
    //    Toast.makeText(this, TEXTO, LENGTH_LONG).show();

    //}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
        }
    }
/*
    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
        }

    }
*/

}
