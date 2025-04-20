package com.emrepbu.smsgateway.domain.repository

import com.emrepbu.smsgateway.domain.model.EmailConfig

/**
 * Interface for managing email configurations and sending emails.
 *
 * This interface defines the operations for retrieving, saving, and utilizing email configurations
 * to send emails. It abstracts the underlying email sending implementation, allowing for
 * different email providers or configurations to be used without changing the calling code.
 */
interface EmailRepository {
    /**
     * Retrieves the email configuration.
     *
     * This function attempts to fetch the email configuration data.
     * It performs a network request (or similar operation) that might take some time,
     * hence it's marked as `suspend`.
     *
     * @return An [EmailConfig] object containing the email configuration details if successful,
     *         or `null` if the configuration could not be retrieved.
     * @throws Exception If an unexpected error occurs during the retrieval process (e.g., network issue, parsing error).
     */
    suspend fun getEmailConfig(): EmailConfig?
    /**
     * Saves the provided email configuration.
     *
     * This function persists the given [EmailConfig] to a persistent storage mechanism.
     * The specific implementation of how the configuration is stored is not defined within this function's
     * scope but would typically involve writing to a file, a database, or some other storage system.
     *
     * This is a suspending function, meaning it can be safely called from coroutines and will
     * not block the calling thread while waiting for the save operation to complete.
     *
     * @param config The [EmailConfig] object to be saved. It contains all the necessary details
     *               for setting up and using email functionalities (e.g., host, port, credentials).
     * @throws Exception if any error occurs during the saving process, such as I/O errors,
     *                  database connection issues, or serialization problems. The specific type of
     *                  exception thrown will depend on the underlying implementation.
     */
    suspend fun saveEmailConfig(config: EmailConfig)
    /**
     * Sends an email to the specified recipients.
     *
     * @param to A list of email addresses to send the email to.
     * @param subject The subject of the email.
     * @param body The body content of the email.
     * @param config Optional email configuration settings. If not provided, default settings will be used.
     *               This can include information like the SMTP server address, port, username, password, etc.
     * @return A [Result] object indicating the success or failure of the email sending operation.
     *         - [Result.success] will contain [Unit] if the email was sent successfully.
     *         - [Result.failure] will contain a [Throwable] representing the error that occurred during email sending.
     * @throws IllegalArgumentException if the `to` list is empty or contains invalid email addresses.
     * @throws Exception if there's an error sending the email, such as a network error, authentication failure, or invalid email configuration.
     */
    suspend fun sendEmail(
        to: List<String>,
        subject: String,
        body: String,
        config: EmailConfig? = null,
    ): Result<Unit>
}
