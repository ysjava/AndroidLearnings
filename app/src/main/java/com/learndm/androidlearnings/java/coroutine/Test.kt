package com.learndm.androidlearnings.java.coroutine


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Cancellable2Task() : Callable<String> {
    override fun call(): String {
        Thread.sleep(2000)
        log("333")
        return "998"
    }
}

suspend fun getId() = suspendCoroutine<Int> {
    it.resume(10)
}
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun getName() = suspendCancellableCoroutine<String> {
    val executor = Executors.newFixedThreadPool(3)
    val future = executor.submit(Cancellable2Task())
    println("getName $it")
    val a = future.get()
    it.resume(a){
        println("===打印错误: $it")
    }
    it.invokeOnCancellation {  }
}
fun main() {

    runBlocking {

        val ll = launch {
            log(getName())
        }
        //ll.cancel()
    }

//    thread {
//        log("111")
//
//
//        log("a: $a")
//    }

//    runBlocking {
//        val name = getName()
//        val age = getAge(name)
//        println("age: $age")
//
//    }
}

//suspend fun getName(): String = withContext(Dispatchers.IO) {
//    delay(1000)
//    "zs"
//}
//
//suspend fun getAge(name: String): Int = withContext(Dispatchers.IO) {
//    delay(1000)
//    1
//}

fun log(str: String) {
    println("${Thread.currentThread()}:$str")
}
