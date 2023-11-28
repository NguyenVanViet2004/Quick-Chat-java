package com.example.pro1121_gr.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.adapter.chatAdapter;
import com.example.pro1121_gr.databinding.ActivityChatBinding;
import com.example.pro1121_gr.databinding.BottomNavigationInChatBinding;
import com.example.pro1121_gr.databinding.SelectFontBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.function.RequestPermission;
import com.example.pro1121_gr.function.VoiceRecordingUtil;
import com.example.pro1121_gr.model.CustomTypefaceInfo;
import com.example.pro1121_gr.model.chatMesseageModel;
import com.example.pro1121_gr.model.chatRoomModel;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.DownloadReceiver;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private final String TAG = ChatActivity.class.getSimpleName();
    private userModel userModel;
    String chatRoomID;
    private String uriOther;
    private chatRoomModel chatRoomModel;
    private chatAdapter adapter;
    private ActivityChatBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private NetworkChangeReceiver networkChangeReceiver;
    private DownloadReceiver downloadReceiver;
    private static final int REQUEST_IMAGE_PICK = 100, REQUEST_IMAGE_CAPTURE = 1000,
            LOCATION_PERMISSION_REQUEST_CODE = 200, REQUEST_CODE_SPEECH_INPUT = 1, REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private String customTypeFace = "RobotoLightTextView";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // đăng ký sự kiện cuộc gọi voice call và video call
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(ChatActivity.this, task -> {
            if (task.isSuccessful()){
                userModel MyUserModel = task.getResult().toObject(userModel.class);
                if (MyUserModel != null) {
                    Functions.startService(MyUserModel.getUserId(), MyUserModel.getUsername(), this);
                } else Functions.showSnackBar(binding.getRoot(), "Error, please try again");
            } else Functions.showSnackBar(binding.getRoot(), "Error, please try again");
        });
        initView();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void initView(){
        // Bật chế độ tối nếu được kích hoạt
        MyApplication.applyNightMode();
        // Khởi tạo và đăng ký BroadcastReceiver
        networkChangeReceiver = Functions.getNetworkChangeReceiver(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Đăng ký theo dõi kết quả download ảnh
        downloadReceiver = new DownloadReceiver();
        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //get UserModel
        userModel = Functions.getUserModelFromIntent(getIntent());
        chatRoomID = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), userModel.getUserId());
        Functions.setVoiceCall(userModel.getUserId(), userModel.getUsername(), binding.call);
        Functions.setVideoCall(userModel.getUserId(), userModel.getUsername(), binding.videoCall);
        setUpCLickEvents();
        getDataChatRoom();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpCLickEvents(){
        binding.backFragmentMess.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.usernameMess.setText(userModel.getUsername().trim());

        binding.TextMESS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    binding.sendMess.setVisibility(View.VISIBLE);
                    binding.like.setVisibility(View.GONE);
                } else {
                    binding.sendMess.setVisibility(View.GONE);
                    binding.like.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.TextMESS.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                hiddenItem(true);
            }
            return false;
        });

        binding.rcvMess.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                hiddenItem(false);
                if (binding.TextMESS.getText().toString().isEmpty()) {
                    binding.sendMess.setVisibility(View.GONE);
                    binding.like.setVisibility(View.VISIBLE);
                }
                binding.TextMESS.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(binding.TextMESS.getWindowToken(), 0); // Ẩn bàn phím ảo (nếu đang hiển thị)
            }
            return false;
        });

        binding.sendMess.setOnClickListener(view -> {
            hiddenItem(false);
            sendMessToOther(binding.TextMESS.getText().toString().trim());
        });

        binding.imageMess.setOnClickListener(view -> {
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Quyền đã được cấp
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            } else
                RequestPermission.requestReadExternalStoragePermission(ChatActivity.this, REQUEST_IMAGE_PICK);

        });

        binding.cameraMess.setOnClickListener(view -> {
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.CAMERA)) {
                // Quyền đã được cấp
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            } else
                RequestPermission.requestCameraPermission(ChatActivity.this, REQUEST_IMAGE_CAPTURE);
        });


        binding.call.setOnClickListener(view -> {
        });

        binding.videoCall.setOnClickListener(view -> {
        });


        binding.optionInMess.setOnClickListener(view -> {
            showBottomDialog();
        });

        binding.like.setOnClickListener(view -> sendMessToOther("https://firebasestorage.googleapis.com/v0/b/du-an-1-197e4.appspot.com/o/like_icon%2Flike_icon.png?alt=media&token=63213f37-2681-412d-b096-177b20373976"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Quyền yêu cầu
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
                // Người dùng từ chối cấp quyền
            } else RequestPermission.showPermissionRationaleDialog(ChatActivity.this);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.CAMERA)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            } else RequestPermission.showPermissionRationaleDialog(ChatActivity.this);
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                checkGPS();
                // Người dùng từ chối cấp quyền vị trí
            } else RequestPermission.showPermissionRationaleDialog(ChatActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference imageRef = storageReference.child("chat_images/" + System.currentTimeMillis() + ".jpg");
        // xử lý gửi ảnh
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectImageUri = data.getData();
                UploadTask uploadTask = null;
                if (selectImageUri != null) {
                    uploadTask = imageRef.putFile(selectImageUri);
                }
                if (uploadTask != null) {
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Lấy URL của ảnh sau khi tải lên thành công
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            sendMessToOther(uri.toString());
                            setChatLayout();
                        });
                    });
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = null;
            if (data != null) {
                imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (imageBitmap != null) {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Lấy URL của ảnh sau khi tải lên thành công
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    sendMessToOther(imageUrl);
                    setChatLayout();
                });
            }).addOnFailureListener(exception -> Functions.showSnackBar( binding.getRoot(), "Lỗi xử lý ảnh"));
        } else if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            VoiceRecordingUtil.processVoiceInput(requestCode, resultCode, data, text -> {
                if (!text.isEmpty()) {
                    sendNotification(text);
                    sendMessToOther(text);
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessToOther(String message) {
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);

        FirebaseUtil.getChatRoomReference(chatRoomID).set(chatRoomModel);

        chatMesseageModel chatMesseageModel =
                new chatMesseageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), new CustomTypefaceInfo(customTypeFace));

        FirebaseUtil.getChatroomMessageReference(chatRoomID).add(chatMesseageModel);
        if (Functions.isURL(message)) sendNotification("[Link]");
        else sendNotification(message);
        binding.TextMESS.setText("");
    }

    private void sendNotification(String message) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userModel userModel1 = task.getResult().toObject(userModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject notification = new JSONObject();
                    if (userModel1 != null) {
                        notification.put("title", userModel1.getUsername());
                        notification.put("body", message);
                        notification.put("sound", "default");
                        JSONObject data = new JSONObject();
                        data.put("userId", userModel1.getUserId());
                        jsonObject.put("notification", notification);
                        jsonObject.put("data", data);
                        jsonObject.put("to", userModel.getFMCToken());
                        callAPI(jsonObject);
                    }

                } catch (Exception e) {
                    Log.e(ChatActivity.class.getSimpleName(), "notification: " + e.getMessage());
                }
            }
        });
    }


    private void callAPI(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder().url(url).post(body).header("Authorization", "Bearer AAAAzn2wU_4:APA91bGbYkKRd6E_tfTinPa_aBOnjjYLU39FJFubqWUUGOzV9uOEffB0gz_auOKjeJCqW7trohwb2aTSMFPcVVlTKD4NivbaubTDaKsHaWH3UMAom6pac6bMLfOA7ZhBQ1T1z1Tj1_vJ")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(ChatActivity.class.getSimpleName(), "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e(ChatActivity.class.getSimpleName(), "onResponse: " + response);
            }
        });
    }

    private void hiddenItem(boolean value) {
        if (value) {
            binding.optionInMess.setVisibility(View.VISIBLE);
            binding.imageMess.setVisibility(View.GONE);
            binding.cameraMess.setVisibility(View.GONE);
        } else {
            binding.optionInMess.setVisibility(View.GONE);
            binding.imageMess.setVisibility(View.VISIBLE);
            binding.cameraMess.setVisibility(View.VISIBLE);
        }
    }

    private void getDataChatRoom() {
        try {
            // Lấy đối tượng userModel
            userModel = Functions.getUserModelFromIntent(getIntent());

            chatRoomID = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), userModel.getUserId());

            // Lấy avatar
            FirebaseUtil.getCurrentOtherProfileImageStorageReference(userModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        uriOther = uri.toString();
                        FirebaseUtil.setAvatar(ChatActivity.this, uri, binding.avatarChat);
                        setChatLayout();
                    }
                }
            });
            FirebaseUtil.getChatRoomReference(chatRoomID).get().addOnCompleteListener(task -> {
                chatRoomModel = task.getResult().toObject(chatRoomModel.class);
                if (chatRoomModel == null) {
                    chatRoomModel = new chatRoomModel();
                    chatRoomModel.setChatroomId(chatRoomID);
                    chatRoomModel.setUserIds(Arrays.asList(FirebaseUtil.currentUserId(), userModel.getUserId()));
                    chatRoomModel.setLastMessageTimestamp(Timestamp.now());
                    chatRoomModel.setLastMessageSenderId("");
                }
                FirebaseUtil.getChatRoomReference(chatRoomID).set(chatRoomModel);
            });

        } catch (Exception e) {
            Log.e("getDataChatRoom", e.toString());
        }

    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void setChatLayout() {
        try {
            Query query = FirebaseUtil.getChatroomMessageReference(chatRoomID)
                    .orderBy("timestamp", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<chatMesseageModel> options = new FirestoreRecyclerOptions.Builder<chatMesseageModel>()
                    .setQuery(query, chatMesseageModel.class)
                    .build();

            adapter = new chatAdapter(options, ChatActivity.this, uriOther, chatRoomID, new chatAdapter.Download() {
                @Override
                public void downloadImage(String uri) {
                    DownloadReceiver.progressDownload(uri, ChatActivity.this, REQUEST_WRITE_EXTERNAL_STORAGE);
                }

                @Override
                public void clickImage(String model) {
                    Intent intent = new Intent(ChatActivity.this, DetailImageActivity.class);
                    intent.putExtra("message", model);
                    startActivity(intent);
                }
            });
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setReverseLayout(true);
            binding.rcvMess.setLayoutManager(manager);
            binding.rcvMess.setAdapter(adapter);
            adapter.startListening();

            // Cuộn màn hình xuống tin nhắn mới nhất khi gửi
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    binding.rcvMess.smoothScrollToPosition(0);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "setChatLayout : " + e.getMessage());
        }
    }

    private void showBottomDialog() {
        BottomNavigationInChatBinding binding = BottomNavigationInChatBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());

        binding.GPS.setOnClickListener(view -> {
            // Xử lý khi nhấn nút GPS
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Quyền đã được cấp
                checkGPS();
                dialog.dismiss();
            } else
                RequestPermission.requestLocationPermission(ChatActivity.this, LOCATION_PERMISSION_REQUEST_CODE);
        });

        binding.Fonts.setOnClickListener(view -> {
            dialog.dismiss();
            showFont();
        });

        binding.Voice.setOnClickListener(view -> {
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO)) {
                VoiceRecordingUtil.startVoiceRecognitionActivity(ChatActivity.this);
                dialog.dismiss();
            } else {
                RequestPermission.requestRecordAudio(ChatActivity.this, REQUEST_CODE_SPEECH_INPUT);
            }
        });

        binding.cancelButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogAnimation);
            window.setGravity(Gravity.BOTTOM);
        }

    }

    private void showFont(){
        SelectFontBinding selectFontBinding = SelectFontBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(selectFontBinding.getRoot());

        selectFontBinding.blackjack.setOnClickListener(view -> {
            customTypeFace = "BlackjackTextview";
            dialog.dismiss();
        });

        selectFontBinding.allura.setOnClickListener(view -> {
            customTypeFace = "AlluraTextView";
            dialog.dismiss();
        });

        selectFontBinding.robotoBold.setOnClickListener(view -> {
            customTypeFace = "RobotoBoldTextView";
            dialog.dismiss();
        });

        selectFontBinding.robotoItalic.setOnClickListener(view -> {
            customTypeFace = "RobotoItalicTextView";
            dialog.dismiss();
        });

        selectFontBinding.robotoLight.setOnClickListener(view -> {
            customTypeFace = "RobotoLightTextView";
            dialog.dismiss();
        });

        selectFontBinding.cancelButton.setOnClickListener(view -> dialog.dismiss());


        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
        }
    }


    private void checkGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this.getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                // gps on
                LocationSettingsResponse apiException = task.getResult(ApiException.class);
                getUserLocation();
            } catch (ApiException e) {
                // gps off
                if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    // Hiển thị cửa sổ đề xuất bật GPS
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(ChatActivity.this, 123);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        Log.e(TAG, "checkGPS: " + sendIntentException.getMessage());
                    }
                } else {
                    // GPS đã bị tắt hoặc có lỗi khác
                    Functions.showSnackBar(binding.getRoot(), "Error, please try again");
                }
            }
        });
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Kiểm tra và yêu cầu quyền truy cập vị trí nếu cần
            RequestPermission.requestLocationPermission(ChatActivity.this, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                try {
                    Geocoder geocoder = new Geocoder(ChatActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(task.getResult().getLatitude(), task.getResult().getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        String addressString = addresses.get(0).getAddressLine(0);
                        sendMessToOther(addressString);
                    } else {
                        Functions.Toasty(ChatActivity.this,"Không thể định vị!", Functions.warning);
                        Log.e(TAG, "getUserLocation: Addresses is null or empty");
                    }
                } catch (IOException e) {
                    Functions.Toasty(ChatActivity.this,"Không thể định vị!", Functions.warning);
                    Log.e(TAG, "getUserLocation: IOException - " + e.getMessage());
                }
            } else {
                Toasty.warning(ChatActivity.this,"Không thể định vị!", Toasty.LENGTH_LONG, true).show();
                Log.e(TAG, "getUserLocation: Unable to get location - " + task.getException());
            }
        });
    }


    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("DOWNLOAD_COMPLETE".equals(action)) {
                Functions.showSnackBar(binding.getRoot(), "Downloaded successfully");
            }else Functions.showSnackBar(binding.getRoot(), "Download failed");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
        registerReceiver(downloadCompleteReceiver, new IntentFilter("DOWNLOAD_COMPLETE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadCompleteReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký BroadcastReceiver khi hoạt động bị hủy
        if (networkChangeReceiver != null) unregisterReceiver(networkChangeReceiver);
        if (downloadReceiver != null) unregisterReceiver(downloadReceiver);
        // stop service
        ZegoUIKitPrebuiltCallInvitationService.unInit();
        DBhelper.getInstance(this).endUsageTracking();
    }

}