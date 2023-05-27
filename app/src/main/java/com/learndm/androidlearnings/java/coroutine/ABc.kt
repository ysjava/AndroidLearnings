package com.learndm.androidlearnings.java.coroutine

import kotlinx.coroutines.newFixedThreadPoolContext
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.concurrent.thread

fun main() {

    println("kuqgk")
var a = 10
    thread {
        a = 20
    }

    val executor = Executors.newSingleThreadExecutor()
    val future = executor.submit(CancellableTask())

    future.get()


}

class CancellableTask():Callable<Int>{
    override fun call(): Int {
        return 998
    }
}