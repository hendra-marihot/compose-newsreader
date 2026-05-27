package com.hendramarihot.newsreader.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hendramarihot.newsreader.model.Category

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    SettingsContent(
        isDarkMode = preferences.isDarkMode,
        selectedCategories = preferences.selectedCategories,
        onDarkModeToggle = viewModel::onDarkModeToggle,
        onCategoryToggle = viewModel::onCategoryToggle,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    isDarkMode: Boolean,
    selectedCategories: Set<Category>,
    onDarkModeToggle: (Boolean) -> Unit,
    onCategoryToggle: (Category, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(title = { Text("Settings") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            ListItem(
                headlineContent = { Text("Dark Mode") },
                supportingContent = { Text("Use dark theme") },
                trailingContent = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onDarkModeToggle,
                    )
                },
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Categories") },
                supportingContent = { Text("Select news categories to follow") },
            )
            Category.entries.forEach { category ->
                ListItem(
                    headlineContent = {
                        Text(category.name.lowercase().replaceFirstChar { it.uppercase() })
                    },
                    trailingContent = {
                        Switch(
                            checked = category in selectedCategories,
                            onCheckedChange = { enabled ->
                                onCategoryToggle(category, enabled)
                            },
                        )
                    },
                )
            }
        }
    }
}
