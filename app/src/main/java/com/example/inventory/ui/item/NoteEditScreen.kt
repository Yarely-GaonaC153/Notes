package com.example.inventory.ui.item

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object NoteEditDestination : NavigationDestination {
    override val route = "edit_note"
    override val titleRes = R.string.edit_item_title
    const val noteIdArg = "noteId"
    val routeWithArgs = "$route/{$noteIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Int,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val noteUiState = viewModel.noteUiState
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(NoteEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->

        // AHORA SÍ COINCIDEN LOS NOMBRES
        NoteEntryBody(
            noteUiState = noteUiState,
            onValueChange = { updatedDetails ->
                // Actualizamos el ViewModel con los datos que vienen del formulario
                viewModel.updateTitle(updatedDetails.title)
                viewModel.updateContent(updatedDetails.content)
            },
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateNote()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}