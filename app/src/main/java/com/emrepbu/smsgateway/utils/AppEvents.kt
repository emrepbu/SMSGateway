package com.emrepbu.smsgateway.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * `AppEvents` is a singleton object responsible for managing and dispatching
 * application-wide events. It currently handles the event of an SMS message being received.
 *
 * This object uses [SharedFlow] to broadcast events to multiple collectors concurrently.
 */
object AppEvents {
    private val _smsReceivedEvent = MutableSharedFlow<Unit>(replay = 0)
    val smsReceivedEvent: SharedFlow<Unit> = _smsReceivedEvent.asSharedFlow()

    /**
     * Notifies subscribers that an SMS message has been received.
     *
     * This function emits a Unit value to the `_smsReceivedEvent` shared flow,
     * signaling that a new SMS message has been received.  Subscribers to this
     * flow will then be notified and can react accordingly.
     *
     * This is a suspend function, meaning it can be safely called from within
     * coroutines.  However, in this specific implementation the suspension is very
     * short because it just emits the value.
     *
     * The intended use case for this is to trigger an action or update UI elements
     * when an SMS message is detected by the application.
     *
     * Example Usage (in a coroutine):
     * ```
     * lifecycleScope.launch {
     *     viewModel.smsReceivedEvent.collect {
     *         // React to SMS received event, e.g., update UI
     *         Log.d("SmsReceiver", "SMS received!")
     *     }
     * }
     *
     * // Elsewhere, when an SMS is detected:
     * viewModel.notifySmsReceived()
     * ```
     *
     * @throws [Exception] if the emission to the shared flow fails.
     *
     * @see [MutableSharedFlow] for more information on shared flows.
     */
    suspend fun notifySmsReceived() {
        _smsReceivedEvent.emit(Unit)
    }
}
