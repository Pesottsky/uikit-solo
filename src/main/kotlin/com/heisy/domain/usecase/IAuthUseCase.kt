package com.heisy.domain.usecase

import com.heisy.schema.*
import io.ktor.server.application.*
import java.util.*

interface IAuthUseCase {

    suspend fun registerCompany(user: User): Token

    suspend fun login(user: User): Token

    suspend fun loginByLink(link: String,user: User): Token

    suspend fun registerFreel(freel: Freel): Token

    suspend fun registerFreelByLink(link: String, freel: Freel): Token

    suspend fun refresh(token: String): Token

    suspend fun logout(userId: Int, userType: String)

    suspend fun updatePassword(pwd: UpdatePassword): Token

    suspend fun forgetPassword(app: Application, pwd: ForgetPassword): UUID
}