package com.transporterusers;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;


import com.transporterusers.databinding.CreateProfileBinding;
import com.transporterusers.api.UserService;



import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {
    CreateProfileBinding binding;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = CreateProfileBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
        }

        binding.btnEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in, 1);
            }
        });



        binding.btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etusername.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    binding.etusername.setError("Username required");
                }
                String address = binding.tvaddress.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    binding.tvaddress.setError("address is required ");
                }
                String phoneNumber = binding.etphonenumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber))
                    binding.etphonenumber.setError("Phone number is required");
                String token = "4324343434343432432434343434";
                if (imageUri != null) {
                    File file = FileUtils.getFile(ProfileActivity.this,imageUri);
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(imageUri)),
                                    file
                            );

                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                    RequestBody userName = RequestBody.create(
                            okhttp3.MultipartBody.FORM, name);

                    RequestBody userAddress = RequestBody.create(okhttp3.MultipartBody.FORM, address);

                    RequestBody userContact = RequestBody.create(okhttp3.MultipartBody.FORM, phoneNumber);

                    RequestBody userToken = RequestBody.create(okhttp3.MultipartBody.FORM, token);

                    UserService.UserApi userApi = UserService.getUserApiInstance();
                    Call<User> call = userApi.saveProfile(body, userName, userAddress, userContact, userToken);

                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.code() == 200) {
                                User user = response.body();
                                Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(ProfileActivity.this, "Failed : " + t, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    Toast.makeText(ProfileActivity.this, "Please select profile pic", Toast.LENGTH_SHORT).show();
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Delete");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            binding.civ.setImageURI(imageUri);
        }
    }
}
