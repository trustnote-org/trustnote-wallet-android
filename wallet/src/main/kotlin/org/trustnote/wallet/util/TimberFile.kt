package org.trustnote.wallet.util

import android.content.Context
import android.os.Environment
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class TimberFile(private val context: Context) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        try {

            val direct = File(Environment.getExternalStorageDirectory(), "ttt_log")

            if (!direct.exists()) {
                direct.mkdir()
            }

            val fileNameTimeStamp = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val logTimeStamp = SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa", Locale.getDefault()).format(Date())

            val fileName = fileNameTimeStamp + ".txt"

            val file = File(direct, fileName)

            file.createNewFile()

            if (file.exists()) {

                val fileOutputStream = FileOutputStream(file, true)

                fileOutputStream.write("$logTimeStamp : $tag : $message \n\r".toByteArray())
                fileOutputStream.close()

            }

        } catch (e: Exception) {
            Log.e(TAG, "Error while logging into file : $e")
        }

    }

    companion object {

        private val TAG = TimberFile::class.java.simpleName
    }
}