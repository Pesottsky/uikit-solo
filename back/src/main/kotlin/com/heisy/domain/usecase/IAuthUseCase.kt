package com.heisy.domain.usecase

import com.heisy.schema.Freel
import com.heisy.schema.Token
import com.heisy.schema.User

interface IAuthUseCase {

    suspend fun loginCompany(user: User): Token

    suspend fun loginFreel(freel: Freel): Token

    suspend fun registerFreel(freel: Freel): Token

    suspend fun registerFreelByLink(link: String, freel: Freel): Token

    suspend fun refresh(token: String): Token
}