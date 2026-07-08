package mx.utng.latp.smarthealthmonitort.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.utng.latp.smarthealthmonitort.tv.presentation.TvCatalogScreen
import mx.utng.latp.smarthealthmonitort.tv.presentation.TvDetailScreen
import mx.utng.latp.smarthealthmonitort.tv.presentation.TvPlaybackScreen

/**
 * Actividad principal para el módulo de TV.
 * Utiliza Jetpack Compose y Navigation para manejar las pantallas.
 */
class TVActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHealthTvTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "catalog") {
                    composable("catalog") {
                        TvCatalogScreen(onCardClick = { lecturaId ->
                            navController.navigate("detail/$lecturaId")
                        })
                    }
                    composable(
                        route = "detail/{lecturaId}",
                        arguments = listOf(navArgument("lecturaId") { type = NavType.IntType })
                    ) { backStack ->
                        val id = backStack.arguments?.getInt("lecturaId") ?: return@composable
                        TvDetailScreen(lecturaId = id, navController = navController)
                    }
                    composable("playback") {
                        TvPlaybackScreen(navController = navController)
                    }
                }
            }
        }
    }
}

// ==========================================
// STUBS TEMPORALES (Para que compile mientras 
// pasas el código real de estas pantallas):
// ==========================================

@Composable
fun SmartHealthTvTheme(content: @Composable () -> Unit) {
    // Tema temporal
    content()
}


