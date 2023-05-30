package com.nnsman.firebase

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.util.Locale


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       val mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(object :RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("kugbwqugrqi", "onReadyForSpeech:  $params")
            }

            override fun onBeginningOfSpeech() {
                Log.d("kugbwqugrqi", "onBeginningOfSpeech ")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d("kugbwqugrqi", "onRmsChanged: $rmsdB ")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("kugbwqugrqi", "onBufferReceived:  $buffer")
            }

            override fun onEndOfSpeech() {
                Log.d("kugbwqugrqi", "onEndOfSpeech:  ")
            }

            override fun onError(error: Int) {
                Log.d("kugbwqugrqi", "onError: $error ")
            }

            override fun onResults(results: Bundle?) {

                val partialResults =
                    results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (partialResults != null && partialResults.size > 0) {
                    val bestResult = partialResults[0]
                    Log.d("kugbwqugrqi", "onResults bestResult=$bestResult")
                    //result.setText(bestResult);
                }



                Log.d("kugbwqugrqi", "onResults:  $results")
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("kugbwqugrqi", "onPartialResults: $partialResults ")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("kugbwqugrqi", "onEvent:  $eventType  $params")
            }

        });

        findViewById<Button>(R.id.button).setOnClickListener {
           val a = SpeechRecognizer.isRecognitionAvailable(this)
            Log.d("kugbwqugrqi", "isRecognitionAvailable:  $a")

            // 启动服务需要一个 Intent
           val  mRecognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mRecognitionIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            mRecognitionIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
// mLocale 是一个语音种类，可以根据自己的需求去设置
            mRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINA);

// 开始语音识别 结果在 mSpeechRecognizer.setRecognitionListener(this);回调中
            mSpeechRecognizer.startListening(mRecognitionIntent);


        }
        val local = Locale("uk")
// 停止监听

        findViewById<Button>(R.id.button2).setOnClickListener {
            mSpeechRecognizer.stopListening();
        }

// 取消服务
        //mSpeechRecognizer.cancel();
//        FirebaseTranslateLanguage.getAllLanguages().size.toString()
//        Log.d("kugbwqugrqi", "language:  " + local.language)
//        Log.d("kugbwqugrqi", "toLanguageTag:  " + local.toLanguageTag())
//        Log.d("kugbwqugrqi", "isO3Language:  " + local.isO3Language)
//        Log.d("kugbwqugrqi", "displayName:  " + local.displayName)
//        Log.d("kugbwqugrqi", "displayLanguage:  " + local.displayLanguage)
//        Log.d("kugbwqugrqi", "displayCountry:  " + local.displayCountry)
//        Log.d("kugbwqugrqi", "displayScript:  " + local.displayScript)
//        Log.d("kugbwqugrqi", "displayVariant:  " + local.displayVariant)
//        a()

    }

    fun a() {
        val modelManager = FirebaseModelManager.getInstance()
        // Get translation models stored on the device.
        modelManager.getDownloadedModels(FirebaseTranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                // ...
                models.forEach {
                    Log.d("kugbwqugrqi", "${models.size}   ||${it.languageCode}")
                }
            }
            .addOnFailureListener {
                // Error.
                Log.d("kugbwqugrqi", "$it")
            }
    }

    fun b() {
        val modelManager = FirebaseModelManager.getInstance()
        // Delete the German model if it's on the device.
        val deModel = FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.DE).build()
        modelManager.deleteDownloadedModel(deModel)
            .addOnSuccessListener {
                // Model deleted.
            }
            .addOnFailureListener {
                // Error.
            }
    }

    fun c() {
        val modelManager = FirebaseModelManager.getInstance()
        // Download the French model.
        val frModel = FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.AF).build()
        val conditions = FirebaseModelDownloadConditions.Builder()
//            .requireWifi()
            .build()
        modelManager.download(frModel, conditions)
            .addOnSuccessListener {
                // Model downloaded.
                Log.d("FirebaseModelManagerdownload", "下载成功: $it")
            }
            .addOnFailureListener {
                // Error.
                Log.d("FirebaseModelManagerdownload", "下载失败 :$it")
            }
    }

    fun d() {
        // Create an English-German translator:
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.ZH)
            .build()
        val englishGermanTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        englishGermanTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
            }

        englishGermanTranslator.translate("hello yang")
            .addOnSuccessListener { translatedText ->
                // Translation successful.
                Log.d("hhzhjghxcjzcquiqq", "翻译成功 :$translatedText")
            }
            .addOnFailureListener { exception ->
                // Error.
                // ...
                Log.d("hhzhjghxcjzcquiqq", "翻译失败 :$exception")
            }
    }
}

//data class LanguageModel()