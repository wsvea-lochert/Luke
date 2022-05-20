package com.cosinum.luke

// code from https://developers.google.com/ml-kit/vision/object-detection/custom-models/android#4_run_the_object_detector and https://developer.android.com/codelabs/camerax-getting-started#5
// with some tricks and mix by William Svea-Lochert

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cosinum.luke.ml.*
import com.cosinum.luke.util.JamesAngles
import com.cosinum.luke.util.YuvToRgbConverter
import com.cosinum.luke.viewmodel.Coordinate
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


// typealias LumaListener = (luma: Double) -> Unit
typealias CoordinateListener = (coordinate: List<Coordinate>) -> Unit
typealias FpsListener = (fps: Double) -> Unit


class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var imageAnalyzer: ImageAnalysis // Analysis use case, for running ML code

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var mlExecutor: ExecutorService
    private var selectedModel: String = ""
    var drawing = true



    private fun getSelectedModel(): String {
        return selectedModel
    }
    fun setSelectedModel(model: String) {
        selectedModel = model
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button
        //camera_capture_button.setOnClickListener { takePhoto() }
        draw_button.setOnClickListener {
            if (drawing) {
                drawing = false
                draw_button.text = "Keypoints on"
                zeroKeypoints()
            } else {
                drawing = true
                draw_button.text = "Keypoints off"
            }
        }


        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        // set up 4 threads for ml
        //mlExecutor = Executors.newFixedThreadPool(4)

        // mlExecutor = Executors.newSingleThreadExecutor()

        val models = resources.getStringArray(R.array.model_array)

        val spinner = findViewById<Spinner>(R.id.model_spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, models)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(this@MainActivity,
                    getString(R.string.selected_item) + " " + "" + models[position], Toast.LENGTH_SHORT).show()
                // do something here when a new model is selected..
                setSelectedModel(models[position])
                startCamera()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // defaults to Mobilenet
                setSelectedModel("Mobilenet")
            }
        }
    }

    private fun zeroKeypoints(){
        var canvas = Canvas(Bitmap.createBitmap(rectOverlay.width, rectOverlay.height, Bitmap.Config.ARGB_8888))
        rectOverlay.head.xValue = 0.0f
        rectOverlay.head.yValue = 0.0f
        // left ankle
        rectOverlay.left_ankle.xValue = 0.0f
        rectOverlay.left_ankle.yValue = 0.0f
        // left_elbow
        rectOverlay.left_elbow.xValue = 0.0f
        rectOverlay.left_elbow.yValue = 0.0f
        //left_hip
        rectOverlay.left_hip.xValue = 0.0f
        rectOverlay.left_hip.yValue = 0.0f
        //left_knee
        rectOverlay.left_knee.xValue = 0.0f
        rectOverlay.left_knee.yValue = 0.0f
        //left_shoulder
        rectOverlay.left_shoulder.xValue = 0.0f
        rectOverlay.left_shoulder.yValue = 0.0f
        //left_wrist
        rectOverlay.left_wrist.xValue = 0.0f
        rectOverlay.left_wrist.yValue = 0.0f
        //neck
        rectOverlay.neck.xValue = 0.0f
        rectOverlay.neck.yValue = 0.0f
        //right_ankle
        rectOverlay.right_ankle.xValue = 0.0f
        rectOverlay.right_ankle.yValue = 0.0f
        //right_elbow
        rectOverlay.right_elbow.xValue = 0.0f
        rectOverlay.right_elbow.yValue = 0.0f
        //right_hip
        rectOverlay.right_hip.xValue = 0.0f
        rectOverlay.right_hip.yValue = 0.0f
        //right_knee
        rectOverlay.right_knee.xValue = 0.0f
        rectOverlay.right_knee.yValue = 0.0f
        //right_shoulder
        rectOverlay.right_shoulder.xValue = 0.0f
        rectOverlay.right_shoulder.yValue = 0.0f
        //right_writs
        rectOverlay.right_writs.xValue = 0.0f
        rectOverlay.right_writs.yValue = 0.0f
        //torso
        rectOverlay.torso.xValue = 0.0f
        rectOverlay.torso.yValue = 0.0f

        rectOverlay.draw(canvas)
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }



    // Log the prediction results so they are readable input
    private fun debugPrint(joints: List<Coordinate>) {

        var canvas = Canvas(Bitmap.createBitmap(rectOverlay.width, rectOverlay.height, Bitmap.Config.ARGB_8888))
        /*val textPaint =
            Paint().apply {
                isAntiAlias = true
                color = Color.RED
                style = Paint.Style.STROKE
            }*/



        // TODO: Fix rectOverlay to be the correct size
        //val displayMetrics = DisplayMetrics()
        //windowManager.defaultDisplay.getMetrics(displayMetrics)
        //val height = displayMetrics.heightPixels
        //val width = displayMetrics.widthPixels

        // get the hegiht of rectOverlay
        if (drawing) {
            val height = rectOverlay.height
            val width = rectOverlay.width


            // head
            rectOverlay.head.xValue = (joints[0].x) * width
            rectOverlay.head.yValue = (joints[0].y) * height + 140
            // left ankle
            rectOverlay.left_ankle.xValue = (joints[1].x) * width
            rectOverlay.left_ankle.yValue = (joints[1].y) * height - 110
            // left_elbow
            rectOverlay.left_elbow.xValue = (joints[2].x) * width
            rectOverlay.left_elbow.yValue = (joints[2].y) * height
            //left_hip
            rectOverlay.left_hip.xValue = (joints[3].x) * width
            rectOverlay.left_hip.yValue = (joints[3].y) * height
            //left_knee
            rectOverlay.left_knee.xValue = (joints[4].x) * width
            rectOverlay.left_knee.yValue = (joints[4].y) * height - 60
            //left_shoulder
            rectOverlay.left_shoulder.xValue = (joints[5].x) * width
            rectOverlay.left_shoulder.yValue = (joints[5].y) * height + 100
            //left_wrist
            rectOverlay.left_wrist.xValue = (joints[6].x) * width
            rectOverlay.left_wrist.yValue = (joints[6].y) * height
            //neck
            rectOverlay.neck.xValue = (joints[7].x) * width
            rectOverlay.neck.yValue = (joints[7].y) * height + 120
            //right_ankle
            rectOverlay.right_ankle.xValue = (joints[8].x) * width
            rectOverlay.right_ankle.yValue = (joints[8].y) * height - 110
            //right_elbow
            rectOverlay.right_elbow.xValue = (joints[9].x) * width
            rectOverlay.right_elbow.yValue = (joints[9].y) * height
            //right_hip
            rectOverlay.right_hip.xValue = (joints[10].x) * width
            rectOverlay.right_hip.yValue = (joints[10].y) * height
            //right_knee
            rectOverlay.right_knee.xValue = (joints[11].x) * width
            rectOverlay.right_knee.yValue = (joints[11].y) * height - 60
            //right_shoulder
            rectOverlay.right_shoulder.xValue = (joints[12].x) * width
            rectOverlay.right_shoulder.yValue = (joints[12].y) * height + 100
            //right_writs
            rectOverlay.right_writs.xValue = (joints[13].x) * width
            rectOverlay.right_writs.yValue = (joints[13].y) * height
            //torso
            rectOverlay.torso.xValue = (joints[14].x) * width
            rectOverlay.torso.yValue = (joints[14].y) * height

            rectOverlay.draw(canvas)
        }

    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            var sendModel = getSelectedModel()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(224, 224))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysisUseCase: ImageAnalysis ->
                    analysisUseCase.setAnalyzer(cameraExecutor, ImageAnalyzer(this, sendModel) { items ->
                        runOnUiThread {
                            var inferenceTime = items[15].x.toString()
                            var signals = JamesAngles(items)
                            signalText.text = signals.getSignal()
                            //throttleText.text = signals.thro
                            when {
                                signalText.text.toString().contains("stop") -> {
                                    signalText.setTextColor(Color.RED)
                                }
                                signalText.text.toString().contains("left") -> {
                                    signalText.setTextColor(Color.GREEN)
                                }
                                signalText.text.toString().contains("right") -> {
                                    signalText.setTextColor(Color.BLUE)
                                }
                                signalText.text.toString().contains("forward") -> {
                                    signalText.setTextColor(Color.YELLOW)
                                }
                                signalText.text.toString().contains("reverse") -> {
                                    signalText.setTextColor(Color.MAGENTA)
                                }
                            }
                            fpsCounter.text = "Inference time: ${inferenceTime} sec"
                        }
                        debugPrint(items)
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        mlExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /* Custom ImageAnalyzer */
    private class ImageAnalyzer(ctx: Context, private var models: String, private val listener: CoordinateListener) : ImageAnalysis.Analyzer {
        // check what items has been selected in spinner

        val currentModel = getModels()  // get selected model from spinner

        val mobilenetS1 = McflyMobilenetArchD2s1Tunedat100.newInstance(ctx)
        val mobilenetS2 = McflyMobilenetArchD2s2Tunedat100.newInstance(ctx)
        val mobilenetS3 = McflyMobilenetArchD2s3Tunedat100.newInstance(ctx)
        val mobilenetS4 = McflyMobilenetArchD2s4Tunedat100.newInstance(ctx)
        val mobilenetS5 = McflyMobilenetArchD2s5Tunedat100.newInstance(ctx)
        val mobilenetS6 = McflyMobilenetArchD2s6Tunedat100.newInstance(ctx)
        val mobilenetS7 = McflyMobilenetArchD2s7Tunedat100.newInstance(ctx)
        val mobilenetS8 = McflyMobilenetArchD2s8Tunedat100.newInstance(ctx)

        val resnets1 = McflyResnetArchD2s1Tunedat100.newInstance(ctx)
        val resnets2 = McflyResnetArchD2s2Tunedat100.newInstance(ctx)
        val resnets3 = McflyResnetArchD2s3Tunedat100.newInstance(ctx)
        val resnets4 = McflyResnetArchD2s4Tunedat100.newInstance(ctx)
        val resnets5 = McflyResnetArchD2s5Tunedat100.newInstance(ctx)
        val resnets6 = McflyResnetArchD2s6Tunedat100.newInstance(ctx)
        val resnets7 = McflyResnetArchD2s7Tunedat100.newInstance(ctx)
        val resnets8 = McflyResnetArchD2s8Tunedat100.newInstance(ctx)

        val cnn1 = McflyCnnD2s1V2.newInstance(ctx)
        val cnn2 = McflyCnnD2s2V2.newInstance(ctx)
        val cnn3 = McflyCnnD2s3V2.newInstance(ctx)
        val cnn4 = McflyCnnD2s4V2.newInstance(ctx)
        val cnn5 = McflyCnnD2s5V2.newInstance(ctx)
        val cnn6 = McflyCnnD2s6V2.newInstance(ctx)
        val cnn7 = McflyCnnD2s7V2.newInstance(ctx)
        val cnn8 = McflyCnnD2s8V2.newInstance(ctx)

        val residual1 = ResidualTrainSplit1.newInstance(ctx)
        val residual2 = ResidualTrainSplit2.newInstance(ctx)
        val residual3 = ResidualTrainSplit3.newInstance(ctx)
        val residual4 = ResidualTrainSplit4.newInstance(ctx)
        val residual5 = ResidualTrainSplit5.newInstance(ctx)
        val residual6 = ResidualTrainSplit6.newInstance(ctx)
        val residual7 = ResidualTrainSplit7.newInstance(ctx)
        val residual8 = ResidualTrainSplit8.newInstance(ctx)

        fun predict(input: TensorBuffer): TensorBuffer{
            // if models contains "mobilenet"
            when {
                models.contains("mobilenet") -> {
                    when (models) {
                        "mobilenet_s1" -> {
                            val outputs = mobilenetS1.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "mobilenet_s2" -> {
                            val outputs = mobilenetS2.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "mobilenet_s3" -> {
                            val outputs = mobilenetS3.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "mobilenet_s4" -> {
                            val outputs = mobilenetS4.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "mobilenet_s5" -> {
                            val outputs = mobilenetS5.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "mobilenet_s6" -> {
                            val outputs = mobilenetS6.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "mobilenet_s7" -> {
                            val outputs = mobilenetS7.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "mobilenet_s8" -> {
                            val outputs = mobilenetS8.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                    }
                }
                models.contains("resnet") -> {
                    when (models) {
                        "resnet_s1" -> {
                            val outputs = resnets1.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "resnet_s2" -> {
                            val outputs = resnets2.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "resnet_s3" -> {
                            val outputs = resnets3.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "resnet_s4" -> {
                            val outputs = resnets4.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "resnet_s5" -> {
                            val outputs = resnets5.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "resnet_s6" -> {
                            val outputs = resnets6.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "resnet_s7" -> {
                            val outputs = resnets7.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "resnet_s8" -> {
                            val outputs = resnets8.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                    }
                }
                models.contains("cnn") -> {
                    when (models) {
                        "cnn_s1" -> {
                            // use GPU if available
                            val outputs = cnn1.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "cnn_s2" -> {
                            val outputs = cnn2.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "cnn_s3" -> {
                            val outputs = cnn3.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "cnn_s4" -> {
                            val outputs = cnn4.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "cnn_s5" -> {
                            val outputs = cnn5.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "cnn_s6" -> {
                            val outputs = cnn6.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "cnn_s7" -> {
                            val outputs = cnn7.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "cnn_s8" -> {
                            val outputs = cnn8.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                    }
                }
                models.contains("residual") -> {
                    when (models) {
                        "residual_s1" -> {
                            // use GPU if available
                            val outputs = residual1.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "residual_s2" -> {
                            val outputs = residual2.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "residual_s3" -> {
                            val outputs = residual3.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "residual_s4" -> {
                            val outputs = residual4.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "residual_s5" -> {
                            val outputs = residual5.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "residual_s6" -> {
                            val outputs = residual6.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "residual_s7" -> {
                            val outputs = residual7.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                        "residual_s8" -> {
                            val outputs = residual8.process(input)
                            return outputs.outputFeature0AsTensorBuffer
                        }
                    }
                }
            }

            return TensorBuffer.createFixedSize(intArrayOf(1, 1, 30), DataType.FLOAT32)
        }

        override fun analyze(imageProxy: ImageProxy) {

            models = getModels()

            val items = mutableListOf<Coordinate>()
            // system time in seconds
            val startTime = System.currentTimeMillis() / 1000.0

            val bitmap = toBitmap(imageProxy)

            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build()

            var tensorImage = TensorImage(DataType.FLOAT32)

            // Analysis code for every frame
            // Preprocess the image
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

            //Run the model on the image and get the output.
            val outputFeature0 = predict(inputFeature0)

            // current system time in seconds
            val endTime = System.currentTimeMillis() / 1000.0
            // TODO 3: Calculate the inference time
            val inferenceTime = endTime - startTime

            // TODO 4: Calculate the FPS
            val fps = 1.0 / inferenceTime


            val head = Coordinate(outputFeature0.floatArray[0], outputFeature0.floatArray[1])
            val left_ankle = Coordinate(outputFeature0.floatArray[2], outputFeature0.floatArray[3])
            val left_elbow = Coordinate(outputFeature0.floatArray[4], outputFeature0.floatArray[5])
            val left_hip = Coordinate(outputFeature0.floatArray[6], outputFeature0.floatArray[7])
            val left_knee = Coordinate(outputFeature0.floatArray[8], outputFeature0.floatArray[9])
            val left_shoulder = Coordinate(outputFeature0.floatArray[10], outputFeature0.floatArray[11])
            val left_wrist = Coordinate(outputFeature0.floatArray[12], outputFeature0.floatArray[13])
            val neck = Coordinate(outputFeature0.floatArray[14], outputFeature0.floatArray[15])
            val right_ankle = Coordinate(outputFeature0.floatArray[16], outputFeature0.floatArray[17])
            val right_elbow = Coordinate(outputFeature0.floatArray[18], outputFeature0.floatArray[19])
            val right_hip = Coordinate(outputFeature0.floatArray[20], outputFeature0.floatArray[21])
            val right_knee = Coordinate(outputFeature0.floatArray[22], outputFeature0.floatArray[23])
            val right_shoulder = Coordinate(outputFeature0.floatArray[24], outputFeature0.floatArray[25])
            val right_wrist = Coordinate(outputFeature0.floatArray[26], outputFeature0.floatArray[27])
            val torso = Coordinate(outputFeature0.floatArray[28], outputFeature0.floatArray[29])

            items.add(head)
            items.add(left_ankle)
            items.add(left_elbow)
            items.add(left_hip)
            items.add(left_knee)
            items.add(left_shoulder)
            items.add(left_wrist)
            items.add(neck)
            items.add(right_ankle)
            items.add(right_elbow)
            items.add(right_hip)
            items.add(right_knee)
            items.add(right_shoulder)
            items.add(right_wrist)
            items.add(torso)
            items.add(Coordinate(inferenceTime.toFloat(), inferenceTime.toFloat()))

            listener(items.toList())
            // fpsCounter(inferenceTime)

            // Close the image,this tells CameraX to feed the next image to the analyzer
            imageProxy.close()
        }

        fun getModels(): String {
            return models
        }
//        fun setModels(models: String) {
//            this.models = models
//        }

        /**
         * Convert Image Proxy to Bitmap
         */
        private val yuvToRgbConverter = YuvToRgbConverter(ctx)
        private lateinit var bitmapBuffer: Bitmap
        private lateinit var rotationMatrix: Matrix

        @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
        private fun toBitmap(imageProxy: ImageProxy): Bitmap? {

            val image = imageProxy.image ?: return null

            // Initialise Buffer
            if (!::bitmapBuffer.isInitialized) {
                // The image rotation and RGB image buffer are initialized only once
                Log.d(TAG, "Initalise toBitmap()")
                rotationMatrix = Matrix()
                rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                bitmapBuffer = Bitmap.createBitmap(
                    imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
                )
            }

            // Pass image to an image analyser
            yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

            // Create the Bitmap in the correct orientation
            return Bitmap.createBitmap(
                bitmapBuffer,
                0,
                0,
                bitmapBuffer.width,
                bitmapBuffer.height,
                rotationMatrix,
                false
            )
        }

    }

}




