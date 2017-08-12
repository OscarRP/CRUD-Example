package com.example.oscarruiz.myapplication.controllers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import com.example.oscarruiz.myapplication.R;
import com.example.oscarruiz.myapplication.Utils.Constants;
import com.example.oscarruiz.myapplication.activities.HomeActivity;
import com.example.oscarruiz.myapplication.interfaces.AppInterfaces;

/**
 * Created by Oscar Ruiz on 09/08/2017.
 */

public class NavigationController {

    /**
     * Home State controller
     */
    private HomeStateController homeStateController;

    /**
     * Fragment Manager
     */
    private FragmentManager fragmentManager;

    /**
     * App state controller
     */
    private AppStateController controller;


    public NavigationController() {
        controller = AppStateController.getInstance();
        homeStateController = HomeStateController.getInstance();
    }

    /**
     * Method to change activity
     */
    public void changeActivity(Activity activity, Bundle params) {
        //Create Intent
        Intent intent;

        switch (controller.getState()) {
            case Constants.APLICATION_STATES.SPLASH_STATE:
                //close activity
                activity.finish();

                //set intent
                intent = new Intent(activity, HomeActivity.class);

                //check parms
                if(params!=null) {
                    //add paramas
                    intent.putExtras(params);
                }

                //start activity
                activity.startActivity(intent);

                //change controller state
                controller.setState(Constants.APLICATION_STATES.HOME_STATE);
                break;
            case Constants.APLICATION_STATES.HOME_STATE:
                break;
        }
    }

    /**
     * Method to change fragment
     */
    public void changeFragment(Activity activity, Fragment fragment, Bundle params, int homeState) {

        //check params
        if (params != null) {
            fragment.setArguments(params);
        }
        //change fragment
        fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        //set home state
        homeStateController.setState(homeState);
    }

    /**
     * Method to get home state
     */
    public int getHomeState () {
        return homeStateController.getState();
    }

}
