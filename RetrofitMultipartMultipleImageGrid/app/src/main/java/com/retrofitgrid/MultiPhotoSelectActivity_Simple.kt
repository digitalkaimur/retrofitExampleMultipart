package com.retrofitgrid

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.immigration.restservices.APIService
import com.immigration.restservices.ApiUtils
import com.retrofitdemo.retrofit.model.ResponseModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*
import javax.security.auth.callback.Callback




class MultiPhotoSelectActivity_Simple : AppCompatActivity() {
    lateinit var apiService: APIService
    private var imageAdapter: ImageAdapter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_multi_photo_select)
        apiService = ApiUtils.apiService
        populateImagesFromGallery()
    }

    fun btnChoosePhotosClick(v: View) {
        val selectedItems = imageAdapter!!.checkedItems

        if (selectedItems !=
                null && selectedItems.size > 0) {
            Toast.makeText(this@MultiPhotoSelectActivity_Simple, "Total photos selected: " + selectedItems.size, Toast.LENGTH_SHORT).show()
            Log.d(MultiPhotoSelectActivity_Simple::class.java.simpleName, "Selected Items: " + selectedItems.toString())
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)



            for (i in selectedItems.indices) {
                val imgFile = File(selectedItems[i])
                if (imgFile.exists()) {
                    val reqFile = RequestBody.create(MediaType.parse("image"), imgFile)
                    val body = MultipartBody.Part.createFormData("file", imgFile.name, reqFile)

                    apiService.multipleImageUpload(body).enqueue(object : Callback, retrofit2.Callback<ResponseModel> {
                        override fun onResponse(call: Call<ResponseModel>?, response: Response<ResponseModel>?) {
                            val status = response!!.code()
                            Log.d("TAGS", "status " + status.toString())

                            when (status) {

                                200 -> {
                                    Log.d("TAGS", "status 200 " + status.toString())
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseModel>?, t: Throwable?) {
                            Log.d("TAGS", "Throwable " + t.toString())

                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })
                }
            }
        }
    }




    private fun populateImagesFromGallery() {
        if (!mayRequestGalleryImages()) {
            return
        }
        val imageUrls = loadPhotosFromNativeGallery()
        initializeRecyclerView(imageUrls)
    }
    private fun mayRequestGalleryImages(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            //promptStoragePermission();
            showPermissionRationaleSnackBar()
        } else {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQUEST_FOR_STORAGE_PERMISSION)
        }

        return false
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            REQUEST_FOR_STORAGE_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        populateImagesFromGallery()
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                            showPermissionRationaleSnackBar()
                        } else {
                            Toast.makeText(this, "Go to settings and enable permission", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
    private fun loadPhotosFromNativeGallery(): ArrayList<String> {
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC")
        val imageUrls = ArrayList<String>()

        for (i in 0 until imagecursor.count) {
            imagecursor.moveToPosition(i)
            val dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA)
            imageUrls.add(imagecursor.getString(dataColumnIndex))

            println("=====> Array path => " + imageUrls[i])
        }

        return imageUrls
    }



    private fun initializeRecyclerView(imageUrls: ArrayList<String>) {
        imageAdapter = ImageAdapter(this, imageUrls)
        val layoutManager = GridLayoutManager(applicationContext, 2)
        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(ItemOffsetDecoration(this, R.dimen.item_offset))
        recyclerView.adapter = imageAdapter
    }
    private fun showPermissionRationaleSnackBar() {
        Snackbar.make(findViewById(R.id.button1), getString(R.string.permission_rationale),
                Snackbar.LENGTH_INDEFINITE).setAction("OK") {
            // Request the permission
            ActivityCompat.requestPermissions(this@MultiPhotoSelectActivity_Simple,
                    arrayOf(READ_EXTERNAL_STORAGE),
                    REQUEST_FOR_STORAGE_PERMISSION)
        }.show()
    }
    companion object {
        private val REQUEST_FOR_STORAGE_PERMISSION = 123
    }
}
