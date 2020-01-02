package com.theftfound.ocrscanning.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.theftfound.ocrscanning.DatabaseUtils.DatabaseRecordingHelper;
import com.theftfound.ocrscanning.Models.Product;
import com.theftfound.ocrscanning.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PDFFragment extends Fragment {
    private ImageView imageView;
    private TextView text_ID;
    private Button ocrScanning_ID;
    private TextView getImage_ID;
    public static int RESULT_LOAD_IMAGE = 1;
    Bitmap bitmap;
    StringBuilder sb;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    TextToSpeech t1;
    int state = 0;
    String pathSave = "";
    ImageView pauseBtnID;
    public static final String ACTION_SCANNING_TEXT = "action_scanning_text";
    private MediaRecorder myAudioRecorder;
    Button saveRecordingBtn_ID;
    Button stopRecordingBtn_ID;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdf, container, false);
        imageView = view.findViewById(R.id.imageView);
        text_ID = view.findViewById(R.id.text_ID);
        ocrScanning_ID = view.findViewById(R.id.ocrScanning_ID);
        getImage_ID = view.findViewById(R.id.getImage_ID);
        pauseBtnID = view.findViewById(R.id.pauseBtnID);

        getImage_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        ocrScanning_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextFromImage();
            }
        });

        t1 = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        return view;
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

    public void getTextFromImage() {
        //Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.hqdefault);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(getActivity(), "Could not get the Text", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            sb = new StringBuilder();
            for (int i = 0; i < items.size(); ++i) {
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }

            text_ID.setText(sb.toString());
            showDialog();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                //text_ID.setText("");
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void showDialog() {

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.custom_dialog_forward_backward, null);

        final TextView scanningTxt_ID = (TextView) view.findViewById(R.id.scanningTxt_ID);
        RelativeLayout backBtn_ID = (RelativeLayout) view.findViewById(R.id.backBtn_ID);
        final RelativeLayout pauseBtnID = (RelativeLayout) view.findViewById(R.id.pauseBtnID);
        RelativeLayout arrow_forwardID = (RelativeLayout) view.findViewById(R.id.nextBtnID);
        saveRecordingBtn_ID = (Button) view.findViewById(R.id.saveRecordingBtn_ID);
        stopRecordingBtn_ID = (Button) view.findViewById(R.id.stopRecordingBtn_ID);

        scanningTxt_ID.setText(sb.toString());

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

                    DatabaseRecordingHelper databaseHelper = new DatabaseRecordingHelper(getActivity());
                    databaseHelper.addProduct(new Product(sb.toString(), getScanTime(), getScanDate(), pathSave));

                }


            }
        });


        pauseBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        pauseBtnID.setBackground(getResources().getDrawable(R.drawable.pause_ic));
                        speak(sb.toString());
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

    private void setupMediaRecorder() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(pathSave);
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If user presses allow
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

                    DatabaseRecordingHelper databaseHelper = new DatabaseRecordingHelper(getActivity());
                    databaseHelper.addProduct(new Product(sb.toString(), getScanTime(), getScanDate(), pathSave));
                } else {
                    //If user presses deny
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


}
