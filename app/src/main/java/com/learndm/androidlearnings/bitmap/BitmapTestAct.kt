package com.learndm.androidlearnings.bitmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.learndm.androidlearnings.R
import org.opencv.android.OpenCVLoader
import java.io.File
import kotlin.concurrent.thread

class BitmapTestAct : AppCompatActivity() {
    data class Abd(val file: File, val bitmap: Bitmap)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_bitmap_test)
        val image = findViewById<ImageView>(R.id.image)
        val btn = findViewById<Button>(R.id.btn)
        val si = 8

        val fileList = listOf<File>()

        //1001/50  21协程
        //得到 ↓
        val abds = listOf<Abd>()

        val re = setOf<Int>()
        for (i in abds.indices) {
            if (re.contains(i)) continue
            for (j in i + 1 until abds.size) {

            }
        }

        val ss = listOf<List<File>>()
// a b c d e

        btn.setOnClickListener {
//            scanData()
            if (!OpenCVLoader.initDebug())
                Log.e("OpenCV", "Unable to load OpenCV!");
            else
                Log.d("OpenCV", "OpenCV loaded Successfully!");
            Log.d("QKGYUWfq", "start decode...")
            val bitmap1 = BitmapFactory.decodeResource(
                resources,
                R.drawable.pic_zheng,
                BitmapFactory.Options().apply {
                    inSampleSize = si
                })
            val bitmap2 = BitmapFactory.decodeResource(
                resources,
                R.drawable.pic_zheng2,
                BitmapFactory.Options().apply {
                    inSampleSize = si
                })
//
            val br1 = pHash.dctImageHash(bitmap1,false)
            val br2 = pHash.dctImageHash(bitmap2,false)

            Log.d("QKGYUWfq", "比较1,sift:  ${SIFTUtils.similarity(bitmap1,bitmap2)}")
            Log.d("QKGYUWfq", "比较2,hanming:  ${pHash.hammingDistance(br1,br2)}")

        }

        //每个协程,最多负责50个,假如有1000图片还是更多,就分20个或则更多线程去decode,然后放入同一个集合中,
        //
    }

    private fun scanData() {
        val apkFiles = arrayListOf<File>()
        val cacheFiles = arrayListOf<File>()
        val otherFiles = arrayListOf<File>()
        val results = arrayListOf(apkFiles, cacheFiles, otherFiles)
        val conditions = arrayListOf<(absolutePath: String) -> Boolean>(
            { it.contains(".apk") },
            { it.contains("cache", true) },

            {
                it.contains(".txt", true)
                        || it.contains(".mlog", true)
                        || it.contains(".xlog", true)
                        || it.contains(".slog", true)
                        || it.split(".").contains("log")
                        || it.split(".").contains("Log")
                        || it.split(".").contains("temp")
                        || it.split(".").contains("Temp")
                        || it.split(".").contains("TEMP")
            }
        )

        thread {
            Scanner.scan(conditions, results)
            Log.d(
                "qkbugfukqwf",
                "apk:${apkFiles.size}  cache:${cacheFiles.size}  other:${otherFiles.size}"
            )

        }
    }


    private fun requestPermis() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
    }

    fun checkPermis(): Boolean {
        val p1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val p2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return p1 && p2
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                for11Par()
            } else {
                Toast.makeText(
                    this,
                    "permission was denied, please open permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun for11Par() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                .apply {
                    val uri = Uri.fromParts("package", packageName, null)
                    data = uri
                    startActivity(this)
                }
        }
    }
}