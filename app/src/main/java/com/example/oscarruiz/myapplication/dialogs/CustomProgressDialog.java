package com.example.oscarruiz.myapplication.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.oscarruiz.myapplication.R;

/**
 * Created by Oscar Ruiz on 11/08/2017.
 */

public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(@NonNull Context context, boolean isResult) {
        super(context, R.style.customDialog);

        //if its a result dialog
        if(isResult){
            //set content view
            setContentView(R.layout.result_dialog);
        }else {
            //its a loading dialog
            //set content view
            setContentView(R.layout.loading_dialog);
        }
    }
}
