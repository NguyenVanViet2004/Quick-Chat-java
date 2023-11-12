package com.example.pro1121_gr.util;

import android.content.Context;
import android.widget.Toast;

public class AndroidUlti {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
