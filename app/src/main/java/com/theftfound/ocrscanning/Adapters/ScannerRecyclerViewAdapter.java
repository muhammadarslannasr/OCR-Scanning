package com.theftfound.ocrscanning.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.theftfound.ocrscanning.DatabaseUtils.DatabaseHelper;
import com.theftfound.ocrscanning.Models.Product;
import com.theftfound.ocrscanning.R;

import java.util.ArrayList;

public class ScannerRecyclerViewAdapter extends RecyclerView.Adapter<ScannerRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private static final String KEY_ID = "id";
    private ArrayList<Object> productArrayList;

    public ScannerRecyclerViewAdapter(Context context, ArrayList<Object> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override
    public ScannerRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scanner_layout,parent,false);
        return new ScannerRecyclerViewAdapter.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ScannerRecyclerViewAdapter.ViewHolder holder, int position) {
        Product product = (Product) productArrayList.get(position);
        holder.scanner_txt.setText(product.getProductBarcodeNo());
        holder.time_ID.setText(product.getScanDate()+" "+product.getScanTime());
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView scanner_txt;
        private TextView time_ID;
        private Button shareBtn_ID,deleteBtn_ID;
        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            //Casting Widget's
            time_ID = itemView.findViewById(R.id.time_ID);
            scanner_txt = itemView.findViewById(R.id.scanner_txt);
            shareBtn_ID = itemView.findViewById(R.id.shareBtn_ID);
            deleteBtn_ID = itemView.findViewById(R.id.deleteBtn_ID);
            //RecyclerView setOnClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product product = (Product) productArrayList.get(getAdapterPosition());
                    shareScanData(context,product.getProductBarcodeNo());
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
                                    DatabaseHelper db = new DatabaseHelper(context);
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
        }
    }

    public static void shareScanData(Context context,String text) {
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,text+"");
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
