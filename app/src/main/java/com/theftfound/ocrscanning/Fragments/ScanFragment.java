package com.theftfound.ocrscanning.Fragments;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.theftfound.ocrscanning.Activities.MainActivity;
import com.theftfound.ocrscanning.DatabaseUtils.DatabaseHelper;
import com.theftfound.ocrscanning.DatabaseUtils.DatabaseRecordingHelper;
import com.theftfound.ocrscanning.Models.Product;
import com.theftfound.ocrscanning.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ScanFragment extends Fragment {

    private CameraSource mCameraSource;
    private TextRecognizer mTextRecognizer;
    private SurfaceView mSurfaceView;
    private TextView mTextView;
    private ImageView imageView_foto;
    String fullText;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    SharedPreferences preferences;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private AlertDialog.Builder alertDialogBuilderTitle;
    private AlertDialog dialogTitle;
    private LayoutInflater inflaterTitle;
    Button saveRecordingBtn_ID;
    Button stopRecordingBtn_ID;
    Button saveNormalBtn_ID;
    Button cancelBtn_ID;
    private MediaRecorder myAudioRecorder;
    String pathSave = "";
    int state = 0;
    TextToSpeech t1;
    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        mSurfaceView = (SurfaceView) view.findViewById(R.id.cameraview);
        mTextView = (TextView) view.findViewById(R.id.button_exit);
        imageView_foto = (ImageView) view.findViewById(R.id.imageView_foto);

//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            startTextRecognizer();
//        } else {
//            askCameraPermission();
//        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 123);
        } else {
            startTextRecognizer();
        }

        imageView_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            mInterstitialAd = newInterstitialAd();
                            if (fullText != null) {
                                if (preferences.getBoolean("switch3", false)) {
                                    showDialogHistory(fullText, getScanTime(), getScanDate());
                                } else if (preferences.getBoolean("switch3", true)) {
                                    showDialogHistory(fullText, getScanTime(), getScanDate());
                                }

                            } else {
                                Toast.makeText(getActivity(), "Scan text is empty!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mInterstitialAd.show();
                } else {
                    if (fullText != null) {
                        if (preferences.getBoolean("switch3", false)) {
                            showDialogHistory(fullText, getScanTime(), getScanDate());
                        } else if (preferences.getBoolean("switch3", true)) {
                            showDialogHistory(fullText, getScanTime(), getScanDate());
                        }

                    } else {
                        Toast.makeText(getActivity(), "Scan text is empty!", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInterstitialAd = newInterstitialAd();
    }

    @Override
    public void onResume() {
        super.onResume();
        preferences = getActivity().getSharedPreferences("PREFS", 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCameraSource.release();
    }

    private void startTextRecognizer() {
        mTextRecognizer = new TextRecognizer.Builder(getActivity()).build();

        if (!mTextRecognizer.isOperational()) {
            Toast.makeText(getActivity(), "Oops ! Not able to start the text recognizer ...", Toast.LENGTH_LONG).show();
        } else {
            mCameraSource = new CameraSource.Builder(getActivity(), mTextRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(15.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            mCameraSource.start(mSurfaceView.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 123);
                        } else {
                            startTextRecognizer();
                        }
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            mTextRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    SparseArray<TextBlock> items = detections.getDetectedItems();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); ++i) {
                        TextBlock item = items.valueAt(i);
                        if (item != null && item.getValue() != null) {
                            stringBuilder.append(item.getValue() + " ");
                        }
                    }

                    fullText = stringBuilder.toString();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            mTextView.setText(fullText);
                        }
                    });

                }
            });
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode != RC_HANDLE_CAMERA_PERM) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            return;
//        }
//
//        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            startTextRecognizer();
//        } else {
//            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If user presses allow
                    //Toast.makeText(OcrCameraScanningActivity.this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                    startAppAgain();
                    startTextRecognizer();
                    Toast.makeText(getActivity(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    //If user presses deny
                    Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void startAppAgain() {
        Intent mStartActivity = new Intent(getActivity(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private void askCameraPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

    }

    public void stopVoice(){
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
    }

    public String getScanTime() {
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return timeFormat.format(new Date());
    }

    public String getScanDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void showDialog(final String scanContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(scanContent)
                .setTitle("Scan Code Result!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                databaseHelper.addProduct(new Product(scanContent, getScanTime(), getScanDate()));
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), "Not Saved", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public InterstitialAd newInterstitialAd() {
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.INTERSTITIAL_ADD_ID));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        return mInterstitialAd;
    }

    private void showDialogNotSavingHistory(final String scanContent, final String currentTime, final String currentDate) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(scanContent)
                .setTitle("Scanned Text");
        final AlertDialog testDialog = builder.create();
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                testDialog.dismiss();  // to dismiss
            }
        });

        builder.show();
    }

    private void showDialogHistory(final String scanContent, String time, String date) {

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.custom_dialog_forward_backward, null);

        final TextView scanningTxt_ID = (TextView) view.findViewById(R.id.scanningTxt_ID);
        RelativeLayout backBtn_ID = (RelativeLayout) view.findViewById(R.id.backBtn_ID);
        final RelativeLayout pauseBtnID = (RelativeLayout) view.findViewById(R.id.pauseBtnID);
        RelativeLayout arrow_forwardID = (RelativeLayout) view.findViewById(R.id.nextBtnID);
        saveRecordingBtn_ID = (Button) view.findViewById(R.id.saveRecordingBtn_ID);
        stopRecordingBtn_ID = (Button) view.findViewById(R.id.stopRecordingBtn_ID);
        saveNormalBtn_ID = (Button) view.findViewById(R.id.saveNormalBtn_ID);
        cancelBtn_ID = (Button) view.findViewById(R.id.cancelBtn_ID);

        scanningTxt_ID.setText(scanContent);

        saveNormalBtn_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Ocr History Management
                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                databaseHelper.addProduct(new Product(scanContent, getScanTime(), getScanDate(), pathSave));
                stopVoice();
                Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_LONG).show();
                //dialog.dismiss();
            }
        });

        cancelBtn_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopVoice();
                dialog.dismiss();
            }
        });

        stopRecordingBtn_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                } catch (RuntimeException stopException) {
                    stopException.printStackTrace();
                }


                stopRecordingBtn_ID.setEnabled(false);
                saveRecordingBtn_ID.setEnabled(true);
                stopVoice();
                showRecordingTitleDialog(scanContent,pathSave);
                Toast.makeText(getActivity(), "Audio recorded successfully", Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }
        });

        saveRecordingBtn_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);

                } else {
                    pathSave = Environment.getExternalStorageDirectory().
                            getAbsolutePath() + "/"
                            + UUID.randomUUID().toString() + "_audio_record.3gp";
                    setupMediaRecorder();
                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    saveRecordingBtn_ID.setEnabled(false);
                    stopRecordingBtn_ID.setEnabled(true);

                    Toast.makeText(getActivity(), "Recording....", Toast.LENGTH_SHORT).show();

//                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
//                    databaseHelper.addProduct(new Product(scanContent, getScanTime(), getScanDate(), pathSave));
                    //Recording History Management


                }


            }
        });


        pauseBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        pauseBtnID.setBackground(getResources().getDrawable(R.drawable.pause_ic));
                        speak(scanContent);
                        state = 1;
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        pauseBtnID.setBackground(getResources().getDrawable(R.drawable.play_ic));
                        if (t1 != null) {
                            t1.stop();
                            t1.shutdown();
                            state = 0;
                        }

                    }
                }
            }
        });

        alertDialogBuilder.setView(view);
        dialog = alertDialogBuilder.create();
        dialog.show();
    }

    public void showRecordingTitleDialog(final String scanContent,final String pathSave){
        alertDialogBuilderTitle = new AlertDialog.Builder(getActivity());
        inflaterTitle = LayoutInflater.from(getActivity());
        View view = inflaterTitle.inflate(R.layout.title_dialog_view, null);

        final EditText etTitle_ID = view.findViewById(R.id.etTitle_ID);
        Button saveBtnID = view.findViewById(R.id.saveBtnID);
        Button cancelBtnID = view.findViewById(R.id.cancelBtnID);

        saveBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseRecordingHelper databaseHelper = new DatabaseRecordingHelper(getActivity());
                databaseHelper.addProduct(new Product(etTitle_ID.getText().toString().trim(), getScanTime(), getScanDate(), pathSave,scanContent));
                Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
                dialogTitle.dismiss();
            }
        });

        cancelBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTitle.dismiss();
            }
        });

        alertDialogBuilderTitle.setView(view);
        dialogTitle = alertDialogBuilderTitle.create();
        dialogTitle.setCancelable(false);
        dialogTitle.show();
    }

    private void setupMediaRecorder() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(pathSave);
    }

    public void speak(final String text) {
        t1 = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

}
