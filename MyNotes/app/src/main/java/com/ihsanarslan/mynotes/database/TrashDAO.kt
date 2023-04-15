package com.ihsanarslan.mynotes.database

import androidx.room.*

@Dao
interface TrashDao {
    @Insert
    suspend fun insert(note: TrashDB)

    @Update
    suspend fun update(note: TrashDB)

    @Delete
    suspend fun delete(note: TrashDB)

    @Query("SELECT * from TrashNotes")
    suspend fun getAllNotes(): List<TrashDB>
}