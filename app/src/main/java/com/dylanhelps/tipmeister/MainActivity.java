package com.dylanhelps.tipmeister;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.*;
import com.google.android.gms.vision.text.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private SurfaceView cameraView;
    private TextView textView;
    private CameraSource cameraSource;
    private final int RequestCameraPermissionID = 1001;
    private String TAG = this.getClass().getName();
    private Button captureBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        captureBtn = (Button) findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askAmount();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView =(TextView) findViewById(R.id.text_view);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {
            setUpCameraSource(textRecognizer);
            List<String> prices = setUpTextRec(textRecognizer);
            String price;
            for (String s : prices){
                // trim the white space out of each element, and break them down to character sequence
                char[] seqs = s.trim().toCharArray();
                for (int i=0; i< seqs.length;i++){
                    // if the character is a decimal point and there are 2 chars after it. e.g. 32.24
                    if(seqs[i]=='.' && i==seqs.length-3){
                        price = seqs.toString();
                    }
                } // end for
            }// end foreach

            //TODO: ask user if the amount is correct
            askAmount();
        }
    }

    private void askAmount() {
        AskAmountDialog askAmountDialog = new AskAmountDialog();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==RequestCameraPermissionID){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (!checkLocationPermission())
                    Log.e(TAG,"Camera permission denied.");
                    return;
                }

                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    Log.e(TAG,e.getMessage());
                }
            }
        }


    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // show explanation or not
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)){

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
            }
            else {
                // no explanation needed. Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
            }
            return false;
        }
        else return true;
    }

    private void setUpCameraSource(TextRecognizer textRecognizer){
        // initialize camera source
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    checkLocationPermission();
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    Log.e(TAG,"Cannot initialize camera source");
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        }); // end addCallback
    }// end setUpCameraSource


    private List<String> setUpTextRec(TextRecognizer textRecognizer){
        final List<String> prices = new ArrayList<>();
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {

                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if(items.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i =0; i<items.size(); ++i) {
                                TextBlock item = items.valueAt(i);
//                                stringBuilder.append(item.getValue());
//                                stringBuilder.append("\n");
                                if (item.getValue().contains("."))
                                    //stringBuilder.append(item.getValue());
                                    prices.add(item.getValue());
                            }
                            textView.setText(stringBuilder.toString());
                        }
                    }); // end new runnable
                }
            }
        });// end setProcessor
        return prices;
    }
}
