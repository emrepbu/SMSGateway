package com.emrepbu.smsgateway.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emrepbu.smsgateway.data.local.entity.FilterRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterRuleDao {
    @Query("SELECT * FROM filter_rules ORDER BY createdAt DESC")
    fun getAllFilterRules(): Flow<List<FilterRuleEntity>>

    @Query("SELECT * FROM filter_rules WHERE isEnabled = 1 ORDER BY createdAt DESC")
    fun getEnabledFilterRules(): Flow<List<FilterRuleEntity>>

    @Query("SELECT * FROM filter_rules WHERE id = :id")
    suspend fun getFilterRuleById(id: Long): FilterRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilterRule(rule: FilterRuleEntity): Long

    @Update
    suspend fun updateFilterRule(rule: FilterRuleEntity)

    @Query("DELETE FROM filter_rules WHERE id = :id")
    suspend fun deleteFilterRule(id: Long)
}