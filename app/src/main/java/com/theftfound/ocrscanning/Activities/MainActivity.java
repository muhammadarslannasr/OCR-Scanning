package com.theftfound.ocrscanning.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.theftfound.ocrscanning.Fragments.PDFFragment;
import com.theftfound.ocrscanning.Fragments.ScanFragment;
import com.theftfound.ocrscanning.R;
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
    private InterstitialAd mInterstitialHistoryAd;
    private InterstitialAd mInterstitialSettingsAd;
    private InterstitialAd mInterstitialAttributionAd;
    private InterstitialAd mInterstitialRateAppAd;
    int appLaunchCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInterstitialHistoryAd = newInterstitialHiistoryAd();
        mInterstitialSettingsAd = newInterstitialSettingsAd();
        mInterstitialAttributionAd = newInterstitialAttributionAd();
        mInterstitialAttributionAd = newInterstitialAttributionAd();
        mInterstitialRateAppAd = newInterstitialRatingAd();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //check Launch Count
        checkLaunchCount();
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
                //Toast.makeText(MainActivity.this, "onCentreButtonClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    //History
                    if (mInterstitialHistoryAd.isLoaded()) {
                        mInterstitialHistoryAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mInterstitialHistoryAd = newInterstitialHiistoryAd();
                                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                            }
                        });
                        mInterstitialHistoryAd.show();
                    } else {
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                    }

                } else if (itemIndex == 1) {
                    //Settings
                    if (mInterstitialSettingsAd.isLoaded()) {
                        mInterstitialSettingsAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mInterstitialSettingsAd = newInterstitialSettingsAd();
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            }
                        });
                        mInterstitialSettingsAd.show();
                    } else {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    }
                } else if (itemIndex == 2) {
                    //Credit
                    if (mInterstitialAttributionAd.isLoaded()) {
                        mInterstitialAttributionAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mInterstitialAttributionAd = newInterstitialAttributionAd();
                                startActivity(new Intent(MainActivity.this, AttributionActivity.class));
                            }
                        });
                        mInterstitialAttributionAd.show();
                    } else {
                        startActivity(new Intent(MainActivity.this, AttributionActivity.class));
                    }
                } else if (itemIndex == 3) {
                    //Rate It
                    if (mInterstitialRateAppAd.isLoaded()) {
                        mInterstitialRateAppAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mInterstitialRateAppAd = newInterstitialAttributionAd();
                                rateUsOurApp(MainActivity.this);
                            }
                        });
                        mInterstitialRateAppAd.show();
                    } else {
                        rateUsOurApp(MainActivity.this);
                    }
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    //History
                    if (mInterstitialHistoryAd.isLoaded()) {
                        mInterstitialHistoryAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mInterstitialHistoryAd = newInterstitialHiistoryAd();
                                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                            }
                        });
                        mInterstitialHistoryAd.show();
                    } else {
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                    }
                } else if (itemIndex == 1) {
                    //Settings
                    if (mInterstitialSettingsAd.isLoaded()) {
                        mInterstitialSettingsAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mInterstitialSettingsAd = newInterstitialSettingsAd();
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            }
                        });
                        mInterstitialSettingsAd.show();
                    } else {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    }
                } else if (itemIndex == 2) {
                    //Credit
                    if (mInterstitialAttributionAd.isLoaded()) {
                        mInterstitialAttributionAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mInterstitialAttributionAd = newInterstitialAttributionAd();
                                startActivity(new Intent(MainActivity.this, AttributionActivity.class));
                            }
                        });
                        mInterstitialAttributionAd.show();
                    } else {
                        startActivity(new Intent(MainActivity.this, AttributionActivity.class));
                    }
                } else if (itemIndex == 3) {
                    //Rate It
                    if (mInterstitialRateAppAd.isLoaded()) {
                        mInterstitialRateAppAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mInterstitialRateAppAd = newInterstitialAttributionAd();
                                rateUsOurApp(MainActivity.this);
                            }
                        });
                        mInterstitialRateAppAd.show();
                    } else {
                        rateUsOurApp(MainActivity.this);
                    }

                }
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
                Toast.makeText(MainActivity.this, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + "" + itemName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLaunchCount() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        appLaunchCount = pref.getInt("appLaunchCount",-1);
        if(appLaunchCount==2){
            // code to show dialog
            showRatingDialog();
            // reset
            appLaunchCount=0;
        } else {
            // increment count
            appLaunchCount = appLaunchCount+1;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("appLaunchCount", appLaunchCount);
        editor.apply();
    }

    public static void rateUsOurApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new ScanFragment();
                case 1:
                    return new PDFFragment();
//                case 2:
//                    return new GoogleMapsFragment();
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    public InterstitialAd newInterstitialHiistoryAd() {
        mInterstitialHistoryAd = new InterstitialAd(MainActivity.this);
        mInterstitialHistoryAd.setAdUnitId(getResources().getString(R.string.INTERSTITIAL_HISTORY_ID));
        mInterstitialHistoryAd.loadAd(new AdRequest.Builder().build());
        return mInterstitialHistoryAd;
    }

    public InterstitialAd newInterstitialSettingsAd() {
        mInterstitialSettingsAd = new InterstitialAd(MainActivity.this);
        mInterstitialSettingsAd.setAdUnitId(getResources().getString(R.string.INTERSTITIAL_SETTINGS_ID));
        mInterstitialSettingsAd.loadAd(new AdRequest.Builder().build());
        return mInterstitialSettingsAd;
    }

    public InterstitialAd newInterstitialAttributionAd() {
        mInterstitialAttributionAd = new InterstitialAd(MainActivity.this);
        mInterstitialAttributionAd.setAdUnitId(getResources().getString(R.string.INTERSTITIAL_ATTRIBUTION_ID));
        mInterstitialAttributionAd.loadAd(new AdRequest.Builder().build());
        return mInterstitialAttributionAd;
    }

    public InterstitialAd newInterstitialRatingAd() {
        mInterstitialRateAppAd = new InterstitialAd(MainActivity.this);
        mInterstitialRateAppAd.setAdUnitId(getResources().getString(R.string.INTERSTITIAL_RATING_ID));
        mInterstitialRateAppAd.loadAd(new AdRequest.Builder().build());
        return mInterstitialRateAppAd;
    }

    private void showRatingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Please acquired few seconds to rate this app ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String packageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
