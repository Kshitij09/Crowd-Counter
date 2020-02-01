package com.kshitijpatil.crowdcounter.ml

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.camera.core.ImageProxy
import com.kshitijpatil.crowdcounter.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.nio.FloatBuffer

const val MOBILENET_V2 = "mobilenet_quantized_scripted_925.pt"

class CrowdClassifier(context: Context) {

    val TAG = CrowdClassifier::class.java.simpleName
    private var mAnalyzeImageErrorState = false
    private val mModule: Module? by lazy {
        val moduleFileAbsoluteFilePath: String = File(
            Utils.assetFilePath(context, MOBILENET_V2)!!
        ).absolutePath

        Module.load(moduleFileAbsoluteFilePath)
    }
    private val mInputTensorBuffer: FloatBuffer? by lazy {
        Tensor.allocateFloatBuffer(3 * INPUT_TENSOR_WIDTH * INPUT_TENSOR_HEIGHT)
    }

    private val mInputTensor: Tensor? by lazy {
        Tensor.fromBlob(
            mInputTensorBuffer, longArrayOf(
                1, 3,
                INPUT_TENSOR_HEIGHT.toLong(), INPUT_TENSOR_WIDTH.toLong()
            )
        )
    }

    @WorkerThread
    fun inference(image: ImageProxy) {
        try {
            val startTime = SystemClock.elapsedRealtime()
            TensorImageUtils.imageYUV420CenterCropToFloatBuffer(
                image.image,
                image.imageInfo.rotationDegrees,
                INPUT_TENSOR_WIDTH,
                INPUT_TENSOR_HEIGHT,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                mInputTensorBuffer,
                0
            )

            val moduleForwardStartTime = SystemClock.elapsedRealtime()
            val outputTensor = mModule!!.forward(IValue.from(mInputTensor)).toTensor()
            val moduleForwardDuration =
                SystemClock.elapsedRealtime() - moduleForwardStartTime

            val scores = outputTensor.dataAsFloatArray
            Log.d(TAG, scores.toString())
            val analysisDuration = SystemClock.elapsedRealtime() - startTime
        } catch (e: Exception) {
            Log.e(TAG, "Error during image analysis", e)
            mAnalyzeImageErrorState = true
        }
    }

    suspend fun inference(image: Bitmap) = withContext(Dispatchers.Main) {
        try {
            val startTime = SystemClock.elapsedRealtime()
            val mInputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(
                image,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )

            val moduleForwardStartTime = SystemClock.elapsedRealtime()
            val outputTensor = mModule!!.forward(IValue.from(mInputTensor)).toTensor()
            Log.d(TAG, "Output: ${outputTensor.shape().size}")
            val moduleForwardDuration =
                SystemClock.elapsedRealtime() - moduleForwardStartTime

            val scores = outputTensor.dataAsFloatArray
            Log.d(TAG, scores.toString())
            val analysisDuration = SystemClock.elapsedRealtime() - startTime
        } catch (e: Exception) {
            Log.e(TAG, "Error during image analysis", e)
            mAnalyzeImageErrorState = true
        }
    }

    companion object {
        private const val INPUT_TENSOR_WIDTH = 224
        private const val INPUT_TENSOR_HEIGHT = 224
        private const val TOP_K = 3
        private const val MOVING_AVG_PERIOD = 10
        private const val FORMAT_MS = "%dms"
        private const val FORMAT_AVG_MS = "avg:%.0fms"

        private const val FORMAT_FPS = "%.1fFPS"
        const val SCORES_FORMAT = "%.2f"
    }

}