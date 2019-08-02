package com.aps.qrcode.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.serviceimpl.QRServiceImpl;
import com.aps.qrcode.view.generalqrgen.GeneralQRCreate;
import com.aps.qrcode.view.generalqrgen.GeneralQRGenHistory;
import com.aps.qrcode.view.generalqrscan.GeneralQRCodeScanResult;
import com.aps.qrcode.view.generalqrscan.GeneralQRScanFavorite;
import com.aps.qrcode.view.generalqrscan.GeneralQRScanHistory;
import com.aps.qrcode.view.generalqrscan.ScanGeneralQR;
import com.aps.qrcode.view.secretqrgen.SecretQRCreate;
import com.aps.qrcode.view.secretqrgen.SecretQRGenFavorite;
import com.aps.qrcode.view.secretqrgen.SecretQRGenHistory;
import com.aps.qrcode.view.secretqrscan.ScanSecretQR;
import com.aps.qrcode.view.secretqrscan.SecretQRCodeScanResult;
import com.aps.qrcode.view.secretqrscan.SecretQRScanFavorite;
import com.aps.qrcode.view.secretqrscan.SecretQRScanHistory;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    private int[] tabIcons = {
            R.drawable.ic_action_qr_create_icon_aps,
            R.drawable.ic_action_qr_scan_aps
    };
    private LinearLayout fab1_container, fab2_container, fab3_container;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private boolean isOpen;

    private FloatingActionButton fab;

    private QRServiceImpl qrService;

    private boolean mGeneralQrCode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrService = new QRServiceImpl();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        fab1_container = findViewById(R.id.fab1_container);
        fab2_container = findViewById(R.id.fab2_container);
        fab3_container = findViewById(R.id.fab3_container);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //initiate scan with our custom scan activity

        String scan_request = getIntent().getStringExtra("qr_scan_request");
        if (!TextUtils.isEmpty(scan_request)) {
            new IntentIntegrator(MainActivity.this).setCaptureActivity(ScannerActivity.class).initiateScan();
        }

        fab1_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Toast.makeText(MainActivity.this, "Camera clicked", Toast.LENGTH_LONG).show();
            }
        });
        fab2_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Toast.makeText(MainActivity.this, "Folder clicked", Toast.LENGTH_LONG).show();
            }
        });
        fab3_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Toast.makeText(MainActivity.this, "Folder clicked", Toast.LENGTH_LONG).show();
                // to test commit testing purpose
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // in new pc
        int id = item.getItemId();
        switch (id) {
            case R.id.secret_qr_create:
                startActivity(new Intent(MainActivity.this, SecretQRCreate.class));
                return true;
            case R.id.secret_qr_scan:
                startActivity(new Intent(MainActivity.this, ScanSecretQR.class));
                return true;
            case R.id.general_qr_create:
                startActivity(new Intent(MainActivity.this, GeneralQRCreate.class));
                return true;
            case R.id.general_qr_scan:
                startActivity(new Intent(MainActivity.this, ScanGeneralQR.class));
            case R.id.actionbar_settings:
                startActivity(new Intent(this, AppSettingsActivity.class));
            case R.id.english_lang_select:
            case R.id.dari_lang_select:
            case R.id.pashto_lang_select:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.payment_created_qr_nav_history) {
            // Handle the camera action
            startActivity(new Intent(this, QRCreateHistory.class));
        } else if (id == R.id.payment_created_qr_nav_favorite) {
            startActivity(new Intent(this, FavoriteGenQRCodes.class));
        } else if (id == R.id.payment_scanned_qr_nav_history) {
            startActivity(new Intent(this, QRScannedHistory.class));
        } else if (id == R.id.payment_scanned_qr_nav_favorite) {
            startActivity(new Intent(this, FavoriteScanQRCodes.class));
        } else if (id == R.id.secret_created_qr_nav_history) {
            startActivity(new Intent(this, SecretQRGenHistory.class));
        } else if (id == R.id.secret_created_qr_nav_favorite) {
            startActivity(new Intent(this, SecretQRGenFavorite.class));
        } else if (id == R.id.secret_scanned_qr_nav_histoyr) {
            startActivity(new Intent(this, SecretQRScanHistory.class));
        } else if (id == R.id.secret_scanned_qr_nav_favorite) {
            startActivity(new Intent(this, SecretQRScanFavorite.class));
        } else if (id == R.id.general_create_qr_nav_history) {
            startActivity(new Intent(this, GeneralQRGenHistory.class));
        } else if (id == R.id.general_create_qr_nav_favorite) {
            startActivity(new Intent(this, GeneralQRScanFavorite.class));
        } else if (id == R.id.general_scanned_qr_nav_history) {
            startActivity(new Intent(this, GeneralQRScanHistory.class));
        } else if (id == R.id.general_scanned_qr_nav_favorite) {
            startActivity(new Intent(this, GeneralQRScanFavorite.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, AppSettingsActivity.class));
        } else if (id == R.id.nav_share) {
            //Display Share Via dialogue
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "APS QR Code");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This QR Code App is awesome):");
            startActivity(Intent.createChooser(sharingIntent, "Share Via Social Medias"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new QRCreateFragment(), "Create QR Code");
        viewPagerAdapter.addFragment(new QRScannerFragment(), "Scan QR Code");
        viewPager.setAdapter(viewPagerAdapter);
    }

    /**
     * QR CODE scan activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We will get scan results here
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check for null
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled!", Toast.LENGTH_LONG).show();
            } else {
                try {
                    String scanResult = result.getContents();
                    if (scanResult.contains("1:01*") && "1:01*".equals(scanResult.substring(0, 5))) {
                        mGeneralQrCode = false;
                        //if the scan is successful then, direct it to the QRScanResultDisplayActivity to display the result in form.
                        Intent intent = new Intent(this, QRScanResultDisplayActivity.class);
                        intent.putExtra("qr_contents", result.getContents());
                        startActivity(intent);

                    } else {
                        String generalQrCode = qrService.decryptQRCodeContent(scanResult);
                        if (generalQrCode.contains("|*_pass:") && !qrService.getSecretQREncryptionKey(scanResult).isEmpty()) {
                            mGeneralQrCode = false;
                            // there is encryption key, then it means that, it belongs to secret QR Code
                            Intent intent = new Intent(this, SecretQRCodeScanResult.class);
                            Toast.makeText(MainActivity.this, "Secret QR scan", Toast.LENGTH_LONG).show();
                            intent.putExtra("secret_qr_contents", result.getContents());
                            startActivity(intent);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the activity
            super.onActivityResult(requestCode, resultCode, data);
        }

        if (mGeneralQrCode) {
            Intent intent = new Intent(this, GeneralQRCodeScanResult.class);
            Toast.makeText(this, "General QR scan", Toast.LENGTH_LONG).show();
            intent.putExtra("general_qr_contents", result.getContents());
            startActivity(intent);
        }

    }

    private void animateFab() {
        if (isOpen) {

            fab1_container.startAnimation(fabClose);
            fab2_container.startAnimation(fabClose);
            fab3_container.startAnimation(fabClose);
            fab1_container.setClickable(false);
            fab2_container.setClickable(false);
            fab3_container.setClickable(false);
            fab.startAnimation(rotateBackward);
            isOpen = false;
        } else {

            fab1_container.startAnimation(fabOpen);
            fab2_container.startAnimation(fabOpen);
            fab3_container.startAnimation(fabOpen);
            fab1_container.setClickable(true);
            fab2_container.setClickable(true);
            fab3_container.setClickable(true);
            fab.startAnimation(rotateForward);
            isOpen = true;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
