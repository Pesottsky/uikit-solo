package com.heisy.data.usecase

import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.plugins.UserTypes
import com.heisy.plugins.dbQuery
import com.heisy.schema.*
import io.ktor.server.plugins.*
import org.mindrot.jbcrypt.BCrypt

class AuthUseCase(
    private val userService: UserService,
    private val freelsService: FreelsService,
    private val linkService: LinksService,
    private val tokensService: TokensService,
    private val profilesService: ProfilesService
) : IAuthUseCase {
    override suspend fun registerCompany(user: User): Token = dbQuery {
        val id = userService.create(user).id.value
        tokensService.generateTokenPair(id, UserTypes.Company.name)
    }

    override suspend fun login(user: User): Token = dbQuery {
        var id = freelsService.checkAuth(user)?.id?.value
        if (id != null) {
            tokensService.generateTokenPair(id, UserTypes.Freel.name)
        } else {
            id = userService.checkAuth(user)?.id?.value ?: throw BadRequestException(UserService.Errors.wrongPair)
            tokensService.generateTokenPair(id, UserTypes.Company.name)
        }
    }

    override suspend fun registerFreel(freel: Freel): Token = dbQuery {
        val freelResult = freelsService.create(freel)
        tokensService.generateTokenPair(freelResult.id.value, UserTypes.Freel.name)
    }

    override suspend fun registerFreelByLink(
        link: String,
        freel: Freel,
    ): Token = dbQuery {

        val exposedLink = linkService.findByUUID(link) ?: throw NotFoundException()
        if (exposedLink.isRegister == true) throw BadRequestException("По ссылке уже регистрировались")

        val exposedFreel = freelsService.create(freel)
        exposedLink.isRegister = true

        // У приглашения есть какая-то запись, а в ней есть профиль
        if (exposedLink.rows.empty()) {
            val exposedProfile = profilesService.create(Profile(firstName = freel.firstName, lastName = freel.lastName))
            exposedFreel.profile = exposedProfile
        } else {
            exposedFreel.profile = exposedLink.rows.firstOrNull()?.profile ?: profilesService.create(
                Profile(
                    firstName = freel.firstName,
                    lastName = freel.lastName
                )
            )
        }

        tokensService.generateTokenPair(exposedFreel.id.value, UserTypes.Freel.name)
    }


    override suspend fun refresh(token: String, userId: Int, userType: String): Token = dbQuery {
        tokensService.refresh(token, userId, userType) ?: throw BadRequestException("token is invalid")
    }

    override suspend fun logout(userId: Int, userType: String) {
        tokensService.logout(userId, userType)
    }

    override suspend fun updatePassword(pwd: UpdatePassword): Token = dbQuery {

        // TODO тут полная хуйня, надо писать на почту
        val checkLoginInFreels = ExposedFreel.find { FreelsService.Freels.login eq pwd.login }.singleOrNull()
        val checkLoginInUsers = ExposedUser.find { UserService.Users.login eq pwd.login }.singleOrNull()
        if (checkLoginInFreels == null && checkLoginInUsers == null) throw BadRequestException(UserService.Errors.wrongPair)
        if (checkLoginInUsers != null) {
            if (!BCrypt.checkpw(
                    pwd.oldPassword,
                    checkLoginInUsers.password
                )
            ) throw BadRequestException(UserService.Errors.wrongPair)
            checkLoginInUsers.password = BCrypt.hashpw(pwd.newPassword, BCrypt.gensalt())
            return@dbQuery tokensService.generateTokenPair(checkLoginInUsers.id.value, UserTypes.Company.name)
        } else {
            if (!BCrypt.checkpw(
                    pwd.oldPassword,
                    checkLoginInFreels!!.password
                )
            ) throw BadRequestException(UserService.Errors.wrongPair)
            checkLoginInFreels.password = BCrypt.hashpw(pwd.newPassword, BCrypt.gensalt())
            return@dbQuery tokensService.generateTokenPair(checkLoginInFreels.id.value, UserTypes.Freel.name)

        }
    }
}