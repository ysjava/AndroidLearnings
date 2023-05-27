package com.learndm.admobpackaging

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.animation.doOnEnd
import com.learndm.admobpackaging.adkk.AdManager
import com.learndm.admobpackaging.adkk.InsAd
import com.learndm.admobpackaging.databinding.ActivityLauncherBinding


class LauncherActivity : AppCompatActivity() {


    private var isHot: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isHot = intent.getBooleanExtra("isHot", false)

        var animator = ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 100)
        animator.duration = 10000

        animator.doOnEnd {
            //jump
            executeStartActivity()
        }
        animator.start()

        AdManager.launcherInsAd.load(object : InsAd.LoadInsAdListener {
            override fun loadSuccess() {
                animator.removeAllListeners()
                animator.cancel()
                animator = ObjectAnimator.ofInt(binding.progressBar, "progress", 100)
                animator.doOnEnd {

                    lifecycleScope.launchWhenResumed {
                        AdManager.launcherInsAd.show(this@LauncherActivity,
                            object : InsAd.ShowInsAdListener {
                                override fun dismissed() {
                                    executeStartActivity()
                                }

                                override fun showFail(isShowBackupAd: Boolean) {
                                    if (isShowBackupAd)
                                        AdManager.backupInsAd.show(this@LauncherActivity,
                                            object : InsAd.ShowInsAdListener {
                                                override fun dismissed() {
                                                    executeStartActivity()
                                                }

                                                override fun showFail(isShowBackupAd: Boolean) {
                                                    executeStartActivity()
                                                }
                                            })
                                    executeStartActivity()
                                }
                            })
                    }

                }
                animator.duration = 2000
                animator.start()
            }
        })
    }

    fun executeStartActivity() {
        //超时的热启动,直接finish即可
        if (!isHot)
            startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}