package com.multipleimage.uploadfile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.multipleimage.R;

import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
   // private static final String SERVER_PATH = "http://worklime.com";
    private static final String SERVER_PATH = "http://18.221.82.73:3004"; //bickup
    private static final String  access_token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6Ijk1OTk4MzQ3MzUiLCJpYXQiOjE1MTk0MTA2MzgsImV4cCI6MTUxOTQxNDIzOH0.Z-6vO4VvKEABNrEoDj3W24Ljjkyt67mAopYA012VgHY";

    private Uri uri;
    private APIService uploadAPIService;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button selectUploadButton = (Button) findViewById(R.id.select_image);
        selectUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);

            }
        });
        Button uploadMultiFile = (Button) findViewById(R.id.upload_multi_file);
        uploadMultiFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadMultiFile();
            }
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Change base URL to your upload server URL.
        uploadAPIService = new Retrofit.Builder()
                .baseUrl(SERVER_PATH)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                progressDialog.show();
                String filePath = getRealPathFromURIPath(uri, MainActivity.this);
                File file = new File(filePath);
                Log.d(TAG, "filePath=" + filePath);
                //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("profilePic", file.getName(), mFile);

                RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"),"KArun");
                RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), "Noida");
                RequestBody contact = RequestBody.create(MediaType.parse("text/plain"),"8920828585");
                RequestBody countryCode = RequestBody.create(MediaType.parse("text/plain"), "+91");

                Call<Model> fileUpload = uploadAPIService.
                        uploadSingleFile("10448859925a8d6d090c67f", fileToUpload,
                                firstName,lastName,contact,countryCode);
                fileUpload.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Response " + response.raw().message(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this, "Success " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Model> call, Throwable t) {
                        progressDialog.dismiss();

                        Log.d(TAG, "Error " + t.getMessage());
                    }

                });
            } else {
                EasyPermissions.requestPermissions(this,"This app needs access to your file storage so that it can read photos.", READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        if (uri != null) {
            progressDialog.show();
        }




      /*  String filePath = getRealPathFromURIPath(uri, MainActivity.this);
        File file = new File(filePath);
        RequestBody mFile = RequestBody.create(MediaType.parse("image*//*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        Call<Model> fileUpload = uploadAPIService.uploadSingleFile(fileToUpload, filename);

        fileUpload.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Success " + response.body().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                progressDialog.dismiss();
                Log.d(TAG, "Error " + t.getMessage());
            }
        });*/

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }













    private void uploadMultiFile() {
        progressDialog.show();
        String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String targetPath = ExternalStorageDirectoryPath + "/pic/";
        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(targetPath+"/a.jpg");
        filePaths.add(targetPath+"/b.jpg");



        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("type_of_goods","[]")
                .addFormDataPart("helper", "[]")
                .addFormDataPart("no_of_helpers", "1")
                .addFormDataPart("pickup_contact_name","jhefjk")
                .addFormDataPart("pickup_contact_number","954596565")
                .addFormDataPart("pickup_comments", "54545")
                .addFormDataPart("pickup_home_type", "1")
                .addFormDataPart("pickup_villa_name","54wew")
                .addFormDataPart("pickup_building_name", "44545")
                .addFormDataPart("pickup_floor_number", "wdwf22")
                .addFormDataPart("pickup_unit_number", "d334")
                .addFormDataPart("pickup_latitude","28.608038920882798")
                .addFormDataPart("pickup_longitude", "77.38677632063626")
                .addFormDataPart("drop_latitude","28.608913119550134")
                .addFormDataPart("drop_longitude","77.38610476255417")
                .addFormDataPart("drop_location_address","dgdgdeg")
                .addFormDataPart("drop_contact_name","954565555")
                .addFormDataPart("drop_contact_number","945534353")
                .addFormDataPart("drop_comments","fdfdg")
                .addFormDataPart("drop_home_type", "2")
                .addFormDataPart("drop_villa_name", "eterge232")
                .addFormDataPart("drop_building_name","343dd")
                .addFormDataPart("drop_floor_number", "343dsd")
                .addFormDataPart("drop_unit_number", "434")
                .addFormDataPart("ride_description", "efdsff")
                .addFormDataPart("distance", "343")
                .addFormDataPart("pickup_time_type", "1")
                .addFormDataPart("pickup_time", "1519375905")
                .addFormDataPart("total_price", "232")
                .addFormDataPart("paid_by_type", "1")
                .addFormDataPart("paid_by_name","ehgfhg")
                .addFormDataPart("paid_by_contact_number", "9656454343")
                .addFormDataPart("pickup_location_address","ret43433");

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        MultipartBody requestBody = builder.build();
        Call<ResponseBody> call = uploadAPIService.uploadMultiFile(access_token,requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

               Toast.makeText(MainActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
               Toast.makeText(MainActivity.this, "Success " + response.body().toString(), Toast.LENGTH_LONG).show();

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.d(TAG, "Error " + t.getMessage());
            }
        });


    }
}

