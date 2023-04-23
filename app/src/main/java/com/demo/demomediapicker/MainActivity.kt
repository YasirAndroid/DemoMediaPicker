package com.demo.demomediapicker

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.demomediapicker.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var newUri: Uri

    val requestMultiplePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->

        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                val imageList = arrayListOf(
                    newUri
                )
                var intent = Intent(this, DataListActivity::class.java).also {
                    it.putExtra("dataList", imageList)
                    it.putExtra("type", 0)
                    startActivity(it)
                }
            }
        }

    private val captureVideo =
        registerForActivityResult(ActivityResultContracts.CaptureVideo()) { isSuccess ->
            if (isSuccess) {
                val imageList = arrayListOf(
                    newUri
                )
                var intent = Intent(this, DataListActivity::class.java).also {
                    it.putExtra("dataList", imageList)
                    it.putExtra("type", 1)
                    startActivity(it)
                }
            }
        }

    private var imagePickerResultActivityLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents())
        { result ->
            if (result.size >= 0) {
                var intent = Intent(this, DataListActivity::class.java).also {
                    it.putExtra("dataList", ArrayList(result))
                    it.putExtra("type", 0)
                    startActivity(it)
                }
            }
        }

    private var videoPickerResultActivityLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents())
        { result ->
            if (result.size >= 0) {
                var intent = Intent(this, DataListActivity::class.java).also {
                    it.putExtra("dataList", ArrayList(result))
                    it.putExtra("type", 1)
                    startActivity(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnImagePicker.setOnClickListener {
            if (checkAndGetPermission()) {
                customDialog(type = 0)
            }
        }
        binding.btnVideoPicker.setOnClickListener {
            if (checkAndGetPermission()) {
                customDialog(type = 1)
            }
        }
    }

    private fun checkAndGetPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            requestMultiplePermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                )
            )
        }
        return false
    }

    private fun takeImage() {
        getTmpFileUri(type = 0).let { uri ->
            newUri = uri
            takePicture.launch(uri)
        }
    }

    private fun takeVideo() {
        getTmpFileUri(type = 1).let { uri ->
            newUri = uri
            captureVideo.launch(uri)
        }
    }

    private fun getTmpFileUri(type: Int): Uri {
        val tmpFile = File.createTempFile("tmp_file", if (type==0) ".png" else ".mp4", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    private fun customDialog(type: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_layout)
        val gallery = dialog.findViewById(R.id.ll_gallery_picker) as LinearLayout
        val camera = dialog.findViewById(R.id.ll_camera_picker) as LinearLayout
        gallery.setOnClickListener {
            if (type==0) {
                imagePickerResultActivityLauncher.launch("image/*")
            }
            else {
                videoPickerResultActivityLauncher.launch("video/*")
            }
        }
        camera.setOnClickListener {
            if (type==0) {
                takeImage()
            }
            else {
                takeVideo()
            }
        }
        dialog.show()
    }
}