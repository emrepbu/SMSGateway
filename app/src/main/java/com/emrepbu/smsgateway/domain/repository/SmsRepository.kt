package com.emrepbu.smsgateway.domain.repository

import com.emrepbu.smsgateway.domain.model.SmsMessage
import kotlinx.coroutines.flow.Flow

/**
 * Interface for interacting with SMS messages.
 * This repository provides methods to access, retrieve, and manage SMS messages,
 * including fetching from the system and updating message status.
 */
interface SmsRepository {
    /**
     * Retrieves all SMS messages from the device's SMS inbox.
     *
     * This function accesses the device's content provider to query for all SMS messages.
     * It returns a Flow that emits a list of [SmsMessage] objects.
     * The list is emitted whenever there are changes in the SMS database.
     * This means that new messages, deleted messages, or changes to existing messages
     * will trigger a new emission from the Flow.
     *
     * **Permissions:**
     *  - This function requires the `READ_SMS` permission to be granted by the user.
     *    If the permission is not granted, the flow will not emit any messages and an empty list will be returned.
     *
     * **Note:**
     *  - This function operates asynchronously and returns a [Flow].
     *    You must collect the flow in a coroutine scope to receive the list of messages.
     *  - The returned list contains all SMS messages found in the device's inbox, regardless of their read status.
     *  - The [SmsMessage] contains the following information:
     *      - `id`: The unique ID of the SMS message.
     *      - `address`: The sender's phone number.
     *      - `body`: The message content.
     *      - `date`: The timestamp of the message.
     *      - `read`: Boolean indicating if the message is read or not.
     *      - `type`: Type of the message (e.g., received, sent).
     *
     * @return A [Flow] that emits a list of [SmsMessage] objects.
     *         If no messages are found or the permission is denied, an empty list is emitted.
     */
    fun getAllSmsMessage(): Flow<List<SmsMessage>>
    /**
     * Retrieves a flow of lists of SMS messages that have not yet been marked as forwarded.
     *
     * This function queries the underlying data source (e.g., a database or content provider)
     * for SMS messages and filters them to include only those that have not been previously
     * identified as forwarded. The specific criteria for determining if a message has been
     * forwarded is implementation-dependent, but could involve checking a flag or timestamp
     * associated with each SMS message.
     *
     * The flow emits a list of `SmsMessage` objects each time there's an update to the
     * set of unforwarded messages. This allows for reactive handling of changes to the
     * unforwarded message pool.
     *
     * Note:
     * - The returned flow will continue to emit lists of unforwarded messages as the underlying
     *   data changes, such as when new messages arrive or existing messages are marked as
     *   forwarded.
     * - The flow is cold, meaning it only starts emitting data when a collector subscribes to it.
     * - Implementors should ensure that the underlying data source is observed for changes
     *   and that these changes trigger new emissions in the flow.
     *
     * @return A [Flow] that emits lists of [SmsMessage] objects representing the unforwarded
     *         SMS messages. Each emission is a snapshot of the current unforwarded messages.
     */
    fun getUnforwardedSmsMessages(): Flow<List<SmsMessage>>
    /**
     * Retrieves an SMS message by its unique identifier.
     *
     * This function fetches an SMS message from the underlying data source based on the provided ID.
     * It is a suspending function, meaning it can be safely called from a coroutine and will not block the main thread.
     *
     * @param id The unique identifier of the SMS message to retrieve.
     * @return An [SmsMessage] object if an SMS message with the given ID is found, or `null` if no such message exists.
     */
    suspend fun getSmsById(id: String): SmsMessage?
    /**
     * Inserts an SMS message into the message storage.
     *
     * This function is responsible for persisting an [SmsMessage] object.
     * It handles the details of storing the message, such as its content,
     * sender, timestamp, and potentially other relevant metadata, into a
     * database or another suitable persistent storage mechanism.
     *
     * This function is marked as `suspend` indicating that it might perform
     * long-running operations (like database I/O) and should be called within
     * a coroutine or another suspending function.
     *
     * @param sms The [SmsMessage] object to be inserted into storage. This object
     *            contains all the necessary information about the SMS, such as the
     *            message body, sender's address, and timestamp.
     *
     * @throws Exception if an error occurs during the message insertion process, such as a
     *                     database error or storage failure. Specific error types may vary
     *                     depending on the underlying storage implementation.
     */
    suspend fun insertMessage(sms: SmsMessage)
    /**
     * Updates the forwarding status of an SMS message.
     *
     * This function modifies the status of an SMS message identified by its unique ID.
     * It allows marking the message as forwarded or not, specifying the recipients it was
     * forwarded to, and recording the timestamp of the forwarding event.
     *
     * @param id The unique identifier of the SMS message to update. This is a non-nullable String.
     * @param isForwarded A boolean indicating whether the SMS message has been forwarded.
     *                    `true` if the message was forwarded, `false` otherwise.
     * @param forwardedTo A list of strings representing the phone numbers or identifiers
     *                    to which the SMS message was forwarded. Can be an empty list if
     *                    the message was not forwarded or if the recipients are unknown.
     * @param forwardedAt An optional timestamp (in milliseconds since the epoch) representing
     *                    the time when the SMS message was forwarded. If the message has not
     *                    been forwarded, or if the forwarding time is unknown, this can be `null`.
     *                    If `isForwarded` is true, it's recommended to set a valid timestamp here.
     *
     * @throws Exception if any error occurs during the update process.
     *
     * @sample
     *   // Example usage:
     *   updateSmsForwardStatus(
     *       "sms123",
     *       true,
     *       listOf("+15551234567", "+15559876543"),
     *       System.currentTimeMillis()
     *   )
     *
     *   // Example usage for a message not yet forwarded:
     *   updateSmsForwardStatus(
     *       "sms456",
     *       false,
     *       emptyList(),
     *       null
     *   )
     */
    suspend fun updateSmsForwardStatus(
        id: String,
        isForwarded: Boolean,
        forwardedTo: List<String>,
        forwardedAt: Long?
    )
    /**
     * Refreshes the list of SMS messages from the system's SMS provider.
     *
     * This function fetches all SMS messages from the system's default SMS provider
     * and updates the application's internal representation of SMS messages.
     * This is a suspending function, meaning it should be called within a coroutine
     * or another suspending function.  It is intended for situations where the
     * application needs to ensure it has the most up-to-date view of the user's
     * SMS messages.
     *
     * This function typically performs the following actions:
     * 1. Queries the system's ContentResolver for SMS messages.
     * 2. Parses the retrieved data to create a list of SMS message objects.
     * 3. Updates the application's data store or UI with the new SMS messages.
     *
     * Note:
     * - This function may take some time to complete, depending on the number of
     *   SMS messages on the device and the performance of the system's SMS provider.
     * - It likely requires appropriate permissions to read SMS messages (e.g., READ_SMS).
     * - Any error or exception that happens in the process will need to be handled
     *   by the calling function (e.g. with try-catch blocks).
     *
     * Example usage within a coroutine:
     * ```kotlin
     * lifecycleScope.launch {
     *     try {
     *         refreshSmsFromSystem()
     *         // Update the UI or perform other actions after refreshing SMS messages.
     *     } catch (e: SecurityException){
     *         // Handle permission error
     *     } catch (e: Exception){
     *         // Handle any other kind of error
     *     }
     * }
     * ```
     *
     * @throws SecurityException if the application does not have the necessary permissions to
     * access SMS messages.
     * @throws Exception if an unexpected error occurs during the refresh process.
     */
    suspend fun refreshSmsFromSystem()
}
