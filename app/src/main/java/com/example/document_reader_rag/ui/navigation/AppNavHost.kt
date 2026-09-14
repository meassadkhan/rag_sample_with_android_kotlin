package com.example.document_reader_rag.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.document_reader_rag.ui.ask.AskScreen
import com.example.document_reader_rag.ui.home.HomeScreen
import kotlinx.serialization.Serializable

/**
 * Type-safe routes: the destination is the object itself, not a string, so a
 * typo becomes a compile error instead of a crash at navigate() time.
 */
@Serializable
data object HomeRoute

@Serializable
data object AskRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onAskClick = { navController.navigate(AskRoute) }
            )
        }

        composable<AskRoute> {
            AskScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
