package com.one.videoeditingapp.ui

import android.Manifest
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.daasuu.gpuv.camerarecorder.CameraRecordListener
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorderBuilder
import com.daasuu.gpuv.camerarecorder.LensFacing
import com.daasuu.gpuv.egl.filter.GlBilateralFilter
import com.daasuu.gpuv.egl.filter.GlBoxBlurFilter
import com.daasuu.gpuv.egl.filter.GlBrightnessFilter
import com.daasuu.gpuv.egl.filter.GlContrastFilter
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.egl.filter.GlGaussianBlurFilter
import com.daasuu.gpuv.egl.filter.GlGrayScaleFilter
import com.daasuu.gpuv.egl.filter.GlInvertFilter
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter
import com.daasuu.gpuv.egl.filter.GlSaturationFilter
import com.daasuu.gpuv.egl.filter.GlSepiaFilter
import com.daasuu.gpuv.egl.filter.GlSharpenFilter
import com.daasuu.gpuv.egl.filter.GlVignetteFilter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.one.videoeditingapp.R
import com.one.videoeditingapp.databinding.ActivityMainBinding
import com.one.videoeditingapp.databinding.BottomSheetDialogBinding
import com.one.videoeditingapp.databinding.BottomSheetStopRecordingBinding
import com.one.videoeditingapp.model.CountDownModel
import com.one.videoeditingapp.model.FilterModel
import com.one.videoeditingapp.model.SpeedModel
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import java.io.File


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var gpuCameraRecorder: GPUCameraRecorder
    private lateinit var glSurfaceView: GLSurfaceView
    private var isRecording = false
    private var listFilter: ArrayList<FilterModel>? = null
    private var listSpeed: ArrayList<SpeedModel>? = null
    private var lastRecordedFile: File? = null
    private var isFilterOpen: Boolean = false
    private var listCountDown: ArrayList<CountDownModel>? = null
    private var isRotateToSelfie: Boolean = false
    private var isFlashOn: Boolean = false
    private var isSpeedShow: Boolean = false
    private var selectedPositionSpeed = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestStoragePermissions()
        setFilterRecycler()
        setUpSpeedRecycler()

        binding.tvNext.setOnClickListener {
            val videoPath = lastRecordedFile?.absolutePath ?: return@setOnClickListener
            val i = Intent(this, VideoEditingActivity::class.java)
            i.putExtra("videoPath", videoPath)
            i.putExtra("selectedSpeed",selectedPositionSpeed)
            startActivity(i)
        }

        binding.imgFilter.setOnClickListener {
            isFilterOpen = !isFilterOpen
            if (isFilterOpen) {
                binding.fyCapture.visibility = View.GONE
                binding.imgCloseFilter.visibility = View.VISIBLE
                binding.ryFilter.visibility = View.VISIBLE
                binding.tvNext.visibility = View.GONE
            } else {
                binding.fyCapture.visibility = View.VISIBLE
                binding.imgCloseFilter.visibility = View.GONE
                binding.ryFilter.visibility = View.GONE
            }
        }

        binding.imgCloseFilter.setOnClickListener {
            isFilterOpen = false
            binding.fyCapture.visibility = View.VISIBLE
            binding.imgCloseFilter.visibility = View.GONE
            binding.ryFilter.visibility = View.GONE
        }

        binding.imgMusic.setOnClickListener {
            startActivity(Intent(this, MusicActivity::class.java))
            binding.fyCapture.visibility = View.VISIBLE
            binding.imgCloseFilter.visibility = View.GONE
            binding.ryFilter.visibility = View.GONE
        }

        binding.imgWatch.setOnClickListener {
            binding.fyCapture.visibility = View.VISIBLE
            binding.ryFilter.visibility = View.GONE
            binding.imgCloseFilter.visibility = View.GONE
            openTimerDialog()
        }

        binding.fyCapture.setOnClickListener {
            startOrStopRecording()
        }

        binding.imgReverseCamera.setOnClickListener {
            isRotateToSelfie = !isRotateToSelfie
            startCameraWithGPURecorder()
        }

        binding.imgFlash.setOnClickListener {
            isFlashOn = !isFlashOn
            if (isFlashOn) {
                binding.imgFlash.setImageResource(R.drawable.ic_flashon)
            } else {
                binding.imgFlash.setImageResource(R.drawable.ic_flashoff)
            }
            gpuCameraRecorder.switchFlashMode();
        }

        binding.imgClose.setOnClickListener {
            stopRecordingBottomSheet()
        }

        binding.speed.setOnClickListener {
            isSpeedShow = !isSpeedShow
            if (isSpeedShow) {
                binding.rcvSpeed.visibility = View.VISIBLE
            } else {
                binding.rcvSpeed.visibility = View.GONE
            }
        }
    }

    /******************************************** function **************************************************/

    /******************************************** Filter rcv set ********************************************/

    private fun setFilterRecycler() {
        listFilter = ArrayList<FilterModel>()
        listFilter!!.add(FilterModel(R.drawable.bg_musicactivity))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        listFilter!!.add(FilterModel(R.drawable.demo_filter))
        val adapter = FilterAdapter(this, listFilter!!)
        binding.rcvFilter.adapter = adapter

        binding.rcvFilter.setItemTransformer(
            ScaleTransformer.Builder()
                .setMaxScale(1.0f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                .build()
        )

        val scrollView = binding.rcvFilter as DiscreteScrollView
        scrollView.adapter = adapter
        binding.rcvFilter.scrollToPosition(0)

        scrollView.addOnItemChangedListener { viewHolder, adapterPosition ->
            // Update your filter based on the selected position
            when (adapterPosition) {
                0 -> gpuCameraRecorder.setFilter(GlFilter())
                1 -> gpuCameraRecorder.setFilter(GlBilateralFilter())
                2 -> gpuCameraRecorder.setFilter(GlBoxBlurFilter())
                3 -> gpuCameraRecorder.setFilter(GlBrightnessFilter())
                4 -> gpuCameraRecorder.setFilter(GlContrastFilter())
                5 -> gpuCameraRecorder.setFilter(GlSepiaFilter())
                6 -> gpuCameraRecorder.setFilter(GlVignetteFilter())
                7 -> gpuCameraRecorder.setFilter(GlInvertFilter())
                8 -> gpuCameraRecorder.setFilter(GlMonochromeFilter())
                9 -> gpuCameraRecorder.setFilter(GlSaturationFilter())
                10 -> gpuCameraRecorder.setFilter(GlGaussianBlurFilter())
                11 -> gpuCameraRecorder.setFilter(GlGrayScaleFilter())
                12 -> gpuCameraRecorder.setFilter(GlSharpenFilter())
            }
        }
    }

    /********************************************************************************************************/
    /******************************************** Filter rcv set ********************************************/

    private fun setUpSpeedRecycler() {
        listSpeed = ArrayList<SpeedModel>()
        listSpeed!!.add(SpeedModel(".5x"))
        listSpeed!!.add(SpeedModel(".4x"))
        listSpeed!!.add(SpeedModel("3x"))
        listSpeed!!.add(SpeedModel("2x"))

        val adapter = SpeedAdapter(this, listSpeed!!, object : SpeedAdapter.OnClickListener {
            override fun onClick(position: Int) {
                binding.rcvSpeed.visibility = View.GONE
                selectedPositionSpeed = position
                if (position != -1) {
                    setSpeedSelected()
                }
            }
        })
        binding.rcvSpeed.adapter = adapter
    }

    private fun setSpeedSelected() {
        val speedText = listSpeed!!.get(selectedPositionSpeed).speed
        binding.imgSpeed.visibility = View.GONE
        binding.lySelectedSpeed.visibility = View.VISIBLE
        binding.tvSpeedSelected.text = speedText
        isSpeedShow = false
    }

    /*************************************************************************************************************************/
    /******************************************** permission for camera and audio ********************************************/

    private fun requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO
                ),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCameraWithGPURecorder()
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraWithGPURecorder()
            } else {
                showCameraPermissionRationaleDialog()
            }
        }
    }

    private fun showCameraPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app requires access to your Camera and Audio. Please grant permission.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /********************************************************************************************************/
    /******************************************** Camera recoding ********************************************/

    private fun startCameraWithGPURecorder() {
        glSurfaceView = GLSurfaceView(this)

        if (glSurfaceView.parent != null) {
            (glSurfaceView.parent as ViewGroup).removeView(glSurfaceView)
        }

         val lensFacingBack = LensFacing.BACK;
         val lensFacingFront = LensFacing.FRONT;

        binding.previewView.removeAllViews()
        binding.previewView.addView(glSurfaceView)

        gpuCameraRecorder = GPUCameraRecorderBuilder(this, glSurfaceView)
            .lensFacing(if (isRotateToSelfie) lensFacingFront else lensFacingBack)
            .videoSize(720, 1280)
            .recordNoFilter(false)
            .cameraRecordListener(object : CameraRecordListener {
                override fun onGetFlashSupport(flashSupport: Boolean) {}
                override fun onRecordComplete() {
                    binding.progressBar.visibility = View.VISIBLE
                    isRecording = false
                }

                override fun onRecordStart() {
                    isRecording = true
                }

                override fun onError(exception: Exception?) {}
                override fun onCameraThreadFinish() {}
                override fun onVideoFileReady() {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.tvNext.visibility = View.VISIBLE
                    }
                }
            })
            .build()

        gpuCameraRecorder.setFilter(GlFilter())
    }

    private fun startOrStopRecording() {
        if (isRecording) {
            gpuCameraRecorder.stop()
            binding.captureButton.setImageResource(R.drawable.bg_capturebutton)
            binding.recordingBorderView.setProgress(1f)
            isRecording = false
        } else {
            val videoFile = File(cacheDir, "video_${System.currentTimeMillis()}.mp4")
            gpuCameraRecorder.start(videoFile.absolutePath)
            binding.captureButton.setImageResource(R.drawable.bg_startcapturebtn)
            isRecording = true
            binding.tvNext.visibility = View.GONE
            lastRecordedFile = videoFile
        }
    }

    /*****************************************************************************************************/
    /******************************************** Timer Dialog ********************************************/

    private fun openTimerDialog() {
        var position1 = 0
        val bottomSheetDialog = BottomSheetDialog(this)
        val bindingb = BottomSheetDialogBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingb.root)
        bottomSheetDialog.show()

        listCountDown = ArrayList()
        listCountDown!!.add(CountDownModel("3s"))
        listCountDown!!.add(CountDownModel("5s"))
        listCountDown!!.add(CountDownModel("10s"))

        val adapter =
            CountDownAdapter(this, listCountDown!!, object : CountDownAdapter.OnClickListener {
                override fun onClick(position: Int) {
                    position1 = position
                }
            })
        bindingb.rcvLateStartCountDown.adapter = adapter

        bindingb.tvSetTimer.setOnClickListener {
            binding.tvNext.visibility = View.GONE
            bottomSheetDialog.dismiss()
            binding.countDown.visibility = View.VISIBLE
            if (position1 == 0) {
                startSmoothCountdown(3, bindingb.seekBar.progress)
            } else if (position1 == 1) {
                startSmoothCountdown(5, bindingb.seekBar.progress)
            } else {
                startSmoothCountdown(10, bindingb.seekBar.progress)
            }
        }

        bindingb.imgClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    /*************************************************************************************************************/
    /******************************************** startSmoothCountdown *******************************************/

    private fun startSmoothCountdown(seconds: Int, videoDurationSec: Int) {
        val totalDuration = seconds * 1000L
        val animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = totalDuration
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Float
                binding.borderView.setProgress(value)
            }
        }
        animator.start()
        object : CountDownTimer(totalDuration, 1000) {
            var count = seconds
            override fun onTick(millisUntilFinished: Long) {
                binding.countText.text = count.toString()
                count--
            }

            override fun onFinish() {
                binding.countText.text = ""
                isRecording = false
                startRecordingBorderAnimation(videoDurationSec * 1000L)
                startOrStopRecording()
                Handler(Looper.getMainLooper()).postDelayed({
                    isRecording = true
                    startOrStopRecording()
                }, videoDurationSec * 1000L)
            }
        }.start()
    }

    private fun startRecordingBorderAnimation(durationMillis: Long) {
        val animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = durationMillis
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Float
                binding.recordingBorderView.setProgress(value)
            }
        }
        animator.start()
    }

    /****************************************************************************************************/
    /******************************************** stop Dialog ********************************************/

    private fun stopRecordingBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bindingStopRecord = BottomSheetStopRecordingBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingStopRecord.root)
        bottomSheetDialog.show()
    }
}

/****************************************************************************************************/
/******************************************** using camerax *****************************************/

//    private var videoCapture: VideoCapture<Recorder>? = null
//    private var currentRecording: Recording? = null

//        private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
//                }
//
//            // Setup Recorder
//            val recorder = Recorder.Builder()
//                .setExecutor(ContextCompat.getMainExecutor(this))
//                .setQualitySelector(QualitySelector.from(Quality.HD))
//                .build()
//
//            val videoCapture = VideoCapture.withOutput(recorder)
//            this.videoCapture = videoCapture // store in property
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    this, cameraSelector, preview, videoCapture
//                )
//            } catch (e: Exception) {
//                Log.e(TAG, "Binding failed", e)
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }

//    private fun startRecording() {
//        val videoCapture = this.videoCapture ?: return
//
//        if (currentRecording != null) {
//            // Stop recording
//            currentRecording?.stop()
//            currentRecording = null
//            return
//        }
//
//        // Create a temporary file in internal cache
//        val tempFile = File.createTempFile("temp_video", ".mp4", cacheDir)
//
//        val fileOutputOptions = FileOutputOptions.Builder(tempFile).build()
//        val pendingRecording = if (ContextCompat.checkSelfPermission(
//                this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
//        ) {
//            videoCapture.output.prepareRecording(this, fileOutputOptions).withAudioEnabled()
//        } else {
//            videoCapture.output.prepareRecording(this, fileOutputOptions)
//        }
//
//        currentRecording =
//            pendingRecording.start(ContextCompat.getMainExecutor(this)) { recordEvent ->
//                when (recordEvent) {
//                    is VideoRecordEvent.Start -> {
//                        binding.captureButton.setImageResource(R.drawable.ic_start)
//                    }
//
//                    is VideoRecordEvent.Finalize -> {
//                        if (!recordEvent.hasError()) {
//                            Log.d(
//                                TAG,
//                                "Temp video saved to: ${recordEvent.outputResults.outputUri}"
//                            )
//                        } else {
//                            Log.e(TAG, "Recording failed: ${recordEvent.error}")
//                        }
//                        currentRecording = null
//
//                        // Optional: delete temp file after use
//                        tempFile.delete()
//                    }
//                }
//            }
//    }


