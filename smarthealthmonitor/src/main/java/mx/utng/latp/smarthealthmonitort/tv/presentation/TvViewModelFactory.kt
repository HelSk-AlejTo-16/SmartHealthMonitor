package mx.utng.latp.smarthealthmonitort.tv.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.utng.latp.smarthealthmonitort.data.SmartHealthRepository

@Suppress("UNCHECKED_CAST")
class TvViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TvViewModel::class.java)) {
            return TvViewModel(SmartHealthRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
