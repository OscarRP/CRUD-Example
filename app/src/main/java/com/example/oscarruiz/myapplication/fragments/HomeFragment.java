package com.example.oscarruiz.myapplication.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.oscarruiz.myapplication.R;
import com.example.oscarruiz.myapplication.Utils.Constants;
import com.example.oscarruiz.myapplication.activities.HomeActivity;
import com.example.oscarruiz.myapplication.adapters.UsersListAdapter;
import com.example.oscarruiz.myapplication.controllers.APIClient;
import com.example.oscarruiz.myapplication.controllers.NavigationController;
import com.example.oscarruiz.myapplication.dialogs.Dialogs;
import com.example.oscarruiz.myapplication.interfaces.APIInterface;
import com.example.oscarruiz.myapplication.interfaces.AppInterfaces;
import com.example.oscarruiz.myapplication.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    /**
     * Dialogs instance
     */
    private Dialogs dialogs;

    /**
     * Edited user with old data
     */
    private User user;

    /**
     * Var to know if an user has been edited as last action
     */
    private boolean userEdited;

    /**
     * Filtered users list
     */
    private ArrayList<User> filteredUsers;

    /**
     * Search edit text
     */
    private EditText searchET;

    /**
     * Navigation controller instance
     */
    private NavigationController navigationController;

    /**
     * if user pressed delete button
     */
    private boolean isDeleting;

    /**
     * Users list adapter
     */
    private UsersListAdapter adapter;

    /**
     * Users list
     */
    private ArrayList<User> usersList;

    /**
     * Contacts List View
     */
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Show floating button
        ((HomeActivity)getActivity()).showFloatingButton();

        //Set options menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        navigationController = new NavigationController();

        getViews(view);
        setListeners();
        getInfo();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem addUser = menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, getString(R.string.add_user));
        MenuItem deleteUser = menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, getString(R.string.delete_user));
        MenuItem undoEditing = menu.add(Menu.NONE, Menu.FIRST+2, Menu.NONE, getString(R.string.undo_editing));
        MenuItem closeSession = menu.add(Menu.NONE, Menu.FIRST+3, Menu.NONE, getString(R.string.close_sesion));

        if (userEdited) {
            undoEditing.setEnabled(true);
        } else {
            undoEditing.setEnabled(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                //change fragment to Add user Fragment
                Fragment addUserFragment = new AddUserFragment();
                navigationController.changeFragment(getActivity(), addUserFragment, null, Constants.HOME_STATES.ADD_USER_STATE);
                break;
            case 2:
                //delete user, put is deleting in bundle
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.ISDELETING, true);

                //reload fragment with bundle
                Fragment homeFragment = new HomeFragment();
                navigationController.changeFragment(getActivity(), homeFragment, bundle, Constants.HOME_STATES.USER_LIST_STATE);
                break;
            case 3:
                //Update user with old data
                updateUser();
                break;
            case 4:
                //kill App
                getActivity().finishAndRemoveTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to get views
     */
    private void getViews(View view) {
        listView = view.findViewById(R.id.contacts_list_view);
        searchET = view.findViewById(R.id.search_edit_text);
    }

    /**
     * Method to set listeners
     */
    private void setListeners() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //delete user, put is deleting in bundle
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER, usersList.get(i));

                //go to detail fragment
                Fragment detailUserFragment = new DetailUserFragment();
                navigationController.changeFragment(getActivity(), detailUserFragment, bundle, Constants.HOME_STATES.DETAIL_USER_STATE);
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //search coincidences
                String searchText = searchET.getText().toString().toLowerCase();
                filteredUsers = searchUsers(searchText);

                //update listview
                adapter = new UsersListAdapter(getActivity(), filteredUsers, isDeleting, new AppInterfaces.IPressDeleteUser() {
                    @Override
                    public void isDeleting(int position) {
                        deleteUser(filteredUsers.get(position).getId());
                    }
                });
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * Method to get info
     */
    private void getInfo() {

        dialogs = new Dialogs();
        dialogs.showLoadingDialog(getActivity());

        //check if there are arguments
        if (getArguments() != null){
            isDeleting = getArguments().getBoolean(Constants.ISDELETING);
            userEdited = getArguments().getBoolean(Constants.USEREDITED);
            //set user with old data
            if(userEdited) {
                user = (User) getArguments().getSerializable(Constants.USER);
            }

        } else {
            //set isDeleting false
            isDeleting = false;
        }

        loadList(isDeleting);
    }

    /**
     * Method to delete user
     */
    private void deleteUser(final int userId) {
        //Check internet connection
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected()) {
            dialogs.showResultDialog(getActivity(), getString(R.string.description_no_connection), true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteUser(userId);
                    dialogs.hideResultDialog();
                }
            }, new AppInterfaces.IBackButtonDialog() {
                @Override
                public void pressBack() {
                    deleteUser(userId);
                    dialogs.hideResultDialog();
                }
            });
        } else {
            dialogs.showLoadingDialog(getActivity());
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call call = apiInterface.deleteUser(userId);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    dialogs.hideLoadingDialog();
                    dialogs.showResultDialog(getActivity(), getString(R.string.deleted), true, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //hide result dialog
                            dialogs.hideResultDialog();
                            //empty search edit text
                            searchET.setText("");
                            isDeleting = false;
                            //reload data
                            loadList(isDeleting);
                        }
                    }, new AppInterfaces.IBackButtonDialog() {
                        @Override
                        public void pressBack() {
                            //hide result dialog
                            dialogs.hideResultDialog();
                            //empty search edit text
                            searchET.setText("");
                            isDeleting = false;
                            //reload data
                            loadList(isDeleting);
                        }
                    });
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    showErrorDialog(getActivity().getResources().getString(R.string.home_title),
                            getActivity().getResources().getString(R.string.delete_error_description),
                            getActivity().getResources().getString(R.string.accept));
                }
            });
        }
    }

    /**
     * Method to search users
     */
    private ArrayList<User> searchUsers (String text) {
        //init aux vars
        ArrayList<User> finalUserList = new ArrayList<>();
        String auxString;

        //search coincidences trhough hole users list
        for (int i=0; i<usersList.size(); i++) {
            //check text contains any character
            if (text.length() > 0) {
                if (usersList.get(i).getName().length() >= text.length()){
                    auxString = usersList.get(i).getName().substring(0, text.length()).toLowerCase();
                    if (text.equals(auxString)) {
                        finalUserList.add(usersList.get(i));
                    }
                }
            } else {
                finalUserList.add(usersList.get(i));
            }
        }
        return finalUserList;
    }

    /**
     * Method to show error dialog
     */
    private void showErrorDialog (String title, String description, String button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(description);
        builder.setNegativeButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //load list
                getInfo();
            }
        });
        builder.show();
    }

    /**
     * Method to update user
     */
    private void updateUser() {
        dialogs.showLoadingDialog(getActivity());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call call = apiInterface.updateUser(user);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                dialogs.hideLoadingDialog();
                if (response.code() == 200) {

                    dialogs.showResultDialog(getActivity(), getString(R.string.user_edited), true, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogs.hideResultDialog();
                            //reload list
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(HomeFragment.this).attach(HomeFragment.this).commit();
                        }
                    }, new AppInterfaces.IBackButtonDialog() {
                        @Override
                        public void pressBack() {
                            dialogs.hideResultDialog();
                            //reload list
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(HomeFragment.this).attach(HomeFragment.this).commit();
                        }
                    });

                } else {
                    showErrorDialog(getString(R.string.home_title), getString(R.string.update_description), getString(R.string.accept));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                dialogs.hideLoadingDialog();
                showErrorDialog(getString(R.string.home_title), getString(R.string.update_description), getString(R.string.accept));
            }
        });
    }

    /**
     * Method to load users list
     */
    private void loadList(final boolean deleting) {
        //Check internet connection
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected()) {
            dialogs.showResultDialog(getActivity(), getString(R.string.description_no_connection), true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getInfo();
                    dialogs.hideResultDialog();
                }
            }, new AppInterfaces.IBackButtonDialog() {
                @Override
                public void pressBack() {
                    getInfo();
                    dialogs.hideResultDialog();
                }
            });
        } else {
            //create api connection
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

            Call call = apiInterface.getUsersList();
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    dialogs.hideLoadingDialog();

                    //show user list
                    if (response.isSuccessful()) {
                        usersList = (ArrayList<User>)response.body();

                        //init filteredUsers
                        filteredUsers = usersList;

                        adapter = new UsersListAdapter(getActivity(), usersList, deleting, new AppInterfaces.IPressDeleteUser() {
                            @Override
                            public void isDeleting(int position) {
                                //delete user from Api
                                deleteUser(usersList.get(position).getId());
                            }
                        });
                        listView.setAdapter(adapter);
                    } else {
                        showErrorDialog(getActivity().getResources().getString(R.string.home_title),
                                getActivity().getResources().getString(R.string.home_description),
                                getActivity().getResources().getString(R.string.home_reload_button));
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    //show Alert Dialog
                    showErrorDialog(getActivity().getResources().getString(R.string.home_title),
                            getActivity().getResources().getString(R.string.home_description),
                            getActivity().getResources().getString(R.string.home_reload_button));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }
}
