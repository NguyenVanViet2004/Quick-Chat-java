package com.example.pro1121_gr.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.adapter.chatAdapter;
import com.example.pro1121_gr.custom_textview.utils;
import com.example.pro1121_gr.databinding.ActivityChatBinding;
import com.example.pro1121_gr.databinding.BottomNavigationInChatBinding;
import com.example.pro1121_gr.databinding.ChatMessageLayoutBinding;
import com.example.pro1121_gr.databinding.SelectFontBinding;
import com.example.pro1121_gr.function.RequestPermission;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.CustomTypefaceInfo;
import com.example.pro1121_gr.model.chatMesseageModel;
import com.example.pro1121_gr.model.chatRoomModel;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
    private Uri selectImageUri;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_IMAGE_PICK = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1000;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;

    private String customTypeFace = "RobotoLightTextView";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView(){
        // Bật chế độ tối nếu được kích hoạt
        MyApplication.applyNightMode();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //get UserModel
        userModel = StaticFunction.getUserModelFromIntent(getIntent());
        chatRoomID = firebaseUtil.getChatroomId(firebaseUtil.currentUserId(), userModel.getUserId());
        setUpCLickEvents();
        getDataChatRoom();
    }

    private void setUpCLickEvents(){
        binding.backFragmentMess.setOnClickListener((View.OnClickListener) view -> {
            startActivity(new Intent(ChatActivity.this, home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });

        binding.usernameMess.setText(userModel.getUsername());

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

        binding.TextMESS.setOnTouchListener((View.OnTouchListener) (view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                hiddenItem(true);
            }
            return false;
        });

        binding.rcvMess.setOnTouchListener((View.OnTouchListener) (view, motionEvent) -> {
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

        binding.sendMess.setOnClickListener((View.OnClickListener) view -> {
            hiddenItem(false);
            sendMessToOther(binding.TextMESS.getText().toString().trim());
        });

        binding.imageMess.setOnClickListener((View.OnClickListener) view -> {
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Quyền đã được cấp
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            } else
                RequestPermission.requestReadExternalStoragePermission(ChatActivity.this, REQUEST_IMAGE_PICK);

        });

        binding.cameraMess.setOnClickListener((View.OnClickListener) view -> {
            if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.CAMERA)) {
                // Quyền đã được cấp
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            } else
                RequestPermission.requestCameraPermission(ChatActivity.this, REQUEST_IMAGE_CAPTURE);
        });

        binding.call.setOnClickListener((View.OnClickListener) view -> {
            // Tạo một Intent với hành động ACTION_DIAL
            Intent intent = new Intent(Intent.ACTION_DIAL);

            // Đặt dữ liệu Uri cho số điện thoại cần gọi
            intent.setData(Uri.parse("tel:" + userModel.getPhone()));

            // Kiểm tra xem ứng dụng Gọi điện thoại có sẵn trên thiết bị hay chưa
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Nếu có, mở ứng dụng Gọi điện thoại
                startActivity(intent);
            } else {
                Toasty.warning(ChatActivity.this, "Không tìm thấy ứng dụng phù hợp", Toasty.LENGTH_SHORT, true).show();
            }
        });

        binding.videoCall.setOnClickListener((View.OnClickListener) view -> {
            // Số điện thoại người dùng muốn gọi
            String phoneNumber = userModel.getPhone();
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phoneNumber));
                intent.putExtra("videocall", true);

                // Gọi ứng dụng Gọi điện thoại hoặc xử lý trường hợp không tìm thấy
                startActivity(intent);
            } catch (Exception e) {
                Log.e("call click in chatActivity", e.getMessage());
            }
        });


        binding.optionInMess.setOnClickListener(view -> {
            showBottomDialog();
        });

        binding.like.setOnClickListener((View.OnClickListener) view -> sendMessToOther("https://firebasestorage.googleapis.com/v0/b/du-an-1-197e4.appspot.com/o/like_icon%2Flike_icon.png?alt=media&token=63213f37-2681-412d-b096-177b20373976"));

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
                selectImageUri = data.getData();
                UploadTask uploadTask = imageRef.putFile(selectImageUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh sau khi tải lên thành công
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        sendMessToOther(uri.toString());
                        setChatLayout();
                    });
                });
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Lấy URL của ảnh sau khi tải lên thành công
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    sendMessToOther(imageUrl);
                    setChatLayout();
                });
            }).addOnFailureListener(exception -> StaticFunction.showWarning(ChatActivity.this, "Đã xảy ra lỗi trong quá trình xử lý ảnh!"));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessToOther(String message) {
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(firebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);

        firebaseUtil.getChatRoomReference(chatRoomID).set(chatRoomModel);

        chatMesseageModel chatMesseageModel =
                new chatMesseageModel(message, firebaseUtil.currentUserId(), Timestamp.now(), new CustomTypefaceInfo(customTypeFace));

        firebaseUtil.getChatroomMessageReference(chatRoomID).add(chatMesseageModel);
        sentotification(message);
        binding.TextMESS.setText("");
    }

    private void sentotification(String message) {
        firebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userModel userModel1 = task.getResult().toObject(userModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("title", userModel1.getUsername());
                    notification.put("body", message);
                    JSONObject data = new JSONObject();
                    data.put("userId", userModel1.getUserId());
                    jsonObject.put("notification", notification);
                    jsonObject.put("data", data);
                    jsonObject.put("to", userModel.getFMCToken());
                    callAPI(jsonObject);
                    Log.e(TAG, "my token222: " + userModel.getFMCToken());

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
            userModel = StaticFunction.getUserModelFromIntent(getIntent());

            chatRoomID = firebaseUtil.getChatroomId(firebaseUtil.currentUserId(), userModel.getUserId());

            // Lấy avatar
            firebaseUtil.getCurrentOtherProfileImageStorageReference(userModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        uriOther = uri.toString();
                        Log.e(TAG, "getdata: " + uriOther);
                        firebaseUtil.setAvatar(ChatActivity.this, uri, binding.avatarChat);
                        setChatLayout();
                    }
                }
            });
            firebaseUtil.getChatRoomReference(chatRoomID).get().addOnCompleteListener(task -> {
                chatRoomModel = task.getResult().toObject(chatRoomModel.class);
                if (chatRoomModel == null) {
                    chatRoomModel = new chatRoomModel();
                    chatRoomModel.setChatroomId(chatRoomID);
                    chatRoomModel.setUserIds(Arrays.asList(firebaseUtil.currentUserId(), userModel.getUserId()));
                    chatRoomModel.setLastMessageTimestamp(Timestamp.now());
                    chatRoomModel.setLastMessageSenderId("");
                }
                firebaseUtil.getChatRoomReference(chatRoomID).set(chatRoomModel);
            });

        } catch (Exception e) {
            Log.e("getDataChatRoom", e.toString());
        }

    }

    private void setChatLayout() {
        //try {
        Query query = firebaseUtil.getChatroomMessageReference(chatRoomID)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<chatMesseageModel> options = new FirestoreRecyclerOptions.Builder<chatMesseageModel>()
                .setQuery(query, chatMesseageModel.class)
                .build();

        adapter = new chatAdapter(options, getApplicationContext(), uriOther.toString());
        Log.e(TAG, "onComplete: " + uriOther.toString());
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

        /*} catch (Exception e) {
            Log.e(TAG, "setChatLayout : "+e.getMessage());
        }*/
    }

    private void showBottomDialog() {
        BottomNavigationInChatBinding binding = BottomNavigationInChatBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());

        binding.GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý khi nhấn nút GPS
                if (RequestPermission.checkPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Quyền đã được cấp
                    checkGPS();
                    dialog.dismiss();
                } else
                    RequestPermission.requestLocationPermission(ChatActivity.this, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });

        binding.Fonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showFont();
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

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
        locationRequest = LocationRequest.create();
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
                        Log.e(TAG, "getUserLocation: Addresses is null or empty");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "getUserLocation: IOException - " + e.getMessage());
                }
            } else {
                Log.e(TAG, "getUserLocation: Unable to get location - " + task.getException());
            }
        });
    }




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

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}