package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DataUsageDao {
    @Query("SELECT * FROM data_usage WHERE id = 1 LIMIT 1")
    fun getDataUsage(): Flow<DataUsageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: DataUsageEntity)
}
