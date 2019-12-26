package com.theftfound.ocrscanning.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.theftfound.ocrscanning.Models.Credit;
import com.theftfound.ocrscanning.R;

import java.util.List;

public class AttributionRecyclerViewAdapter extends RecyclerView.Adapter<AttributionRecyclerViewAdapter.ViewHolder> {

    Context context1;
    List<Credit> stringList;

    public AttributionRecyclerViewAdapter(Context context, List<Credit> list) {
        context1 = context;
        this.stringList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView iconNameID;
        public TextView iconmadebyID;
        public TextView creativeCommonID;

        public ViewHolder(View view) {

            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            iconNameID = (TextView) view.findViewById(R.id.iconNameID);
            iconmadebyID = (TextView) view.findViewById(R.id.iconmadebyID);
        }
    }

    @Override
    public AttributionRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view2 = LayoutInflater.from(context1).inflate(R.layout.credits_author_cardview_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view2);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final Credit credit = stringList.get(position);
        viewHolder.iconNameID.setText(credit.getNameIcon());
        viewHolder.iconmadebyID.setText(credit.getIconURL());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(credit.getIconURL()));
                context1.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}
