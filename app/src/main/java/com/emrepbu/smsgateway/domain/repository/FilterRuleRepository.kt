package com.emrepbu.smsgateway.domain.repository

import com.emrepbu.smsgateway.domain.model.FilterRule
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [FilterRule] data.
 *
 * This interface defines the operations for retrieving, inserting, updating, and deleting filter rules.
 */
interface FilterRuleRepository {
    /**
     * Retrieves all filter rules.
     *
     * This function returns a Flow that emits a list of all currently active filter rules.
     * The Flow will emit an updated list whenever the set of filter rules changes.
     *
     * Each `FilterRule` in the emitted list represents a single filter rule,
     * defining a condition or criteria used for filtering data.
     *
     * This function can be used to:
     *   - Display the currently active filter rules to the user.
     *   - React to changes in the filter rules and update the UI or application state accordingly.
     *   - Apply the retrieved filter rules to a data source for filtering purposes.
     *
     * @return A Flow emitting a list of `FilterRule` objects. The list will be empty if no filter rules are currently defined.
     *         The Flow will continue to emit new lists as filter rules are added, removed, or modified.
     */
    fun getAllFilterRules(): Flow<List<FilterRule>>
    /**
     * Retrieves a flow of enabled filter rules.
     *
     * This function returns a [Flow] that emits a list of [FilterRule] objects.
     * Each emitted list represents the currently enabled filter rules at the time of emission.
     * The flow will emit new lists whenever the set of enabled filter rules changes.
     *
     * A filter rule is considered "enabled" if its state (as determined within the FilterRule object)
     * indicates that it should be actively applied.
     *
     * This function is typically used to observe changes in the enabled filter rules
     * and react accordingly, such as updating the UI or re-filtering data.
     *
     * @return A [Flow] that emits a list of [FilterRule] objects representing the currently enabled filter rules.
     */
    fun getEnabledFilterRules(): Flow<List<FilterRule>>
    /**
     * Retrieves a [FilterRule] from the data source by its unique identifier.
     *
     * This function asynchronously fetches a filter rule based on the provided ID.
     * If a filter rule with the given ID exists, it will be returned.
     * Otherwise, `null` will be returned.
     *
     * @param id The unique identifier of the filter rule to retrieve.
     * @return The [FilterRule] associated with the given ID, or `null` if no such rule exists.
     */
    suspend fun getFilterRuleById(id: Long): FilterRule?
    /**
     * Inserts a new filter rule into the data store.
     *
     * This function takes a [FilterRule] object, persists it to the underlying data store (e.g., a database),
     * and returns the ID of the newly inserted rule. The operation is performed within a coroutine, allowing
     * for non-blocking I/O operations.
     *
     * @param rule The [FilterRule] object to be inserted.
     * @return The ID (primary key) of the newly inserted filter rule. Returns -1 if insertion failed.
     * @throws Exception if any error occurs during the insertion process (e.g., database error,
     *      constraint violation).
     *
     * @see FilterRule
     *
     * Example Usage:
     * ```kotlin
     * val newRule = FilterRule(type = "email", value = "@example.com", action = "block")
     * CoroutineScope(Dispatchers.IO).launch {
     *   try {
     *      val insertedId = insertFilterRule(newRule)
     *      if (insertedId != -1L) {
     *          println("Successfully inserted rule with ID: $insertedId")
     *      } else {
     *          println("Failed to insert rule")
     *      }
     *   } catch(e: Exception) {
     *       println("Error inserting rule: ${e.message}")
     *   }
     * }
     * ```
     */
    suspend fun insertFilterRule(rule: FilterRule): Long
    /**
     * Updates an existing filter rule.
     *
     * This function takes a [FilterRule] object as input and updates the corresponding
     * filter rule in the underlying data store. The update operation is performed
     * asynchronously.
     *
     * @param rule The [FilterRule] object containing the updated information.
     *             The rule must have a valid identifier (e.g., `rule.id`) that
     *             corresponds to an existing filter rule.
     *
     * @throws IllegalArgumentException If the provided `rule` object is invalid,
     *                                  e.g., if it's missing a necessary identifier.
     * @throws IllegalStateException If the underlying data store is in an
     *                                  inconsistent state or the update operation fails.
     * @throws Exception If any other error occurs during the update process.
     */
    suspend fun updateFilterRule(rule: FilterRule)
    /**
     * Deletes a filter rule with the specified ID.
     *
     * This function asynchronously deletes a filter rule from the data source.
     * If a rule with the given ID does not exist, the function will complete
     * successfully without performing any action. No error will be thrown in this case.
     *
     * @param id The unique identifier of the filter rule to delete.
     * @throws Exception if an error occurs during the deletion process.
     */
    suspend fun deleteFilterRule(id: Long)
}
