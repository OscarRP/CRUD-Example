package com.example.oscarruiz.myapplication.activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oscarruiz.myapplication.animations.AnimationManager;
import com.example.oscarruiz.myapplication.R;
import com.example.oscarruiz.myapplication.controllers.NavigationController;
import com.example.oscarruiz.myapplication.dialogs.Dialogs;
import com.example.oscarruiz.myapplication.interfaces.AppInterfaces;

public class SplashActivity extends Activity {

    /**
     * Navigation Controller
     */
    private NavigationController navigationController;

    /**
     * Animation Manager instance
     */
    private AnimationManager animation;

    /**
     * Create Text View
     */
    private TextView createTV;

    /**
     * Read Text view
     */
    private TextView readTV;

    /**
     * Update Text View
     */
    private TextView updateTV;

    /**
     * Delete Text View
     */
    private TextView deleteTV;

    /**
     * Example Text View
     */
    private TextView exampleTV;

    /**
     * CRUD Text View
     */
    private TextView crudTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //navigation controller instance
        navigationController = new NavigationController();

        getViews();
        startAnim();
    }

    /**
     * Mehtod to get views
     */
    private void getViews() {
        crudTV = findViewById(R.id.crud_text_view);
        exampleTV = findViewById(R.id.example_text_view);
        createTV = findViewById(R.id.create_text_view);
        readTV = findViewById(R.id.read_text_view);
        updateTV = findViewById(R.id.update_text_view);
        deleteTV = findViewById(R.id.delete_text_view);
    }

    /**
     * Method to anim Text Views
     */
    private void startAnim() {
        animation = new AnimationManager();
        animation.SplashAnimation(SplashActivity.this, crudTV, exampleTV, createTV, readTV, updateTV, deleteTV, new AppInterfaces.IFinishAnimation() {
            @Override
            public void animationFinished() {
                //Timer to finish Splash Activity
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long l) {
                    }

                    @Override
                    public void onFinish() {
                        //change Activity
                        navigationController.changeActivity(SplashActivity.this, null);
                    }
                }.start();
            }
        });
    }
}
