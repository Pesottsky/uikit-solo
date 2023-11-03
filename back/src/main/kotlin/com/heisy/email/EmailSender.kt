package com.heisy.email

import io.ktor.server.application.*
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    private const val host = "mail.hosting.reg.ru"
    private const val port = 465

    private val properties = Properties().apply {
        put("mail.smtp.host", host)
        put("mail.smtp.port", port)
        put("mail.smtp.ssl.enable", "true")
        put("mail.smtp.auth", "true")
    }

    fun sendMail(app: Application, bundle: MailBundle) {
        val password = app.environment.config.property("smtp.${bundle.from.configParam}.password").getString()
        val from = app.environment.config.property("smtp.${bundle.from.configParam}.login").getString()


        val session: Session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(from, password)
            }
        })
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(from))
        message.setRecipient(Message.RecipientType.TO, InternetAddress(bundle.to))
        message.subject = bundle.subject.text
        message.setText(bundle.text)
        Transport.send(message)
    }

    fun getFreelInviteText(link: String): String {
        return "Привет! Это приглашение от компании, которая хочет вас добавить в свою базу фрилансеров. Так она сможет видеть вашу занятость, портфолио и не потеряет ваш контакт.\n" +
                "\n" +
                "1. Перейдите по ссылке $link и зарегистрируйтесь\n" +
                "2. Заполните информацию о себе \n" +
                "3. Обновляйте ваш статус, когда открыты к проектам, а когда заняты\n" +
                "\n" +
                "Вы автоматически добавитесь в базу компании после регистрации\n" +
                "\n" +
                "Платформа Soloteam помогает компаниям создавать и работать с базой фрилансеров, вовремя находить людей на проекты и быстрее согласовывать условия.\n" +
                "\n" +
                "Вы сможете:\n" +
                "— быстро делиться профилем фрилансера с заказчиками\n" +
                "— обновлять статус занятости, чтобы компании знали когда прийти к вам с проектом\n" +
                "— быть на связи с заказчиками\n" +
                "\n" +
                "Мы всегда рады вам помочь, любые вопросы по работе сервиса и предложения — пишите нам на service@soloteam.io или лично в телеграм @vviiktoor\n" +
                "\n" +
                "Рады видеть вас в Soloteam"
    }
}

data class MailBundle(
    val to: String,
    val from: MailFrom,
    val subject: MailSubjects,
    val text: String
)

enum class MailFrom(val configParam: String) {
    NO_REPLAY("no_replay"),
    INFO("info"),
    HELLO("hello"),
    SERVICE("service")
}

enum class MailSubjects(val text: String) {
    PasswordRecovery("Восстановление пароля"),
    InviteByLink("Вас пригласили на фриланс-платформу Soloteam"),
    Registration("Рады видеть вас в Soloteam!")
}