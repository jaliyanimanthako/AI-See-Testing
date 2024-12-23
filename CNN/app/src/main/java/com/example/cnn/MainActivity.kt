package com.example.cnn

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.Manifest
import android.graphics.Bitmap
import android.media.ThumbnailUtils

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cnn.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity() {
    private lateinit var result: TextView
    private lateinit var confidence: TextView
    private lateinit var imageView: ImageView
    private lateinit var picture: Button
    val imageSize = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result = findViewById(R.id.result)
        confidence = findViewById(R.id.confidence)
        imageView = findViewById(R.id.imageView)
        picture = findViewById(R.id.button)

        picture.setOnClickListener {
            // Launch camera if we have permission
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 1)
            } else {
                // Request camera permission if we don't have it
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
    }

    private fun classifyImage(image: Bitmap) {
        try {
            val model = Model.newInstance(applicationContext)
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)

            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val pixelVal = intValues[pixel++] // RGB
                    byteBuffer.putFloat(((pixelVal shr 16) and 0xFF) * (1f / 255f)) // R
                    byteBuffer.putFloat(((pixelVal shr 8) and 0xFF) * (1f / 255f))  // G
                    byteBuffer.putFloat((pixelVal and 0xFF) * (1f / 255f))          // B

                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray
            val maxPos = confidences.indices.maxByOrNull { confidences[it] } ?: 0
            val classes = arrayOf("Banana", "Orange", "Pen", "Sticky Notes")

            // Display the result
            result.text = classes[maxPos]
            val resultString = StringBuilder()
            for (i in classes.indices) {
                resultString.append(String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100))
            }
            confidence.text = resultString.toString()

            // Close the model after use
            model.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val image = data?.extras?.get("data") as Bitmap
            val dimension = minOf(image.width, image.height)
            val thumbnail = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            imageView.setImageBitmap(thumbnail)

            val scaledImage = Bitmap.createScaledBitmap(thumbnail, imageSize, imageSize, false)
            classifyImage(scaledImage)
        }
    }



}



