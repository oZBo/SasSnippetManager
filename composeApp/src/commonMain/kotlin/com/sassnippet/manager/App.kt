package com.sassnippet.manager

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import com.sassnippet.manager.ui.create.CreateSnippetScreen
import com.sassnippet.manager.ui.detail.SnippetDetailScreen
import com.sassnippet.manager.ui.list.SnippetListScreen

@Composable
@Preview
fun App() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            SnippetListScreen(
                onSnippetClick = { id -> navController.navigate("detail/$id") },
                onCreateClick = { navController.navigate("create") }
            )
        }
        composable("detail/{id}") { backStack ->
            val id = backStack.arguments?.read { getString("id") }?.toIntOrNull() ?: return@composable
            SnippetDetailScreen(
                snippetId = id,
                onBack = { navController.popBackStack() }
            )
        }
        composable("create") {
            CreateSnippetScreen(
                onBack = { navController.popBackStack() },
                onCreated = { navController.popBackStack() }
            )
        }
    }
}