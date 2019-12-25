package com.theftfound.ocrscanning.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theftfound.ocrscanning.R;

import java.util.Locale;

import static com.theftfound.ocrscanning.Fragments.PDFFragment.ACTION_SCANNING_TEXT;

public class RecordingActivity extends AppCompatActivity {
    private String intentText;
    TextToSpeech t1;
    RelativeLayout pauseBtnID;
    TextView scanningTxt_ID;
    StringBuilder sb;
    int state = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);


        //Get Intent
        intentText = getIntent().getStringExtra(ACTION_SCANNING_TEXT);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        pauseBtnID = (RelativeLayout) findViewById(R.id.pauseBtnID);
        scanningTxt_ID = (TextView) findViewById(R.id.scanningTxt_ID);
        scanningTxt_ID.setText(sb.toString());
        pauseBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        pauseBtnID.setBackground(getResources().getDrawable(R.drawable.ic_play_button_dialog));
                        t1.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null);
                        state = 1;
                    }
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        pauseBtnID.setBackground(getResources().getDrawable(R.drawable.ic_round_pause_button_dialog));
                        if(t1 !=null){
                            t1.stop();
                            t1.shutdown();
                            state = 0;
                        }

                    }
                }

            }
        });
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }
}
