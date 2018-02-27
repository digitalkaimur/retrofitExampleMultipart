package com.retrofitprofile.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.immigration.restservices.APIService
import com.immigration.restservices.ApiUtils
import com.retrofitdemo.retrofit.model.ResponseModel
import com.retrofitprofile.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.net.URI
import java.net.URISyntaxException

class MultiPartProfileActivity : AppCompatActivity() {

    private var apiService: APIService? = null
    private val TAG = MultiPartProfileActivity::class.java.getName()

    private val SELECT_PICTURE = 170
    private var TAKE_PIC = 291
    private val REQUEST_CODE_STORAGE_PERMS = 501
    private var outPutfileUri: Uri? = null
    private var bitImg: Bitmap? = null
    private var file: File? = null
    private var resultUri: Uri? = null
    private lateinit var pb: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiService = ApiUtils.apiService

        pb = findViewById<ProgressBar>(R.id.progressBar)

        btn_opencamera.setOnClickListener {
            AskPermissions()
        }

        btn_submit.setOnClickListener {
            initJsonOperationPost()
        }
    }


    //Camera

    //ASK Runtime permisstion camera open
    private fun AskPermissions() {
        if (applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if (!hasPermissions()) {
                requestNecessaryPermissions()
            } else {
                selectImage()
            }
        } else {
            Toast.makeText(applicationContext, "Camera not supported", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("WrongConstant")
    private fun hasPermissions(): Boolean {
        var res: Int
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        for (perms in permissions) {
            res = applicationContext.checkCallingOrSelfPermission(perms)
            if (res != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestNecessaryPermissions() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE_STORAGE_PERMS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grandResults: IntArray) {
        var allowed = true
        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMS -> for (res in grandResults) {
                allowed = allowed && res == PackageManager.PERMISSION_GRANTED
            }
            else -> allowed = false
        }
        if (allowed) {
            selectImage()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(applicationContext, "Camera Permissions denied", Toast.LENGTH_SHORT).show()
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(applicationContext, "Storage Permissions denied", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    //ASK Runtime permisstion camera end

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder = android.support.v7.app.AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                dialog.dismiss()
                takeImageFromCamera()
            } else if (options[item] == "Choose From Gallery") {
                dialog.dismiss()
                choosefromgallery()

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()

    }

    private fun takeImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(Environment.getExternalStorageDirectory(), System.currentTimeMillis().toString() + ".jpg")
        outPutfileUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri)
        startActivityForResult(intent, TAKE_PIC)
    }

    private fun choosefromgallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_PICTURE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKE_PIC && resultCode == Activity.RESULT_OK) {
            if (null != outPutfileUri) {
                crop_Method(outPutfileUri!!)
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                val selectedImageUri = data!!.data
                if (null != selectedImageUri) {
                    crop_Method(selectedImageUri)
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.uri
                val path = resultUri.toString()
                var bitmap: Bitmap? = null
                try {
                    file = File(URI(path))
                    profile_image.setImageURI(resultUri)
                    Log.d(" profilePicPath", "file " + file!!.path)

                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                    if (bitmap != null) {
                        bitImg = bitmap
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }

        }

    }

    private fun crop_Method(imageUri: Uri) {
        CropImage.activity(imageUri).start(this@MultiPartProfileActivity)
    }


    //API Parse
    private fun initJsonOperationPost() {

        pb.visibility = View.VISIBLE

        val mFile = RequestBody.create(MediaType.parse("image/*"), file)
        val fileToUpload = MultipartBody.Part.createFormData("profilePic", file!!.getName(), mFile)


        val firstName = "KArun"
        val lastName = "Singh"
        val countryCodeValues = "+91"
        val contactValues = "8920828585"
        val accessToken = "10448859925a8d6d090c67f"

        val fname = RequestBody.create(MediaType.parse("text/plain"), firstName)
        val lname = RequestBody.create(MediaType.parse("text/plain"), lastName)
        val cnt_code = RequestBody.create(MediaType.parse("text/plain"), countryCodeValues)
        val mobile = RequestBody.create(MediaType.parse("text/plain"), contactValues)

        apiService!!.postImage(accessToken, fileToUpload, fname, lname, mobile, cnt_code)
                .enqueue(object : retrofit2.Callback<ResponseModel> {
                    override fun onResponse(call: retrofit2.Call<ResponseModel>?, response: retrofit2.Response<ResponseModel>?) {
                        pb.visibility = View.GONE
                        if (response!!.isSuccessful) {
                            Log.d("TAGS", "response " + response!!.body().message)
                        } else
                            Log.d("TAGS", "errorHandler" + errorHandler(response))
                    }

                    override fun onFailure(call: retrofit2.Call<ResponseModel>?, t: Throwable?) {
                        pb.visibility = View.GONE

                        Log.d("TAGS", t.toString())
                    }
                })

    }

    private fun errorHandler(response: Response<ResponseModel>?): String {
        return try {
            val jObjError = JSONObject(response!!.errorBody().string())
            jObjError.getString("message")
        } catch (e: Exception) {
            e.message!!
        }
    }


}
