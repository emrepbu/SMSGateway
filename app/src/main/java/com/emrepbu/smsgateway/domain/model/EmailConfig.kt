package com.emrepbu.smsgateway.domain.model

/**
 * Represents the configuration for sending emails.
 *
 * This data class holds all the necessary information to connect to an SMTP server
 * and send emails. It includes details like the server address, port, authentication
 * credentials, and the sender's email address.
 *
 * @property id A unique identifier for this email configuration. Defaults to 0. This could
 *             be used to differentiate between multiple configurations if needed.
 * @property smtpServer The address of the SMTP server (e.g., "smtp.gmail.com").
 * @property smtpPort The port number used for the SMTP connection (e.g., 587 for TLS, 465 for SSL).
 * @property username The username for authenticating with the SMTP server.
 * @property password The password for authenticating with the SMTP server.
 * @property fromAddress The email address that will appear as the sender of the emails.
 * @property useSsl A flag indicating whether to use SSL/TLS for the connection. Defaults to true.
 */
data class EmailConfig(
    val id: Long = 0,
    val smtpServer: String,
    val smtpPort: Int,
    val username: String,
    val password: String,
    val fromAddress: String,
    val useSsl: Boolean = true,
)
