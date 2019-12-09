package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.irfaan008.irbottomnavigation.SpaceItem;
import com.irfaan008.irbottomnavigation.SpaceNavigationView;
import com.irfaan008.irbottomnavigation.SpaceOnClickListener;
import com.irfaan008.irbottomnavigation.SpaceOnLongClickListener;

public class MainActivity extends AppCompatActivity {
    SpaceNavigationView spaceNavigationView;
    TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.containerEvents);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = findViewById(R.id.tabsEvents);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.setCentreButtonIcon(R.drawable.ic_home_black_24dp);
        spaceNavigationView.addSpaceItem(new SpaceItem("History", R.drawable.ic_history_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("Setting", R.drawable.ic_settings_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("Credit", R.drawable.ic_credit_card_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("Rate Us", R.drawable.ic_rate_review_black_24dp));
        spaceNavigationView.showIconOnly();
        spaceNavigationView.setSpaceBackgroundColor(ContextCompat.getColor(this, R.color.yourColor));
        spaceNavigationView.setCentreButtonColor(ContextCompat.getColor(this, R.color.colorPrimary));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(MainActivity.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
                Toast.makeText(MainActivity.this,"onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return new ScanFragment();
                case 1:
                    return new PDFFragment();
                case 2:
                    return new GoogleMapsFragment();
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            // Show 7 total pages.
            return 3;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }
}
