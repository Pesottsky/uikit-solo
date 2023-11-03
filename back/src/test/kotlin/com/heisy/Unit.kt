package com.heisy

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.Unit
import kotlin.test.assertEquals

class Unit {

    @Test
    fun rootRouteRespondsWithHelloWorldString(): Unit = runBlocking {
        val currentTime = System.currentTimeMillis()
        println(currentTime)
        val diffInMillisec: Long = currentTime - 86400000

        val diffInDays = TimeUnit.MILLISECONDS.toDays(864000123)
        println(diffInDays)
    }
}