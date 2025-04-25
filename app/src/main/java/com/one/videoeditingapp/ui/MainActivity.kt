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
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
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
import com.one.videoeditingapp.model.CountDownModel
import com.one.videoeditingapp.model.FilterModel
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var gpuCameraRecorder: GPUCameraRecorder
    private lateinit var glSurfaceView: GLSurfaceView
    private var isRecording = false
    private var listFilter: ArrayList<FilterModel>? = null
    private var lastRecordedFile: File? = null
    private var isFilterOpen: Boolean = false
    private var listCountDown: ArrayList<CountDownModel>? = null
    private var isRotateToSelfie: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestStoragePermissions()
        setFilterRecycler()

        binding.tvNext.setOnClickListener {
            val videoPath = lastRecordedFile?.absolutePath ?: return@setOnClickListener
            val i = Intent(this, VideoEditingActivity::class.java)
            i.putExtra("videoPath", videoPath)
            startActivity(i)
        }

        binding.imgFilter.setOnClickListener {
            isFilterOpen = !isFilterOpen
            if (isFilterOpen) {
                binding.captureButton.visibility = View.GONE
                binding.imgCloseFilter.visibility = View.VISIBLE
                binding.ryFilter.visibility = View.VISIBLE
                binding.tvNext.visibility = View.GONE
            } else {
                binding.captureButton.visibility = View.VISIBLE
                binding.imgCloseFilter.visibility = View.GONE
                binding.ryFilter.visibility = View.GONE
            }
        }

        binding.imgCloseFilter.setOnClickListener {
            binding.captureButton.visibility = View.VISIBLE
            binding.imgCloseFilter.visibility = View.GONE
            binding.ryFilter.visibility = View.GONE
        }

        binding.imgMusic.setOnClickListener {
            startActivity(Intent(this, MusicActivity::class.java))
            binding.captureButton.visibility = View.VISIBLE
            binding.imgCloseFilter.visibility = View.GONE
            binding.ryFilter.visibility = View.GONE
        }

        binding.imgWatch.setOnClickListener {
            binding.captureButton.visibility = View.VISIBLE
            binding.rcvFilter.visibility = View.GONE
            binding.imgCloseFilter.visibility = View.GONE
            openTimerDialog()
        }

        binding.captureButton.setOnClickListener {
            startOrStopRecording()
        }

        binding.imgReverseCamera.setOnClickListener {
            isRotateToSelfie = !isRotateToSelfie
            if (isRotateToSelfie) {
                startCameraWithGPURecorderSelfie()
            } else {
                startCameraWithGPURecorder()
            }
        }
    }

    /******************************************** function ********************************************/
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
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcvFilter.layoutManager = layoutManager
        val itemWidth = resources.getDimensionPixelSize(R.dimen.filter_item_width)
        val sidePadding = (resources.displayMetrics.widthPixels / 2) - (itemWidth /2)
        binding.rcvFilter.setPadding(sidePadding, 0, sidePadding, 0)
        binding.rcvFilter.clipToPadding = false

        binding.rcvFilter.post {
            layoutManager.scrollToPositionWithOffset(0, sidePadding)
        }


        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rcvFilter)

        binding.rcvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val snapView = snapHelper.findSnapView(layoutManager)
                    snapView?.let {
                        val position = layoutManager.getPosition(it)
                        when (position) {
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
            }
        })
    }

    /****************************************************************************************/
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

    /****************************************************************************************/
    /******************************************** Camera recoding ********************************************/

    private fun startCameraWithGPURecorder() {
        glSurfaceView = GLSurfaceView(this)

        if (glSurfaceView.parent != null) {
            (glSurfaceView.parent as ViewGroup).removeView(glSurfaceView)
        }

        binding.previewView.removeAllViews()
        binding.previewView.addView(glSurfaceView)

        gpuCameraRecorder = GPUCameraRecorderBuilder(this, glSurfaceView)
            .lensFacing(LensFacing.BACK)
            .videoSize(720, 1280)
            .recordNoFilter(false)
            .cameraRecordListener(object : CameraRecordListener {
                override fun onGetFlashSupport(flashSupport: Boolean) {}
                override fun onRecordComplete() {
                    isRecording = false
                }

                override fun onRecordStart() {
                    isRecording = true
                }

                override fun onError(exception: Exception?) {}
                override fun onCameraThreadFinish() {}
                override fun onVideoFileReady() {
                    runOnUiThread {
                        binding.tvNext.visibility = View.VISIBLE
                    }
                }
            })
            .build()
        gpuCameraRecorder.setFilter(GlFilter())
    }

    private fun startCameraWithGPURecorderSelfie() {
        glSurfaceView = GLSurfaceView(this)

        if (glSurfaceView.parent != null) {
            (glSurfaceView.parent as ViewGroup).removeView(glSurfaceView)
        }

        binding.previewView.removeAllViews()
        binding.previewView.addView(glSurfaceView)

        gpuCameraRecorder = GPUCameraRecorderBuilder(this, glSurfaceView)
            .lensFacing(LensFacing.FRONT)
            .videoSize(720, 1280)
            .recordNoFilter(false)
            .cameraRecordListener(object : CameraRecordListener {
                override fun onGetFlashSupport(flashSupport: Boolean) {}
                override fun onRecordComplete() {
                    isRecording = false
                }

                override fun onRecordStart() {
                    isRecording = true
                }

                override fun onError(exception: Exception?) {}
                override fun onCameraThreadFinish() {}
                override fun onVideoFileReady() {
                    runOnUiThread {
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
            binding.captureButton.setImageResource(R.drawable.ic_stoprecording)
            isRecording = false
        } else {
            val videoFile = File(cacheDir, "video_${System.currentTimeMillis()}.mp4")
            gpuCameraRecorder.start(videoFile.absolutePath)
            binding.captureButton.setImageResource(R.drawable.ic_startrecording)
            isRecording = true
            binding.tvNext.visibility = View.GONE
            lastRecordedFile = videoFile
        }
    }

    /****************************************************************************************/
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

    /****************************************************************************************/

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
                startOrStopRecording()
                Handler(Looper.getMainLooper()).postDelayed({
                    isRecording = true
                    startOrStopRecording()
                }, videoDurationSec * 1000L)
            }
        }.start()
    }
}

/******************************************** using camerax ********************************************/

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

/******************************************** video preview ********************************************/



