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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service responsible for sending emails.
 *
 * This class provides a method to send emails using SMTP protocol with configurable options for
 * SSL/TLS encryption and authentication.
 *
 * @property No parameters needed for the constructor, as dependencies are injected.
 */
class EmailService @Inject constructor() {
    suspend fun sendEmail(
        to: List<String>,
        subject: String,
        body: String,
        config: EmailConfig
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    put("mail.smtp.host", config.smtpServer)
                    put("mail.smtp.port", config.smtpPort.toString())
                    put("mail.smtp.auth", "true")

                    if (config.useSsl) {
                        put("mail.smtp.socketFactory.port", config.smtpPort.toString())
                        put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                        put("mail.smtp.ssl.enable", "true")
                    } else {
                        put("mail.smtp.starttls.enable", "true")
                        put("mail.smtp.starttls.required", "true")
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
}
