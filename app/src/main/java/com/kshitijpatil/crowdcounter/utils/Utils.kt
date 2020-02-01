package com.kshitijpatil.crowdcounter.utils

import android.content.Context
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object Utils {
    val TAG = Utils::class.java.simpleName
    fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        try {
            context.assets.open(assetName).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (`is`.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
                return file.absolutePath
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error process asset $assetName to file path")
        }
        return null
    }

    fun getBase64FromPath(path: String?): String {
        var base64 = ""
        try { /*from   w w w .  ja  va  2s  .  c om*/
            val file = File(path)
            val buffer = ByteArray(file.length().toInt() + 100)
            val length: Int = FileInputStream(file).read(buffer)
            base64 = Base64.encodeToString(
                buffer, 0, length,
                Base64.DEFAULT
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return base64
    }
}
