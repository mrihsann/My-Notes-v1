package com.ihsanarslan.mynotes.database

import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: NoteDB)

    @Update
    suspend fun update(note: NoteDB)

    @Delete
    suspend fun delete(note: NoteDB)

    @Query("SELECT * from Notes")
    suspend fun getAllNotes(): List<NoteDB>
}