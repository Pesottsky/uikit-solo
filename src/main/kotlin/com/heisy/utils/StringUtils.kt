package com.heisy.utils

object StringUtils {
    fun linkToFront(link: String): String {
        return InjectionUtils.provideApplication().environment.config.property("path.registration").getString() + link
    }
}