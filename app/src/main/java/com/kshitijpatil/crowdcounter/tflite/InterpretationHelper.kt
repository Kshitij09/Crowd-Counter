package com.kshitijpatil.crowdcounter.tflite

import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class InterpretationHelper {

    private val interpreterOptions by lazy {
        // use the neuralNet API if available
        Interpreter.Options()
            .setUseNNAPI(true)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {

        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)

        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until inputImageWidth) {
            for (j in 0 until inputImageHeight) {
                val pixelVal = pixels[pixel++]
                byteBuffer.put((pixelVal shr 16 and 0xFF).toByte())
                byteBuffer.put((pixelVal shr 8 and 0xFF).toByte())
                byteBuffer.put((pixelVal and 0xFF).toByte())
            }
        }
        bitmap.recycle()

        return byteBuffer
    }

    companion object {
        // Our model expects a RGB image, hence the channel size is 3
        private const val channelSize = 3

        // Width of the image that our model expects
        private const val inputImageWidth = 224
        // Height of the image that our model expects
        private const val inputImageHeight = 224
        // Size of the input buffer size (if your model expects a float input, multiply this with 4)
        private var modelInputSize = inputImageWidth * inputImageHeight * channelSize
    }
}