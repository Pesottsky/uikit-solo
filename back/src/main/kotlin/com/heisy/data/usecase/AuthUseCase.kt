package com.heisy.data.usecase

import com.heisy.domain.usecase.IAuthUseCase
import com.heisy.errors.ExpiredException
import com.heisy.plugins.UserTypes
import com.heisy.plugins.dbQuery
import com.heisy.schema.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.util.concurrent.TimeUnit


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
        val id = freelsService.checkAuth(user)?.id?.value
        if (id != null) {
            tokensService.generateTokenPair(id, UserTypes.Freel.name)
        } else {
            val exposedUser = userService.checkAuth(user) ?: throw BadRequestException(UserService.Errors.wrongPair)

            val currentTime = System.currentTimeMillis()
            val diffInMillisec: Long = currentTime - exposedUser.registrationDate

            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillisec)
            if (diffInDays > 60) {
                if (exposedUser.paymentUntil == null) throw ExpiredException("Оплатите подписку")
                if (exposedUser.paymentUntil!! < currentTime) throw ExpiredException("Оплатите подписку")
            }

            tokensService.generateTokenPair(exposedUser.id.value, UserTypes.Company.name)
        }
    }

    override suspend fun registerFreel(freel: Freel): Token = dbQuery {
        val exposedProfile = profilesService.create(Profile(firstName = freel.firstName, lastName = freel.lastName))
        val freelResult = freelsService.create(freel, exposedProfile)
        tokensService.generateTokenPair(freelResult.id.value, UserTypes.Freel.name)
    }

    override suspend fun registerFreelByLink(
        link: String,
        freel: Freel,
    ): Token = dbQuery {

        val exposedLink = linkService.findByUUID(link) ?: throw NotFoundException()
        if (exposedLink.isRegister == true) throw BadRequestException("По ссылке уже регистрировались")

        // У приглашения есть какая-то запись, а в ней есть профиль
        val exposedProfile = exposedLink.rows.firstOrNull()?.profile ?: profilesService.create(
            Profile(
                firstName = freel.firstName,
                lastName = freel.lastName
            )
        )
        exposedProfile.firstName = freel.firstName
        exposedProfile.lastName = freel.lastName

        val exposedFreel: ExposedFreel = freelsService.create(freel, exposedProfile)
        exposedLink.isRegister = true
        tokensService.generateTokenPair(exposedFreel.id.value, UserTypes.Freel.name)
    }


    override suspend fun refresh(token: String): Token = dbQuery {
        tokensService.refresh(token) ?: throw BadRequestException("token is invalid")
    }

    override suspend fun logout(userId: Int, userType: String) {
        tokensService.logout(userId, userType)
    }

    override suspend fun updatePassword(pwd: UpdatePassword): Token = dbQuery {
        val convertUUID = UUID.fromString(pwd.code)

        // Если код не совпадает
        val exposedPassword = ExposedForgetPassword.find { TokensService.Passwords.code eq convertUUID }.singleOrNull()
            ?: throw BadRequestException(UserService.Errors.wrongPair)

        // Если логин для кода не верный
        if (exposedPassword.email != pwd.login) throw BadRequestException(UserService.Errors.wrongPair)

        if (exposedPassword.expiresAt < System.currentTimeMillis() || exposedPassword.isRecovered) throw ExpiredException(
            "code is expired"
        )

        val checkLoginInFreels = ExposedFreel.find { FreelsService.Freels.login eq pwd.login }.singleOrNull()
        val checkLoginInUsers = ExposedUser.find { UserService.Users.login eq pwd.login }.singleOrNull()
        if (checkLoginInFreels == null && checkLoginInUsers == null) throw BadRequestException(UserService.Errors.wrongPair)
        if (checkLoginInUsers != null) {
            checkLoginInUsers.password = BCrypt.hashpw(pwd.newPassword, BCrypt.gensalt())
            exposedPassword.isRecovered = true
            return@dbQuery tokensService.generateTokenPair(checkLoginInUsers.id.value, UserTypes.Company.name)
        } else {
            checkLoginInFreels!!.password = BCrypt.hashpw(pwd.newPassword, BCrypt.gensalt())
            exposedPassword.isRecovered = true
            return@dbQuery tokensService.generateTokenPair(checkLoginInFreels.id.value, UserTypes.Freel.name)
        }
    }

    override suspend fun forgetPassword(app: Application, pwd: ForgetPassword): UUID = dbQuery {
        val checkLoginInFreels = ExposedFreel.find { FreelsService.Freels.login eq pwd.login }.singleOrNull()
        val checkLoginInUsers = ExposedUser.find { UserService.Users.login eq pwd.login }.singleOrNull()
        if (checkLoginInFreels == null && checkLoginInUsers == null) throw BadRequestException(UserService.Errors.wrongPair)
        val code = dbQuery {
            tokensService.onForgetPassword(pwd)
        }
        code
    }
}