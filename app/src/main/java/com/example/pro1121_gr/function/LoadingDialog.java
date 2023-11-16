package com.example.pro1121_gr.function;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.example.pro1121_gr.R;

public class LoadingDialog {
    private Dialog isDialog;
    private final Activity activity;
    private static LoadingDialog loadingDialog;


    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoading() {
        // set view
        android.view.LayoutInflater inflater = activity.getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.loading_item, null);

        // set dialog
        Dialog builder = new Dialog(activity);
        builder.setContentView(dialogView);
        builder.setCancelable(false);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.show();
        isDialog = builder;
    }

    public void isDismiss() {
        if (isDialog != null && isDialog.isShowing()) {
            isDialog.dismiss();
        }
    }

    public static LoadingDialog getInstance(Activity activity) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(activity);
        }
        return loadingDialog;
    }
}
