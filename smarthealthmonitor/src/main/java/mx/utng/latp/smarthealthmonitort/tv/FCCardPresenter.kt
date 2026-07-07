package mx.utng.latp.smarthealthmonitort.tv

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter

// Modelo de datos temporal para la TV (se debe conectar con tus datos reales)
data class LecturaFC(
    val id: Int,
    val valorBpm: Int,
    val hora: String,
    val esNormal: Boolean = true
)

class FCCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            // CRÍTICO: sin estas dos líneas,
            // el D-pad no puede navegar a este card
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(240, 180)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val card = viewHolder.view as ImageCardView
        val lectura = item as LecturaFC

        card.titleText = "${lectura.valorBpm} bpm"
        card.contentText = lectura.hora

        // Color de fondo según si FC es normal
        val bgColor = if (lectura.esNormal) {
            Color.parseColor("#1B4F8A")  // primary
        } else {
            Color.parseColor("#B3261E")  // error
        }
        card.setBackgroundColor(bgColor)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val card = viewHolder.view as ImageCardView
        card.badgeImage = null
        card.mainImage = null
    }
}
