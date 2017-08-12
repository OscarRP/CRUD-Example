package com.example.oscarruiz.myapplication.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailUserFragment extends Fragment {

    /**
     * Dialogs instance
     */
    private Dialogs dialogs;

    /**
     * Edited user
     */
    private User editedUser;

    /**
     * User
     */
    private User user;

    /**
     * boolean to know if user is editing info
     */
    private boolean isEditing;

    /**
     * Header text view
     */
    private TextView headerTV;

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

        //hide floating button
        ((HomeActivity)getActivity()).hideFloatingButton();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        navigationController = new NavigationController();

        getViews(view);
        getInfo();
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
        headerTV = view.findViewById(R.id.header_text_view);
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if isn´t editing, allow edit
                if (!isEditing) {
                    isEditing = true;
                    userNameET.setEnabled(true);
                    birthdateTV.setEnabled(true);
                    acceptButton.setText(getString(R.string.accept));
                } else {
                    //check fields
                    if (checkFields()) {
                        //update user
                        updateUser();
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to home fragment
                Fragment homeFragment = new HomeFragment();
                navigationController.changeFragment(getActivity(), homeFragment, null, Constants.HOME_STATES.USER_LIST_STATE);
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
     * Method to set initial info
     */
    private void getInfo() {
        //init isEditing
        isEditing = false;

        //set header text
        headerTV.setText(getString(R.string.detail_user_header));
        //change accept button text to Edit
        acceptButton.setText(getString(R.string.edit));

        //check if there are arguments
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(Constants.USER);
            userNameET.setText(user.getName());

            //eliminate T char from birthdate string
            String birthdate = user.getBirthdate().replace("T", " ");
            birthdateTV.setText(birthdate);
        }

        userNameET.setEnabled(false);
        birthdateTV.setEnabled(false);
    }

    /**
     * Method to update user
     */
    private void updateUser() {
        //Check internet connection
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected()) {
            dialogs.showResultDialog(getActivity(), getString(R.string.description_no_connection), true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateUser();
                    dialogs.hideResultDialog();
                }
            }, new AppInterfaces.IBackButtonDialog() {
                @Override
                public void pressBack() {
                    updateUser();
                    dialogs.hideResultDialog();
                }
            });
        } else {
            dialogs.showLoadingDialog(getActivity());
            editedUser = new User(user.getId(), userNameET.getText().toString(), birthdateTV.getText().toString());

            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call call = apiInterface.updateUser(editedUser);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    dialogs.hideLoadingDialog();
                    if (response.code() == 200) {

                        dialogs.showResultDialog(getActivity(), getString(R.string.user_edited), true, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogs.hideResultDialog();
                                //go to home fragment
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(Constants.USEREDITED, true);
                                bundle.putSerializable(Constants.USER, user);

                                Fragment homeFragment = new HomeFragment();
                                navigationController.changeFragment(getActivity(), homeFragment, bundle, Constants.HOME_STATES.USER_LIST_STATE);
                            }
                        }, new AppInterfaces.IBackButtonDialog() {
                            @Override
                            public void pressBack() {
                                dialogs.hideResultDialog();
                                //go to home fragment
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(Constants.USEREDITED, true);
                                bundle.putSerializable(Constants.USER, user);

                                Fragment homeFragment = new HomeFragment();
                                navigationController.changeFragment(getActivity(), homeFragment, bundle, Constants.HOME_STATES.USER_LIST_STATE);
                            }
                        });

                    } else {
                        showErrorDialog();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialogs.hideLoadingDialog();
                    showErrorDialog();
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

    /**
     * Method to show error dialog
     */
    private void showErrorDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getResources().getString(R.string.home_title));
        builder.setMessage(getActivity().getResources().getString(R.string.update_description));
        builder.setNegativeButton(getActivity().getResources().getString(R.string.home_reload_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }
}
