package com.acquaintsoft.kotlinapi

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.acquaintsoft.kotlinapi.models.UserModel
import com.acquaintsoft.kotlinapi.utils.*
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.ArrayList
import java.util.HashMap

class RegisterActivity : AppCompatActivity(), ApiResponseListener {

    lateinit var apiController: ApiController
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        apiController = ApiController(context)
        setContentView(R.layout.activity_register)
        ivUser.setOnClickListener(View.OnClickListener { view ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 101)

                } else {
                    openImageIntent()
                }
            } else {
                openImageIntent()
            }
        })
        buttonSubmit.setOnClickListener(View.OnClickListener { view ->
            val params = HashMap<String, String>()
            params.put("action", "register")
            params.put("name", editTextName.text.toString())
            params.put("mobile", editTextMobile.text.toString())
            params.put("email", editTextEmail.text.toString())
            params.put("password", editTextPassword.text.toString())

            val file = HashMap<String, VolleyMultipartRequest.DataPart>()
            if (imagesModel != null) {
                file.put("user_image", imagesModel!!)
            }
            apiController.actionCallWebServiceWithFiles(Constants.MAIN_URL, params, file);
        })
    }

    private lateinit var fileName: String

    private lateinit var captureFile: File

    private lateinit var outputFileUri: Uri

    private fun openImageIntent() {

        // Determine Uri of camera image to save.
        val root = File(Environment.getExternalStorageDirectory().toString() + File.separator + context.resources.getString(R.string.app_name) + File.separator)
        root.mkdirs()
        fileName = "IMG_" + System.currentTimeMillis() + ".png"
        captureFile = File(root, fileName)

        outputFileUri = Uri.fromFile(captureFile)

        // Camera.
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = context.packageManager
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.putExtra("return-data", true)
            intent.component = ComponentName(packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            cameraIntents.add(intent)
            break
        }

        // Filesystem.
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_PICK

        // Chooser of filesystem options.
        val chooserIntent = Intent.createChooser(galleryIntent, "Select Source")

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray<Parcelable>())

        startActivityForResult(chooserIntent, 101)
    }

    override fun onSuccessResponse(response: String, hashMap: HashMap<String, String>) {
        if (hashMap.get("action") != null && hashMap.get("action").equals("register")) {
            var userModel = Gson().fromJson(response, UserModel::class.java)
            if (userModel?.meta?.status.equals("success") && userModel?.meta?.code.equals("200")) {
                val myIntent = Intent(context, MainActivity::class.java)
                myIntent.putExtra("data", userModel.userDetails)
                startActivity(myIntent)
                Common.showToast(context, userModel?.meta?.message!!)

            } else {
                Common.showToast(context, userModel?.meta?.message!!)
            }

        }
    }

    override fun onErrorResponse(error: VolleyError, hashMap: HashMap<String, String>) {

    }

    private var imagesModel: VolleyMultipartRequest.DataPart? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode != 0) {
            imagesModel = VolleyMultipartRequest.DataPart()
            var uri: Uri? = null
            if (data != null && data.data != null) {
                uri = data.data
            } else {
                var url = MediaStore.Images.Media.insertImage(contentResolver, captureFile.absolutePath, captureFile.name, captureFile.name)
                uri = Uri.parse(url);
            }

            imagesModel?.imageUri = uri
            val c = context.contentResolver.query(uri!!, null, null, null, null)
            c!!.moveToFirst()

            val type = c.getString(c.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
            val file_name = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
            c.close()
            imagesModel?.fileName = file_name
            imagesModel?.type = type


            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            val baos = ByteArrayOutputStream()
            if (type.contains("png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100 /* Ignored for PNGs */, baos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /* Ignored for PNGs */, baos);
            }

            val fileBytes = baos.toByteArray()
            imagesModel?.content = fileBytes
//            imagesModel.type = "image/*"
            Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(ivUser)

        }
    }
}
