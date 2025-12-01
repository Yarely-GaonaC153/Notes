package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val fecha: Long = 0L,
    val hora: Long = 0L,

    // Este es el campo clave para tus audios.
    // Requiere que tengas el TypeConverter configurado en tu Base de Datos.
    val multimediaUris: List<String> = emptyList()
)

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * from notes WHERE id = :id")
    fun getNoteById(id: Int): Flow<Note?>

    // Ordenado por título (A-Z)
    @Query("SELECT * from notes ORDER BY title ASC")
    fun getAllNotes(): Flow<List<Note>>
}