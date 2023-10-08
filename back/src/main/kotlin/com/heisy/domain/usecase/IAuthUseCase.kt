package com.heisy.domain.usecase

import com.heisy.schema.Freel
import com.heisy.schema.Token
import com.heisy.schema.UpdatePassword
import com.heisy.schema.User

interface IAuthUseCase {

    suspend fun registerCompany(user: User): Token

    suspend fun login(user: User): Token

    suspend fun registerFreel(freel: Freel): Token

    suspend fun registerFreelByLink(link: String, freel: Freel): Token

    suspend fun refresh(token: String): Token

    suspend fun logout(userId: Int, userType: String)

    suspend fun updatePassword(pwd: UpdatePassword): Token
}