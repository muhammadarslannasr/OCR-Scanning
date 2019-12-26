package com.theftfound.ocrscanning.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.theftfound.ocrscanning.Adapters.AttributionRecyclerViewAdapter;
import com.theftfound.ocrscanning.Models.Credit;
import com.theftfound.ocrscanning.R;

import java.util.ArrayList;
import java.util.List;

public class AttributionActivity extends AppCompatActivity {
    private RecyclerView credit_authorRecyclerViewID;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    private AttributionRecyclerViewAdapter attributionRecyclerViewAdapter;
    private List<Credit> creditList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribution);

        getSupportActionBar().setTitle("Attribution");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        credit_authorRecyclerViewID = findViewById(R.id.credit_authorRecyclerViewID);
        creditList = new ArrayList<>();

        creditList.add(new Credit("Home","https://icons8.com/icons/set/home"));
        creditList.add(new Credit("History Unselected","https://icons8.com/icons/set/time-machine"));
        creditList.add(new Credit("Calendar icon","https://icons8.com/icons/set/calendar"));
        creditList.add(new Credit("History Selected","https://icons8.com/icons/set/time-machine"));
        creditList.add(new Credit("Settings Unselected","https://icons8.com/icons/set/settings"));
        creditList.add(new Credit("Settings Selected","https://icons8.com/icons/set/settings"));
        creditList.add(new Credit("Reference Unselected","https://icons8.com/icons/set/recurring-appointment-exception"));
        creditList.add(new Credit("Reference Selected","https://icons8.com/icons/set/recurring-appointment-exception"));
        creditList.add(new Credit("Email Icon for history display","https://icons8.com/icons/set/email"));
        creditList.add(new Credit("Rate Us UnSelected","https://icons8.com/icons/set/five-of-five-stars"));
        creditList.add(new Credit("Rate Us Selected","https://icons8.com/icons/set/five-of-five-stars"));
        creditList.add(new Credit("Recording","https://icons8.com/icons/set/record"));
        creditList.add(new Credit("PayPal ic for history","https://icons8.com/icons/set/paypal"));
        creditList.add(new Credit("Play","https://icons8.com/icons/set/play"));
        creditList.add(new Credit("Pause","https://icons8.com/icons/set/pause"));
        creditList.add(new Credit("Speaker for History","https://icons8.com/icons/set/speaker"));
        creditList.add(new Credit("Share icon for History","https://icons8.com/icons/set/share"));
        creditList.add(new Credit("Delete icon for History","https://icons8.com/icons/set/delete-sign"));

        recyclerViewLayoutManager = new GridLayoutManager(AttributionActivity.this, 1);

        credit_authorRecyclerViewID.setLayoutManager(recyclerViewLayoutManager);

        attributionRecyclerViewAdapter = new AttributionRecyclerViewAdapter(AttributionActivity.this, creditList);

        credit_authorRecyclerViewID.setAdapter(attributionRecyclerViewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
