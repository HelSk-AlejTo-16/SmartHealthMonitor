package mx.utng.latp.smarthealthmonitort.tv.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

@Composable
fun TvPlaybackScreen(navController: NavController) {
    val ctx = LocalContext.current

    // Crear ExoPlayer ligado al ciclo de vida del Composable
    val exoPlayer = remember {
        ExoPlayer.Builder(ctx).build().apply {
            val mediaItem = MediaItem.fromUri(
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            )
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    // CRÍTICO: liberar ExoPlayer al salir del Composable
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()  // equivalente a onDestroyView en Fragment
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {

        // AndroidView envuelve el PlayerView del View system
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Botón Back en esquina superior izquierda
        Surface(
            onClick = { exoPlayer.stop(); navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color(0x88000000),
                focusedContainerColor = Color(0xCCFFFFFF)
            )
        ) {
            Text("← Volver", color = Color.White, modifier = Modifier.padding(12.dp))
        }
    }
}
