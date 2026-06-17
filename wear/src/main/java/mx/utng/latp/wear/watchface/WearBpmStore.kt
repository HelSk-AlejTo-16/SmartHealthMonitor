package mx.utng.latp.wear.watchface

import android.content.Context

/**
 * Almacenamiento persistente del último BPM conocido.
 *
 * Problema: WearHealthState es in-memory. Cuando el Watch Face arranca,
 * el sensor puede tardar 10-60 s en entregar el primer valor. Durante ese
 * tiempo fcFlow == 0 y el renderer no dibuja nada.
 *
 * Solución: cada vez que llega un BPM válido lo guardamos aquí.
 * El renderer lee WearHealthState (en vivo) o, si es 0, lee el último
 * valor guardado para no mostrar la pantalla vacía.
 */
object WearBpmStore {

    private const val PREFS  = "smart_health_wear_prefs"
    private const val KEY    = "last_bpm"

    fun guardar(context: Context, bpm: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putInt(KEY, bpm).apply()
    }

    fun leerUltimo(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY, 0)
}
