package com.heisy.utils

import com.heisy.schema.ExposedLink
import io.ktor.server.application.*

object StringUtils {
    fun linkToFront(link: String): String {
        return InjectionUtils.provideApplication().environment.config.property("path.registration").getString() + link
    }
}