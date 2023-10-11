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
        message.subject = bundle.subject
        message.setText(bundle.text)
        Transport.send(message)
    }
}

data class MailBundle(
    val to: String,
    val from: MailFrom,
    val subject: String,
    val text: String
)

enum class MailFrom(val configParam: String) {
    NO_REPLAY("no_replay"),
    INFO("info"),
    HELLO("hello"),
    SERVICE("service")
}