package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.inventory.ui.item.NoteDetailsScreen
import com.example.inventory.ui.item.NoteEditScreen
import com.example.inventory.ui.item.NoteEntryScreen
import com.example.inventory.ui.notes.NotesScreen

@Composable
fun InventoryNavHost(
    navController: NavHostController,
    startDestination: String = "notes"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Pantalla Principal
        composable("notes") {
            NotesScreen(
                navigateToNoteEntry = { navController.navigate("note_entry") },
                // Aquí enviamos el ID, la estructura debe coincidir con la de abajo
                navigateToNoteDetail = { noteId ->
                    navController.navigate("note_details/$noteId")
                }
            )
        }

        // 2. Pantalla Crear Nota
        composable(route = "note_entry") {
            NoteEntryScreen(
                navigateBack = { navController.popBackStack() }
            )
        }

        // 3. Pantalla Detalles (AQUÍ ESTABA EL ERROR)
        // Cambiamos "noteId" por "itemId" para que coincida con lo que espera el ViewModel
        composable(
            route = "note_details/{itemId}", // <--- CAMBIO IMPORTANTE
            arguments = listOf(navArgument("itemId") { type = NavType.IntType }) // <--- CAMBIO IMPORTANTE
        ) {
            NoteDetailsScreen(
                navigateBack = { navController.popBackStack() },
                navigateToEditItem = { id -> navController.navigate("edit_note/$id") }
            )
        }

        // 4. Pantalla Editar
        composable(
            route = "edit_note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            NoteEditScreen(
                noteId = noteId,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}