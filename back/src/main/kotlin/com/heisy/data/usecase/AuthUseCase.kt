package com.heisy.data.usecase

import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.plugins.UserTypes
import com.heisy.schema.*
import io.ktor.server.plugins.*

class AuthUseCase(
    private val userService: UserService,
    private val freelsService: FreelsService,
    private val linkService: LinksService,
    private val rowService: FreelsRowsService,
    private val tokensService: TokensService
) : IAuthUseCase {
    override suspend fun registerCompany(user: User): Token {
        val id = userService.create(user) ?: throw BadRequestException("Измените почту")
        return tokensService.generateTokenPair(id, UserTypes.User.type)
    }

    override suspend fun login(user: User): Token {
        var id: Int? = freelsService.checkAuth(user)
        return if (id != null) {
            tokensService.generateTokenPair(id, UserTypes.Freel.type)
        } else {
            id = userService.checkAuth(user) ?: throw BadRequestException("Неправильный логин или пароль")
            tokensService.generateTokenPair(id, UserTypes.User.type)
        }

    }

    override suspend fun registerFreel(freel: Freel): Token {
        val freelResult = freelsService.create(freel) ?: throw BadRequestException("Измените почту")
        return tokensService.generateTokenPair(freelResult.id.value, UserTypes.Freel.type)
    }

    override suspend fun registerFreelByLink(
        link: String,
        freel: Freel,
    ): Token {

        val exposedLink = linkService.find(link) ?: throw NotFoundException()
        if (exposedLink.isRegister == true) throw BadRequestException("По ссылке уже регистрировались")

        val exposedFreel = freelsService.create(freel) ?: throw BadRequestException("Измените почту")

        linkService.update(exposedLink.id.value, true)
        val row = rowService.findByLink(link = exposedLink)
        // У приглашения есть какая-то запись, а в ней есть профиль
        if (row != null) {
            freelsService.update(exposedFreel.id.value, row)
        }

        return tokensService.generateTokenPair(exposedFreel.id.value, UserTypes.Freel.type)
    }


    override suspend fun refresh(token: String): Token {
        return tokensService.refresh(token) ?: throw BadRequestException("token is invalid")
    }
}