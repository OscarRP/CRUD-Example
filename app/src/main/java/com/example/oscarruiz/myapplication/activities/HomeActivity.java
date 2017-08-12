package com.example.oscarruiz.myapplication.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;

import com.example.oscarruiz.myapplication.R;
import com.example.oscarruiz.myapplication.Utils.Constants;
import com.example.oscarruiz.myapplication.controllers.NavigationController;
import com.example.oscarruiz.myapplication.fragments.AddUserFragment;
import com.example.oscarruiz.myapplication.fragments.HomeFragment;
import com.example.oscarruiz.myapplication.interfaces.AppInterfaces;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener {

    /**
     * Floating action button
     */
    private FloatingActionButton floatingButton;

    /**
     * Formatted date
     */
    private String formattedDate;

    /**
     * Formatted time
     */
    private String formattedTime;

    /**
     * Listener to add user image
     */
    private AppInterfaces.IAddImage imageListener;

    /**
     * Set date listener
     */
    private AppInterfaces.ISetDate listener;

    /**
     * Navigation controller instance
     */
    private NavigationController navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //navigation controller instance
        navigationController = new NavigationController();

        getViews();
        setListeners();

        //Init navigation
        Fragment homeFragment = new HomeFragment();
        navigationController.changeFragment(HomeActivity.this, homeFragment, null, Constants.HOME_STATES.USER_LIST_STATE);
    }

    /**
     * Method to get views
     */
    private void getViews() {
        floatingButton = (FloatingActionButton)findViewById(R.id.floating_button);
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change frament to add fragment
                Fragment addFragment = new AddUserFragment();
                navigationController.changeFragment(HomeActivity.this, addFragment, null, Constants.HOME_STATES.ADD_USER_STATE);
            }
        });
    }

    /**
     * Method to show date picker dialog
     */
    public void setDate(AppInterfaces.ISetDate listener) {
        this.listener = listener;

        //init calendar and get actual date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //create date time picker
        //from external lib to change year easier
        com.fourmob.datetimepicker.date.DatePickerDialog datePickerDialog = com.fourmob.datetimepicker.date.DatePickerDialog.newInstance(this, year, month, day);
        datePickerDialog.setYearRange(1930, 2020);
        datePickerDialog.show(getSupportFragmentManager(), "Date Picker");
    }

    /**
     * Method to show time picker dialog
     */
    public void setTime() {
        //get actual time
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        //create time picker dialog
        com.sleepbot.datetimepicker.time.TimePickerDialog timePickerDialog = com.sleepbot.datetimepicker.time.TimePickerDialog.newInstance(new com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hour, int minute) {
                //time selected string
                if (minute < 10) {
                    //add a "0" before minute
                    formattedTime = String.valueOf(hour)+":0"+String.valueOf(minute);
                } else {
                    formattedTime = String.valueOf(hour)+":"+String.valueOf(minute);
                }

                //final date string
                String finalDate = formattedDate + " " + formattedTime;

                //send data to fragment
                listener.setDate(finalDate);
            }
        }, hour, minute, true);
        //show time picker
        timePickerDialog.show(getSupportFragmentManager(), "Time Picker");
    }

    @Override
    public void onDateSet(com.fourmob.datetimepicker.date.DatePickerDialog datePickerDialog, int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        //date format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = simpleDateFormat.format(c.getTime());

        //open time picker dialog
        setTime();
    }

    /**
     * Method to select a Image source
     */
    public void selectProfileImage(final Activity activity, AppInterfaces.IAddImage imageListener) {
        this.imageListener = imageListener;

        final String title = getString(R.string.select_option);

        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(title);
        builder.setItems(this.getResources().getStringArray(R.array.add_image_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    //check for permissions
                    if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        //open camera
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activity.startActivityForResult(intent, Constants.SELECT_CAMERA);
                    } else {
                        //request permissions
                        checkAndRequestPermissions(activity, Constants.SELECT_CAMERA);
                    }

                } else if (item==1) {
                    //check for permissions
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //open gallery
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        activity.startActivityForResult(Intent.createChooser(intent, title), Constants.SELECT_GALLERY);
                    } else {
                        //request permissions
                        checkAndRequestPermissions(activity, Constants.SELECT_GALLERY);
                    }
                }else if (item == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Method to check all request permissions
     */
    public static void checkAndRequestPermissions(Activity activity, int permission) {
        int permissionCamera = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //check if user pressed select from gallery
        if (permission == Constants.SELECT_GALLERY) {
            //check permission
            if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                //request permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.SELECT_GALLERY);
            }
            //check if user pressed select from camera
        } else if (permission == Constants.SELECT_CAMERA) {
            //list of permissions needed
            List<String> listPermissionsNeeded = new ArrayList<>();
            //check external storage permission
            if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                //add to permissions needed
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            //check camera permission
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                //add to permissions needed
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            //request permissions needed
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.SELECT_CAMERA);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA},  Constants.SELECT_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //camera result permission
        if (requestCode == Constants.SELECT_CAMERA && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.SELECT_CAMERA);
        }

        //gallery result permission
        if (requestCode == Constants.SELECT_GALLERY && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            //open gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.SELECT_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image from gallery
        if (requestCode == Constants.SELECT_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                //convert chosen image to uri
                Uri selectedImageUri = data.getData();

                if(imageListener != null) {
                    imageListener.addImage(selectedImageUri.toString());
                }
            }

            //Image from camera
        } else if (requestCode == Constants.SELECT_CAMERA && resultCode == Activity.RESULT_OK) {
            //photo url
            String userPhotoUrl = null;

            //take info from intent
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            FileOutputStream fileOutputStream;
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    try {
                        //make directory
                        File direct = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Constants.PHOTO_DIRECTORY);
                        //make if no exists
                        if (!direct.exists()) {
                            if (!direct.mkdirs()) {
                                direct.mkdir();
                            }
                        }
                        //save image
                        String filename = System.currentTimeMillis() + ".jpg";
                        File destination = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Constants.PHOTO_DIRECTORY, filename);
                        destination.createNewFile();

                        //get url
                        userPhotoUrl = Environment.getExternalStorageDirectory().toString() + File.separator + Constants.PHOTO_DIRECTORY + filename;

                        //write in memory
                        fileOutputStream = new FileOutputStream(destination);
                        fileOutputStream.write(bytes.toByteArray());
                        fileOutputStream.close();

                        if (imageListener != null) {
                            imageListener.addImage(userPhotoUrl);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Method to hide floating action button
     */
    public void hideFloatingButton () {
        floatingButton.setVisibility(View.GONE);
    }

    /**
     * Method to show floating action button
     */
    public void showFloatingButton () {
        floatingButton.setVisibility(View.VISIBLE);
    }

    /**
     * Control navigation on backbutton pressed
     */
    @Override
    public void onBackPressed() {
        if (navigationController.getHomeState()==Constants.HOME_STATES.USER_LIST_STATE) {
            //close App
            finishAffinity();
        } else {
            Fragment homeFragment = new HomeFragment();
            navigationController.changeFragment(HomeActivity.this, homeFragment, null, Constants.HOME_STATES.USER_LIST_STATE);
        }
    }

}
