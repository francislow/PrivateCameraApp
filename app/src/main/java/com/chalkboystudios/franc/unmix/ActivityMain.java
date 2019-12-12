package com.chalkboystudios.franc.unmix;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.chalkboystudios.franc.unmix.Utilities.PicturesDatabaseHelper;
import com.chalkboystudios.franc.unmix.Utilities.MyCamera;
import com.chalkboystudios.franc.unmix.Utilities.MyUtilities;
import com.chalkboystudios.franc.unmix.Fragments.FragmentAdaptor;
import com.chalkboystudios.franc.unmix.Utilities.RequestCodeHelper;

import java.util.Calendar;


/**
 * The main entry point to the application.
 */
public class ActivityMain extends FragmentActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String DEFAULT_CATEGORY_NAME = "Unsorted";
    public static final String IMAGE_STORAGE_FOLDER_NAME = "UserPictures";

    private static MyCamera myCamera;
    private PicturesDatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCamera = new MyCamera(this);
        mydb = new PicturesDatabaseHelper(this);

        // Remove phone's notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set viewpager
        FragmentAdaptor fragmentAdaptor = new FragmentAdaptor(getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.myvp);
        viewPager.setAdapter(fragmentAdaptor);
        viewPager.setCurrentItem(0);

        // Setup first time tutorial guide
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyUtilities.createOneTimeIntroDialog(ActivityMain.this,
                                    "first_time_page2", R.drawable.starting_dialog2);
                        }
                    }, 400);
                }
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setup Btm Tab
        TabLayout btmTabLayout = (TabLayout) findViewById(R.id.btmtablayout);
        btmTabLayout.setupWithViewPager(viewPager);
        btmTabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        btmTabLayout.getTabAt(1).setIcon(R.drawable.ic_gallery);
        btmTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeHelper.TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            // Note that in this case, data will be null since in camera. This is a special case
            // when pictures taken are stored to a uri
            updateDatabase(myCamera.getPictureFilePath());

            // Setup first time tutorial guide
            MyUtilities.createOneTimeIntroDialog(this,"first_time_page1_2", R.drawable.starting_dialog1_2);
        } else if (requestCode == RequestCodeHelper.PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // Create a image file in UsersPicture folder
            Uri newUri = MyUtilities.copyMediaStoreUriToCacheDir(data.getData(), this);

            //Store picture into database
            updateDatabase(newUri.getPath());

            // Setup first time tutorial guide
            MyUtilities.createOneTimeIntroDialog(this,"first_time_page1_2", R.drawable.starting_dialog1_2);
        }
    }

    /**
     * Starts camera
     */
    public static void startCamera() {
        myCamera.dispatchTakePictureIntent();
    }

    /**
     * Inserts photo path and generated time into database
     *
     * @param photoPath photo path of the new image file
     */
    private void updateDatabase(String photoPath) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);

        boolean hasInsertedData = mydb.insertNewRowPTable(photoPath, DEFAULT_CATEGORY_NAME, "",
                null, year, month, day, hour, min, seconds);

        if (hasInsertedData) {
            Toast.makeText(this, "Successfully added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error adding", Toast.LENGTH_LONG).show();
        }
    }
}
