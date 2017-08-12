package com.example.oscarruiz.myapplication.interfaces;

/**
 * Created by Oscar Ruiz on 09/08/2017.
 */

public class AppInterfaces {

    /**
     * Interface to tell an animation is finished
     */
    public interface IFinishAnimation {
        public abstract void animationFinished();
    }

    /**
     * Interface to delete user
     */
    public interface IPressDeleteUser {
        public abstract void isDeleting(int position);
    }

    /**
     * Interface to set date
     */
    public interface ISetDate {
        public abstract void setDate(String date);
    }

    /**
     * Interface to add user image
     */
    public interface IAddImage{
        public abstract void addImage(String userPhotoUrl);
    }

    /**
     * Interface for dialog
     */
    public interface IBackButtonDialog{
        public abstract void pressBack();
    }
}
