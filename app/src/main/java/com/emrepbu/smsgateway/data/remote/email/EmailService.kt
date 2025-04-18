package com.emrepbu.smsgateway.data.remote.email

import com.emrepbu.smsgateway.domain.model.EmailConfig
import java.util.Properties
import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import javax.inject.Inject

class EmailService @Inject constructor() {
    fun sendEmail(
        to: List<String>,
        subject: String,
        body: String,
        config: EmailConfig
    ): Result<Unit> {
        return try {
            val props = Properties().apply {
                put("mail.smtp.host", config.smtpServer)
                put("mail.smtp.port", config.smtpPort.toString())
                put("mail.smtp.auth", "true")

                if (config.useSsl) {
                    put("mail.smtp.socketFactory.port", config.smtpPort.toString())
                    put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                }
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(config.username, config.password)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(config.fromAddress))

                to.forEach { recipient ->
                    addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
                }

                setSubject(subject)
                setText(body)
            }

            Transport.send(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
