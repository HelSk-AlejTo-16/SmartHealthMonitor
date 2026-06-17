package mx.utng.smarthealthmonitor.wear.watchface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import mx.utng.latp.wear.presentation.healthMonitor.wear.WearHealthState
import mx.utng.latp.wear.watchface.WearBpmStore
import java.time.ZonedDateTime

class SmartHealthRenderer(
    private val context: Context,
    surfaceHolder: SurfaceHolder,
    watchState: WatchState,
    complicationSlotsManager: ComplicationSlotsManager,
    currentUserStyleRepository: CurrentUserStyleRepository,
    interactiveDrawModeUpdateDelayMillis: Long
) : Renderer.CanvasRenderer2<Renderer.SharedAssets>(
    surfaceHolder,
    currentUserStyleRepository,
    watchState,
    CanvasType.HARDWARE,
    interactiveDrawModeUpdateDelayMillis,
    clearWithBackgroundTintBeforeRenderingHighlightLayer = false
) {
    private val paintHora = Paint().apply {
        color       = Color.WHITE
        textSize    = 72f
        isAntiAlias = true
        typeface    = Typeface.DEFAULT_BOLD
    }

    private val paintFC = Paint().apply {
        color       = Color.RED
        textSize    = 30f
        isAntiAlias = true
    }

    private val paintSub = Paint().apply {
        color       = Color.GRAY
        textSize    = 22f
        isAntiAlias = true
    }

    override suspend fun createSharedAssets(): Renderer.SharedAssets =
        object : Renderer.SharedAssets { override fun onDestroy() {} }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: Renderer.SharedAssets
    ) {
        // Fondo negro — ahorra batería en modo AOD
        canvas.drawColor(Color.BLACK)

        val cx = bounds.exactCenterX()
        val cy = bounds.exactCenterY()

        // Hora digital centrada (HH:mm:ss en una sola línea)
        val hora = String.format(
            "%02d:%02d:%02d",
            zonedDateTime.hour,
            zonedDateTime.minute,
            zonedDateTime.second
        )
        val paintHoraAjustado = paintHora.also { it.textSize = 56f }  // más compacto para HH:mm:ss
        val tw = paintHoraAjustado.measureText(hora)
        canvas.drawText(hora, cx - tw / 2f, cy - 10f, paintHoraAjustado)

        // FC — estrategia en 3 niveles:
        //   1) WearHealthState (en vivo, actualizado por el sensor del servicio)
        //   2) WearBpmStore (último valor guardado, sobrevive reinicios)
        //   3) "Midiendo..." mientras el sensor todavía no entrega datos
        val fcVivo = WearHealthState.fcFlow.value
        val fc = when {
            fcVivo > 0 -> fcVivo
            else       -> WearBpmStore.leerUltimo(context)
        }

        if (fc > 0) {
            val fcStr = "❤ $fc bpm"
            val fcW   = paintFC.measureText(fcStr)
            canvas.drawText(fcStr, cx - fcW / 2f, cy + 70f, paintFC)
        } else {
            // Sensor aún calentando — mostrar texto en lugar de espacio vacío
            val midiendo = "❤ Midiendo..."
            val mW = paintSub.measureText(midiendo)
            canvas.drawText(midiendo, cx - mW / 2f, cy + 70f, paintSub)
        }
    }

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: Renderer.SharedAssets
    ) {
        renderParameters.highlightLayer?.let { layer ->
            canvas.drawColor(layer.backgroundTint)
        }
    }
}
