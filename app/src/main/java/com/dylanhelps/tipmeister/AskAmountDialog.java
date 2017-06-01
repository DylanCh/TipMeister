package com.dylanhelps.tipmeister;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Hanjun Chen on 5/31/17.
 */
public class AskAmountDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Is the Amount correct?")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preceedToCalculate();
                    }
                }).setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askForCorrectAmount();
                    }
        });
        return builder.create();
    }

    // TODO:Ask for correct Amount
    private void askForCorrectAmount() {

    }

    public static AskAmountDialog getInstance(String title){
        AskAmountDialog ask = new AskAmountDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);

        return ask;
    }

    //TODO: list tips
    private void preceedToCalculate() {

    }
}
