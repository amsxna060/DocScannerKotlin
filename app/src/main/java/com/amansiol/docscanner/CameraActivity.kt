package com.amansiol.docscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.*
import android.text.Layout
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.ImageCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException

class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null

    private var isFlashOn = false
    lateinit var cameraControl: CameraControl

    var currentImageNumberToHandle: Int = -1
    lateinit var gallery: ImageView
    var currentBitmap: Bitmap? = null

    override fun onBackPressed() {
        if (currentImageNumberToHandle == -1) {
            //We have pressed back with only images captured and no editing so far
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.MyTheme)
            val v: View = LayoutInflater.from(this).inflate(R.layout.leave_all_dialog, null, false)
            builder.setView(v)

            val leaveButton: Button = v.findViewById(R.id.sure_leave)
            val noBtn: Button = v.findViewById(R.id.stay)

            val alertDialog: AlertDialog = builder.create()

            alertDialog.show()

            leaveButton.setOnClickListener {
                finish()
                CreatingPdf.bitmapFileArray.clear()
                CreatingPdf.bitmapArray.clear()
            }

            noBtn.setOnClickListener {
                alertDialog.dismiss()
            }

        } else {
            val intent = Intent(this@CameraActivity, CropActivity::class.java)
            intent.putExtra("modified", currentImageNumberToHandle)
            startActivity(intent)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.RC_CAMERA) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this@CameraActivity, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == Constants.RC_WRITE_EXTERNAL_STORAGE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("CameraActivity", "write to external storage permission granted")
            } else {
                Toast.makeText(this@CameraActivity, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getImageFromGallery(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                val uri: Uri = data.data!!
                currentBitmap = getBitmapFromUri(uri)
                val dir = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}")
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED) {
                    if (!dir.exists()) {
                        dir.mkdir()
                    }
                }
                else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Constants.RC_WRITE_EXTERNAL_STORAGE
                    )
                }
                val tempFile: File = File(dir, "temp_${CreatingPdf.bitmapFileArray.size}.jpeg")
                try {
                    FileOutputStream(tempFile).use { out ->
                        currentBitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }

                    CreatingPdf.bitmapFileArray.add(tempFile)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private var soundPool: SoundPool? = null
    private var sound1 = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        sound1 = soundPool!!.load(this, R.raw.camerashutter, 1)
        gallery = findViewById(R.id.pickfromgallery)

        currentImageNumberToHandle = intent.getIntExtra("position_in_array", -1)

        if (currentImageNumberToHandle != -1) {
            gallery.visibility = View.GONE
            done_capturing.visibility = View.GONE
//            prev_image.visibility = View.GONE
            current.visibility = View.GONE
            photo_number.visibility = View.GONE
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()

            //capture photo by clicking button
            capture.setOnClickListener {
                soundPool!!.play(sound1, 1f, 1f, 0, 0, 1f)
                takePhoto()
            }

            done_capturing.setOnClickListener{
                val intent: Intent = Intent(this@CameraActivity, CropActivity::class.java)
                startActivity(intent)
                finish()
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                Constants.RC_CAMERA
            )
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture?: return

        val dir = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_FOLDER_NAME}")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (!dir.exists()) {
                dir.mkdir()
            }
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.RC_WRITE_EXTERNAL_STORAGE
            )
        }
        val tempFile: File
        tempFile = if (currentImageNumberToHandle == -1)
            File(dir, "temp_${CreatingPdf.bitmapFileArray.size}.jpeg")
        else
            File(dir, "temp_${currentImageNumberToHandle}.jpeg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Camera image captured failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    //val uri: Uri = FileProvider.getUriForFile(this@CameraActivity,applicationContext.packageName + ".provider",tempFile)
                    currentBitmap = decodeSampledBitmapFromFile(tempFile, 170, 170)

                    val hide: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.animate_slide_down_exit)

                    if (currentImageNumberToHandle == -1) {
                        current.setImageBitmap(currentBitmap)

                        //small preview of the captured image
                        snap_image.visibility = View.VISIBLE
                        snap_image.setImageBitmap(currentBitmap)

                        val handler: Handler = Handler()
                        handler.postDelayed({
                            snap_image.startAnimation(hide)
                            snap_image.visibility = View.GONE
                        },1000)
                        //preview of image code ends animation needs to be applied

                        CreatingPdf.bitmapFileArray.add(tempFile)
                        photo_number.text = "${CreatingPdf.bitmapFileArray.size}"
                    } else {
                        CreatingPdf.bitmapFileArray[currentImageNumberToHandle] = tempFile
                        val intent = Intent(this@CameraActivity, CropActivity::class.java)
                        intent.putExtra("modified", currentImageNumberToHandle)
                        startActivity(intent)
                        finish()
                    }
                }
            })
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun decodeSampledBitmapFromFile(
        file: File,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, this)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeFile(file.absolutePath, this)
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val parcelFileDescriptor: ParcelFileDescriptor =
                applicationContext.contentResolver.openFileDescriptor(uri, "r")!!
            val fileDescriptor: FileDescriptor = parcelFileDescriptor.getFileDescriptor()
            val currentBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            currentBitmap
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview: Preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.createSurfaceProvider())
            }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(400,400))
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                cameraControl = camera.cameraControl

            } catch (e: Exception) {
                Toast.makeText(this, "Camera failed to open", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    fun toggleFlash(view: View) {
        cameraControl.enableTorch(!isFlashOn)
        isFlashOn = !isFlashOn
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool!!.release()
        soundPool = null
    }
}