package com.heisy.utils

object LogUtils {

    fun createLog(pair: Pair<String, String>, route: String): String {
        return "Пользователь ${pair.first} с типом ${pair.second} на роуте $route"
    }
}