package com.example.oscarruiz.myapplication.dialogs;


import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.oscarruiz.myapplication.R;
import com.example.oscarruiz.myapplication.interfaces.AppInterfaces;


/**
 * Created by Oscar Ruiz on 10/08/2017.
 */

public class Dialogs  {

    /**
     * indiates if result dialog is showing
     */
    private static Boolean resultDialogShowing;

    /**
     * indicates if loading dialog is hidden
     */
    private static boolean hideLoading;

    /**
     * loading dialog
     */
    private static CustomProgressDialog loading;

    /**
     * result dialog
     */
    private static CustomProgressDialog resultDialog;

    /**
     * Method to show loading dialog
     */
    public static void showLoadingDialog(Context context) {
        if(loading == null){
            //create dialog
            loading  = new CustomProgressDialog(context,false);
            //set properties
            loading.setCancelable(false);
        }

        //loading dialog is showing
        hideLoading = false;

        loading.show();
    }

    /**
     * Method to hide loading dialog
     */
    public static void hideLoadingDialog() {
        //check dialog
        if ((loading != null)) {
            try {
                //hide loading
                loading.dismiss();
                //reset loading
                loading = null;
            } catch (Exception a) {
                //kill loading
                loading = null;
            }
        }
        //kill loading
        loading = null;
    }

    /**
     * Method to show result dialog
     */
    public static void showResultDialog(Context context, String text, boolean isOkResult, final View.OnClickListener listener, final AppInterfaces.IBackButtonDialog backListener){
        resultDialogShowing = true;

        //dialog is null
        if(resultDialog == null){
            //create dialog
            resultDialog  = new CustomProgressDialog(context,true);
        }

        //get views
        TextView message = resultDialog.findViewById(R.id.result_text);
        ImageView resultImage = resultDialog.findViewById(R.id.result_image);
        final RelativeLayout container = resultDialog.findViewById(R.id.dialog_container);
        //set text
        message.setText(text);

        //check result
        if(isOkResult){
            //set ok result image
            resultImage.setImageResource(R.mipmap.result_ok);
        }
        else{
            //set error result image
            resultImage.setImageResource(R.mipmap.error_icon);
        }

        //set listener
        container.setOnClickListener(listener);

        //set back dialog
        resultDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    backListener.pressBack();
                    return true;
                }else{
                    return false;
                }
            }
        });

        //show dialog
        resultDialog.show();
    }

    /**
     * Method to hide result dialog
     */
    public static void hideResultDialog(){
        resultDialogShowing = false;
        //check dialog
        if(resultDialog!=null){
            try {
                //hide loading
                resultDialog.dismiss();
                //reset loading
                resultDialog = null;
            }catch(Exception a){
                //kill loading
                resultDialog = null;
            }
        }
        //kill loading
        resultDialog = null;
    }
}
