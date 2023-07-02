package com.learndm.androidlearnings.java.file

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.learndm.androidlearnings.R
import java.io.*

class FileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)
        val con = findViewById<LinearLayout>(R.id.lay_con)
        packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES).filter {
            (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
        }.forEach { info ->
            layoutInflater.inflate(R.layout.cell_icon_and_pkg, null).apply {
                findViewById<ImageView>(R.id.iv_iconn).setImageDrawable(
                    packageManager.getApplicationIcon(
                        info.packageName
                    )
                )
                findViewById<TextView>(R.id.tv_name_pkg).text = info.packageName
                con.addView(this)
            }
        }
//        val iconDrawable = packageManager.getApplicationIcon("com.baidu.tieba")
//        findViewById<ImageView>(R.id.imageview).setImageDrawable(iconDrawable)

        Log.d("FileFileActivityActivity", "内部存储")
        //filesDir: /data/user/0/com.android.learn/files
        Log.d("FileFileActivityActivity", "filesDir: $filesDir")
        //cacheDir: /data/user/0/com.android.learn/cache
        Log.d("FileFileActivityActivity", "cacheDir: $cacheDir")
        //codeCacheDir: /data/user/0/com.android.learn/code_cache
        Log.d("FileFileActivityActivity", "codeCacheDir: $codeCacheDir")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //dataDir: /data/user/0/com.android.learn
            Log.d("FileFileActivityActivity", "dataDir: $dataDir")
        }

        Log.d("FileFileActivityActivity", "外部存储")
        //ExternalStorageDirectory: /storage/emulated/0
        Log.d(
            "FileFileActivityActivity",
            "ExternalStorageDirectory: ${Environment.getExternalStorageDirectory()}"
        )
        //ExternalStoragePublicDirectory: /storage/emulated/0/Music
        Log.d(
            "FileFileActivityActivity",
            "ExternalStoragePublicDirectory: ${
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path
            }"
        )
        //ExternalStorageState: mounted
        Log.d(
            "FileFileActivityActivity",
            "ExternalStorageState: ${Environment.getExternalStorageState()}"
        )

        Log.d("FileFileActivityActivity", "外部存储私有目录")
        //externalCacheDir: /storage/emulated/0/Android/data/com.android.learn/cache
        Log.d("FileFileActivityActivity", "externalCacheDir: $externalCacheDir")
        //externalCacheDirs: [Ljava.io.File;@984c7bf
        Log.d("FileFileActivityActivity", "externalCacheDirs: $externalCacheDirs")

        //写"2022减肥成功"进一个文件中
        val str = "2022减肥成功"

        val ba = str.toByteArray()
        var fos: FileOutputStream? = null
        var oos: ObjectOutputStream? = null
        val file = File("$filesDir/2022target.txt")
//        fos = File("$filesDir/2022target.txt").outputStream()
        val file2 = File("$filesDir/YangSong2022target")

        try {
            fos = FileOutputStream(file)
            fos.write(ba, 0, ba.size)

            oos = ObjectOutputStream(FileOutputStream(file2))
            oos.writeObject(YangSong("杨崧", 1, 24, 69.99f, 90f))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fos?.close()
            oos?.close()
        }

//        val name = findViewById<TextView>(R.id.name)
//        val sex = findViewById<TextView>(R.id.sex)
//        val age = findViewById<TextView>(R.id.age)
//        val weight = findViewById<TextView>(R.id.weight)
//        val appearance = findViewById<TextView>(R.id.appearance)
//
//        findViewById<Button>(R.id.button).setOnClickListener {
//            var ois: ObjectInputStream? = null
//            var yangSong: YangSong? = null
//            try {
//                ois = ObjectInputStream(file2.inputStream())
//                yangSong = ois.readObject() as YangSong
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                ois?.close()
//            }
//
//            yangSong?.let {
////                name.text = String.format(getString(R.string.txt1), it.name)
////                sex.text = String.format(getString(R.string.txt2), if (it.sex == 1) "男" else "女")
////                age.text = String.format(getString(R.string.txt3), it.age)
////                weight.text = String.format(getString(R.string.txt4), it.weight.toString())
////                appearance.text = String.format(getString(R.string.txt5), it.appearance.toString())
//            }
//        }
    }

    data class YangSong(
        val name: String,
        val sex: Int,
        val age: Int,
        val weight: Float,
        val appearance: Float
    ) : Serializable
}