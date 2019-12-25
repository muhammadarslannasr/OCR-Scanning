package com.theftfound.ocrscanning.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.theftfound.ocrscanning.DatabaseUtils.DatabaseHelper;
import com.theftfound.ocrscanning.DatabaseUtils.DatabaseRecordingHelper;
import com.theftfound.ocrscanning.Models.Product;
import com.theftfound.ocrscanning.R;

import java.util.ArrayList;
import java.util.Locale;

public class RecordingRecyclerViewAdapter extends RecyclerView.Adapter<RecordingRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private static final String KEY_ID = "id";
    private ArrayList<Object> productArrayList;
    int state = 0;
    TextToSpeech t1;
    public RecordingRecyclerViewAdapter(Context context, ArrayList<Object> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;

        t1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }

    @NonNull
    @Override
    public RecordingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recording_layout,parent,false);
        return new RecordingRecyclerViewAdapter.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingRecyclerViewAdapter.ViewHolder holder, int position) {
        Product product = (Product) productArrayList.get(position);
        holder.scanner_txt.setText(product.getSongPath());
        holder.time_ID.setText(product.getScanDate()+" "+product.getScanTime());
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView scanner_txt;
        private TextView time_ID;
        private Button shareBtn_ID,deleteBtn_ID,pauseBtn_ID;
        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            //Casting Widget's
            time_ID = itemView.findViewById(R.id.time_ID);
            scanner_txt = itemView.findViewById(R.id.scanner_txt);
            shareBtn_ID = itemView.findViewById(R.id.shareBtn_ID);
            deleteBtn_ID = itemView.findViewById(R.id.deleteBtn_ID);
            pauseBtn_ID = itemView.findViewById(R.id.pauseBtn_ID);
            //RecyclerView setOnClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product product = (Product) productArrayList.get(getAdapterPosition());
                    Toast.makeText(context, product.getSongPath()+"", Toast.LENGTH_SHORT).show();
                }
            });

            shareBtn_ID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Product product = (Product) productArrayList.get(getAdapterPosition());
                    shareScanData(context,product.getProductBarcodeNo());
                }
            });

            deleteBtn_ID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    //builder.setTitle("Are you sure you want to delete?");
                    builder.setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Product product = (Product) productArrayList.get(getAdapterPosition());
                                    //delete the item.
                                    DatabaseRecordingHelper db = new DatabaseRecordingHelper(context);
                                    //delete item
                                    db.deleteBarcode(Integer.parseInt(product.getProductNo()));
                                    productArrayList.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertdialog = builder.create();
                    alertdialog.show();


                }
            });

            pauseBtn_ID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = (Product) productArrayList.get(getAdapterPosition());
                    if (state == 0){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            pauseBtn_ID.setBackground(context.getResources().getDrawable(R.drawable.play_ic));
                            speak(product.getProductBarcodeNo());
                            state = 1;
                        }
                    }else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            pauseBtn_ID.setBackground(context.getResources().getDrawable(R.drawable.pause_ic));
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
    }

    public void speak(final String text) {
        //t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        t1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    public static void shareScanData(Context context,String text) {
        String sharePath = Environment.getExternalStorageDirectory().getPath()
                + text;
        Uri uri = Uri.parse(sharePath);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(share, "Share Sound File"));
    }
}
