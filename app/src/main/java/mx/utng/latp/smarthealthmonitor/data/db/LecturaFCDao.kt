package mx.utng.latp.smarthealthmonitor.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturaFCDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(lectura: LecturaFC): Long

    // Flow: actualización reactiva cuando hay nuevos datos
    @Query("""
        SELECT * FROM lecturas_fc
        ORDER BY id DESC
        LIMIT 50""")  // últimas 50 lecturas
    fun obtenerUltimas(): Flow<List<LecturaFC>>

    @Query("SELECT * FROM lecturas_fc ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<LecturaFC>>

    @Query("SELECT COUNT(*) FROM lecturas_fc")
    suspend fun contarRegistros(): Int

    // ── Nuevos para sync ───────────────────────────────────
    /** Upsert: inserta o reemplaza si el id ya existe */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lectura: LecturaFC)

    /** Obtener los registros que aún no fueron a Neon */
    @Query("SELECT * FROM lecturas_fc WHERE sincronizado = 0")
    suspend fun obtenerNoSincronizados(): List<LecturaFC>

    /** Marcar un registro como sincronizado con Neon */
    @Query("UPDATE lecturas_fc SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: Long)

    /** Contar pendientes de sync */
    @Query("SELECT COUNT(*) FROM lecturas_fc WHERE sincronizado = 0")
    fun contarPendientes(): Flow<Int>
}
