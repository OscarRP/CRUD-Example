package com.example.oscarruiz.myapplication.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.oscarruiz.myapplication.R;
import com.example.oscarruiz.myapplication.Utils.Constants;
import com.example.oscarruiz.myapplication.activities.HomeActivity;
import com.example.oscarruiz.myapplication.controllers.APIClient;
import com.example.oscarruiz.myapplication.controllers.NavigationController;
import com.example.oscarruiz.myapplication.dialogs.Dialogs;
import com.example.oscarruiz.myapplication.interfaces.APIInterface;
import com.example.oscarruiz.myapplication.interfaces.AppInterfaces;
import com.example.oscarruiz.myapplication.models.User;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserFragment extends Fragment {

    /**
     * Dialogs instance
     */
    private Dialogs dialogs;

    /**
     * User name Edit text
     */
    private EditText userNameET;

    /**
     * Navigation controller instance
     */
    private NavigationController navigationController;

    /**
     * Birthdate Edittext
     */
    private TextView birthdateTV;

    /**
     * Change profile image button
     */
    private ImageView changeImage;

    /**
     * User image
     */
    private ImageView userImage;

    /**
     * Cancel Button
     */
    private Button cancelButton;

    /**
     * Accept Button
     */
    private Button acceptButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide floating button
        ((HomeActivity)getActivity()).hideFloatingButton();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        navigationController = new NavigationController();

        getViews(view);
        setListeners();
        
        return view;
    }

    /**
     * Method to get views
     */
    private void getViews(View view) {
        cancelButton = view.findViewById(R.id.cancel_button);
        acceptButton = view.findViewById(R.id.accept_button);
        birthdateTV = view.findViewById(R.id.birthdate_edit_text);
        userImage = view.findViewById(R.id.user_image);
        userNameET = view.findViewById(R.id.user_name_edit_text);
        changeImage = view.findViewById(R.id.change_profile_image);
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to home fragment
                Fragment homeFragment = new HomeFragment();
                navigationController.changeFragment(getActivity(), homeFragment, null, Constants.HOME_STATES.USER_LIST_STATE);
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check fields
                if (checkFields()) {
                    //create user
                    createUser(new User(userNameET.getText().toString(), birthdateTV.getText().toString()));
                } else {
                    //show Alert Dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getActivity().getResources().getString(R.string.add_title));
                    builder.setMessage(getActivity().getResources().getString(R.string.add_description));
                    builder.setNegativeButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            }
        });

        birthdateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show date / time picker dialog
                ((HomeActivity)getActivity()).setDate(new AppInterfaces.ISetDate() {
                    @Override
                    public void setDate(String date) {
                        //set date in birthday text view
                        birthdateTV.setText(date);
                    }
                });
            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            //NO DATA PERSISTENCE BECAUSE IMAGE CANT BE SAVE IN API
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).selectProfileImage(getActivity(), new AppInterfaces.IAddImage() {
                    @Override
                    public void addImage(String userPhotoUrl) {
                        //rounded image
                        Glide.with(getActivity()).load(userPhotoUrl).asBitmap().centerCrop().placeholder(R.mipmap.profile).into(new BitmapImageViewTarget(userImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                userImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Method to create user
     */
    private void createUser(final User user) {
        //Check internet connection
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        //if user hasn´t internet connection
        if (netInfo == null || !netInfo.isConnected()) {
            dialogs.showResultDialog(getActivity(), getString(R.string.description_no_connection), true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createUser(user);
                    dialogs.hideResultDialog();
                }
            }, new AppInterfaces.IBackButtonDialog() {
                @Override
                public void pressBack() {
                    createUser(user);
                    dialogs.hideResultDialog();
                }
            });
        } else {
            dialogs.showLoadingDialog(getActivity());
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call call = apiInterface.createUser(user);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    dialogs.hideLoadingDialog();

                    dialogs.showResultDialog(getActivity(), getString(R.string.user_created), true, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogs.hideResultDialog();
                            //go to home fragment
                            Fragment homeFragment = new HomeFragment();
                            navigationController.changeFragment(getActivity(), homeFragment, null, Constants.HOME_STATES.USER_LIST_STATE);
                        }
                    }, new AppInterfaces.IBackButtonDialog() {
                        @Override
                        public void pressBack() {
                            dialogs.hideResultDialog();
                            //go to home fragment
                            Fragment homeFragment = new HomeFragment();
                            navigationController.changeFragment(getActivity(), homeFragment, null, Constants.HOME_STATES.USER_LIST_STATE);
                        }
                    });
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialogs.hideLoadingDialog();
                    //show error
                    //show Alert Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getActivity().getResources().getString(R.string.add_title));
                    builder.setMessage(getActivity().getResources().getString(R.string.error_add_description));
                    builder.setNegativeButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    /**
     * Method to check if fields are filled
     */
    private boolean checkFields() {
        if (userNameET.getText().toString().isEmpty() || birthdateTV.getText().toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
