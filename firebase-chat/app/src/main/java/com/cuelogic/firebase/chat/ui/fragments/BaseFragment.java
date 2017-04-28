package com.cuelogic.firebase.chat.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    public void showToastShort(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
    public void showToastLong(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
    public void showAlertMessage(String message) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
