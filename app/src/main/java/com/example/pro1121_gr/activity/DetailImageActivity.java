package com.example.pro1121_gr.activity;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pro1121_gr.databinding.ActivityDetailImageBinding;
import com.example.pro1121_gr.function.RequestPermission;
import com.example.pro1121_gr.util.DownloadReceiver;

public class DetailImageActivity extends AppCompatActivity {

    ActivityDetailImageBinding detailImageBinding;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailImageBinding = ActivityDetailImageBinding.inflate(getLayoutInflater());
        setContentView(detailImageBinding.getRoot());


        initView();
    }

    private void initView() {
        String url = getIntent().getStringExtra("message");
        Glide.with(this)
                .load(url)
                .apply(new RequestOptions()
                        .fitCenter() // Giữ tỷ lệ và hiển thị trong khung
                )
                .into(detailImageBinding.photoView); // ImageView để hiển thị ảnh


        detailImageBinding.backFragmentMess.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        detailImageBinding.downloadImg.setOnClickListener(view -> {
            if (RequestPermission.checkPermission(DetailImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                DownloadReceiver.progressDownload(url, this, REQUEST_WRITE_EXTERNAL_STORAGE);
            } else RequestPermission.requestWriteExternalStoragePermission(DetailImageActivity.this, REQUEST_WRITE_EXTERNAL_STORAGE);
        });

        detailImageBinding.photoView.setOnClickListener(view -> {
            if (click == 0){
                detailImageBinding.line1.setVisibility(View.GONE);
                click++;
            }else {
                detailImageBinding.line1.setVisibility(View.VISIBLE);
                click = 0;
            }
        });
    }
}