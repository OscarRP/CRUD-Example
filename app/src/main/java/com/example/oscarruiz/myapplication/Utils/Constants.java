package com.example.oscarruiz.myapplication.Utils;


/**
 * Created by Oscar Ruiz on 09/08/2017.
 */

public class Constants {

    /**
     * Api URL
     */
    public static final String URL = "http://hello-world.innocv.com/api/user/";

    /**
     * Endpoints
     */
    public interface ENDPOINTS {
        public static final String GET_ALL = "getall";
        public static final String GET = "get";
        public static final String CREATE = "create";
        public static final String UPDATE = "update";
        public static final String REMOVE = "remove";
    }

    /**
     * Application states interface
     */
    public interface APLICATION_STATES {
        public static final int SPLASH_STATE = 0;
        public static final int HOME_STATE = SPLASH_STATE + 1;
    }

    /**
     * Home states to control fragemnt navigation
     */
    public interface HOME_STATES {
        public static final int USER_LIST_STATE = 0;
        public static final int ADD_USER_STATE = USER_LIST_STATE + 1;
        public static final int DETAIL_USER_STATE = ADD_USER_STATE + 1;
    }

    /**
     * Bundle key is deleting user
     */
    public static final String ISDELETING = "isdeleting";

    /**
     * Bundle key an user was edited
     */
    public static final String USEREDITED = "useredited";

    /**
     * Bundle key user
     */
    public static final String USER = "user";

    /**
     * Constant to define camera option
     */
    public static int SELECT_CAMERA = 0;

    /**
     * Constant to define Gallery option
     */
    public static int SELECT_GALLERY = 1;

    /**
     *  Directory to save photos
     */
    public static final String PHOTO_DIRECTORY = "crud/profile/photos/";
}
