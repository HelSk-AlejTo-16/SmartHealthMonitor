package mx.utng.latp.smarthealthmonitor.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.*

/** Request genérico para la Neon HTTP API */
@Serializable
data class NeonRequest(val query: String, val params: List<String> = emptyList()) // Changed Any to String to avoid Kotlinx Serialization errors, since SQL params are usually passed as strings or we don't need @Serializable if we use Gson.

/** Response de la Neon HTTP API */
@Serializable
data class NeonResponse<T>(
    val rows        : List<T>   = emptyList(),
    val rowCount    : Int       = 0,
    val command     : String    = "",
)

/** DTO de lectura FC (mapea fila de PostgreSQL) */
@Serializable
data class LecturaFcDto(
    val id          : Int    = 0,
    val bpm         : Int,
    val estado      : String,
    val dispositivo : String,
    val hora        : String,
    val fecha       : String  = "",
    val created_at  : String  = "",
)

/** Interfaz Retrofit para la Neon HTTP API */
interface NeonApiService {

    @POST("sql")
    suspend fun executeQuery(
        @Header("Authorization") auth: String,
        @Header("Neon-Connection-String") connStr: String,
        @Body request: NeonRequest
    ): NeonResponse<LecturaFcDto>
}
