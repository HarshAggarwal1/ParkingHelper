package com.project.parking_helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class ProgressLoadingBar {
    Context context;
    Dialog dialog;

    public ProgressLoadingBar(Context context) {
        this.context = context;
    }

    public void startLoadingDialog(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_bar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        dialog.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
