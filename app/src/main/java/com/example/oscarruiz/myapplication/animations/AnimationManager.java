package com.example.oscarruiz.myapplication.animations;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.oscarruiz.myapplication.R;
import com.example.oscarruiz.myapplication.interfaces.AppInterfaces;

/**
 * Created by Oscar Ruiz on 09/08/2017.
 */

public class AnimationManager {

    /**
     * Animation
     */
    private Animation animation;


    /**
     * Starts left to right animnation
     */
    public void leftToRightAnimation (View view, Context context, int duration) {
        animation = AnimationUtils.loadAnimation(context, R.anim.left_to_right);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    /**
     * Starts right to left animnation
     */
    public void rightToLeftAnim (View view, Context context, int duration) {
        animation = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    /**
     * Starts up to alpha_in animation
     */
    public void alphaIn (View view, Context context, int duration) {
        animation = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    /**
     * Method to create Splash Animation
     */
    public void SplashAnimation (final Context context, View crudTV, View exampleTV, final View createTV, final View readTV, final View updateTV, final View deleteTV, final AppInterfaces.IFinishAnimation listener) {
        //start crudTV animatino
        alphaIn(crudTV, context, 1500);
        crudTV.setVisibility(View.VISIBLE);

        //create exampleTV animation
        animation = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        animation.setDuration(1500);
        exampleTV.startAnimation(animation);
        exampleTV.setVisibility(View.VISIBLE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //start create, read, update, delete animations
                leftToRightAnimation(createTV, context, 1000);
                rightToLeftAnim(readTV, context, 1000);
                leftToRightAnimation(updateTV, context, 1000);
                rightToLeftAnim(deleteTV, context, 1000);
                //set views visible
                createTV.setVisibility(View.VISIBLE);
                readTV.setVisibility(View.VISIBLE);
                updateTV.setVisibility(View.VISIBLE);
                deleteTV.setVisibility(View.VISIBLE);

                listener.animationFinished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
