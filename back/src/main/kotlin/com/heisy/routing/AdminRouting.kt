package com.heisy.routing

import com.heisy.email.EmailSender
import com.heisy.email.MailBundle
import com.heisy.email.MailFrom
import com.heisy.email.MailSubjects
import com.heisy.plugins.dbQuery
import com.heisy.schema.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImportDataBase(
    @SerialName("key")
    val key: String,

    @SerialName("login")
    val login: String,

    @SerialName("profiles")
    val profiles: List<ImportProfile>,
)

@Serializable
data class ImportProfile(
    @SerialName("profile")
    val profile: Profile,

    @SerialName("comment")
    val comment: String,
)

fun Application.configureAdminRouting(
    profilesService: ProfilesService,
    rowService: FreelsRowsService,
    linksService: LinksService,
) {
    routing {
        route("/admin") {
            post {
                val importDataBase = call.receive<ImportDataBase>()
                if (application.environment.config.property("ktor.deployment.key").getString() != importDataBase.key) {
                    throw BadRequestException("key is incorrect")
                }
                val user = dbQuery {
                    ExposedUser.find { UserService.Users.login eq importDataBase.login }
                        .singleOrNull() ?: throw BadRequestException("login is incorrect")
                }

                for (profile in importDataBase.profiles) {
                    launch(SupervisorJob() + Dispatchers.IO) {
                        val link = dbQuery {
                            val profileResult = profilesService.create(profile.profile)
                            val row = rowService.create(user.tables.singleOrNull() ?: throw BadRequestException("login is incorrect"), profileResult)
                            val exposedLink = linksService.create()
                            rowService.updateLink(row, exposedLink)
                            ExposedComment.new {
                                this.comment = profile.comment
                                this.profile = profileResult
                                this.user = user
                            }
                            exposedLink.email = profile.profile.email
                            exposedLink.toDataClass()
                        }
                        EmailSender.sendMail(
                            call.application, MailBundle(
                                to = profile.profile.email ?: "",
                                from = MailFrom.HELLO,
                                subject = MailSubjects.InviteByLink,
                                text = EmailSender.getFreelInviteText(link.link)
                            )
                        )
                        dbQuery {
                            linksService.onEmailSending(link.id)
                        }
                    }

                }

            }
        }
    }
}