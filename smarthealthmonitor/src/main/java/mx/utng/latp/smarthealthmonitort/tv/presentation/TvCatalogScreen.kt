package mx.utng.latp.smarthealthmonitort.tv.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.tv.material3.Text
import androidx.tv.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TvCatalogScreen(
    onCardClick: (Int) -> Unit, // Pasado desde el NavHost del TVActivity
    viewModel: TvViewModel = viewModel(factory = TvViewModelFactory(LocalContext.current))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize().background(Color(0xFF0D1B4A))) {

        if (state.isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
            return@Box
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(48.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Fila 1: FC actual
            item {
                RowSection(title = "⚡ Estado Actual — ${state.fcActual} bpm") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(state.lecturas.takeLast(3)) { lectura ->
                            FcCardItem(lectura = lectura, onClick = { onCardClick(lectura.id) })
                        }
                    }
                }
            }

            // Fila 2: Historial completo
            item {
                RowSection(title = "📋 Historial FC") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(state.lecturas) { lectura ->
                            FcCardItem(lectura = lectura, onClick = { onCardClick(lectura.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title, 
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White, 
            fontWeight = FontWeight.Bold
        )
        content()
    }
}
