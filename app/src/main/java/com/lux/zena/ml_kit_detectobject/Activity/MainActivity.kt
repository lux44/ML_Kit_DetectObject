package com.lux.zena.ml_kit_detectobject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.PredefinedCategory
import com.lux.zena.ml_kit_detectobject.R
import com.lux.zena.ml_kit_detectobject.databinding.ActivityMainBinding
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    lateinit var image:InputImage

    fun detect(image:InputImage){
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableClassification()
            .build()

        val objectDetector = ObjectDetection.getClient(options)

        objectDetector.process(image)
            .addOnSuccessListener {
                Log.e("TAG","SUCCESS")
                Log.e("TAG","${it.size}")
                for (detectedObject in it) {
                    val boundingBox = detectedObject.boundingBox
                    Log.e("TAG","$boundingBox")
                    for (label in detectedObject.labels){
                        val text = label.text
                        val index = label.index
                        val confidence = label.confidence
                        binding.tv.text = "$boundingBox , text : $text, index : $index, confidence : $confidence"
                    }
                }
            }
            .addOnFailureListener{
                Log.e("TAG","FAIL")
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        binding.btnSelect.setOnClickListener {
            TedImagePicker.with(this)
                .mediaType(MediaType.IMAGE)
                .start {
                    Glide.with(this).load(it).into(binding.iv)
                    image = InputImage.fromFilePath(this,it)
                    binding.btnDetect.visibility = View.VISIBLE
                }
        }

        binding.tv.movementMethod = ScrollingMovementMethod()

        binding.btnDetect.setOnClickListener {
            if (image==null) Toast.makeText(this, "select photo first", Toast.LENGTH_SHORT).show()
            else detect(image)
        }
    }
}