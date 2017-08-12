package com.example.oscarruiz.myapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.oscarruiz.myapplication.R;
import com.example.oscarruiz.myapplication.interfaces.AppInterfaces;
import com.example.oscarruiz.myapplication.models.User;

import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 09/08/2017.
 */

public class UsersListAdapter extends BaseAdapter {

    /**
     * Interface deleting user
     */
    private AppInterfaces.IPressDeleteUser listener;

    /**
     * Holder with all views
     */
    private ViewHolder viewHolder;

    /**
     * Activate delete button
     */
    private boolean isDeleting;

    /**
     * Layout inflater
     */
    private LayoutInflater inflater;

    /**
     * Context
     */
    private Context context;

    /**
     * Users list
     */
    private ArrayList<User> usersList;

    public UsersListAdapter (Context context, ArrayList<User> usersList, boolean isDeleting, AppInterfaces.IPressDeleteUser listener) {
        this.context = context;
        this.usersList = usersList;
        this.isDeleting = isDeleting;
        this.listener = listener;

        //init inflater
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Object getItem(int i) {
        return usersList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return usersList.get(i).getId();
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            //inflate view
            view = inflater.inflate(R.layout.user_list_item, viewGroup, false);

            //initialize viewholder
            viewHolder = new ViewHolder();

            //getviews
            viewHolder.nameTV = view.findViewById(R.id.name_text_view);
            viewHolder.deleteButton = view.findViewById(R.id.delete_button);

            //set tag
            view.setTag(viewHolder);
        } else {
            //get holder
            viewHolder = (ViewHolder)view.getTag();
        }

        //set info
        viewHolder.nameTV.setText(usersList.get(position).getName());

        if (isDeleting) {
            //show Delete button
            viewHolder.deleteButton.setVisibility(View.VISIBLE);
            //set delete button listener
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //show Alert Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.delete_title));
                    builder.setMessage(context.getResources().getString(R.string.delete_description));
                    builder.setPositiveButton(context.getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //send positin to delete user
                            listener.isDeleting(position);
                        }
                    });
                    builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            });
        }
        return view;
    }

    /**
     * View Holder class
     */
    private class ViewHolder{
        //set all view elements
        private TextView nameTV;
        private ImageButton deleteButton;
    }
}
