package com.henrisusanto.rentipro.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrisusanto.rentipro.BuildConfig
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import com.henrisusanto.rentipro.core.model.AppLanguage
import com.henrisusanto.rentipro.core.model.ThemeMode
import com.henrisusanto.rentipro.ui.components.SettingsChoiceRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val dueSoonMinutes by viewModel.dueSoonMinutes.collectAsStateWithLifecycle()
    val presets by viewModel.presets.collectAsStateWithLifecycle()
    
    var dueSoonMinutesText by remember(dueSoonMinutes) { mutableStateOf(dueSoonMinutes.toString()) }
    var presetDurationText by remember { mutableStateOf("") }
    var presetPriceText by remember { mutableStateOf("") }
    var presetToDelete by remember { mutableStateOf<RentalPresetEntity?>(null) }
    
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingsSectionTitle(text = stringResource(R.string.settings_preferences))

            Text(
                text = stringResource(R.string.settings_language),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            AppLanguage.entries.forEach { option ->
                SettingsChoiceRow(
                    label = stringResource(option.labelRes),
                    selected = language == option,
                    onClick = { viewModel.setLanguage(option) },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = stringResource(R.string.settings_theme),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            ThemeMode.entries.forEach { option ->
                SettingsChoiceRow(
                    label = stringResource(option.labelRes),
                    selected = themeMode == option,
                    onClick = { viewModel.setThemeMode(option) },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsSectionTitle(text = stringResource(R.string.settings_rental))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_due_soon_minutes),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.settings_due_soon_minutes_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = dueSoonMinutesText,
                        onValueChange = { newText ->
                            dueSoonMinutesText = newText
                            val minutes = newText.toIntOrNull()
                            if (minutes != null && minutes in 1..120) {
                                viewModel.setDueSoonMinutes(minutes)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.settings_due_soon_minutes_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsSectionTitle(text = stringResource(R.string.settings_presets_title))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                presets.forEach { preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(R.string.rental_preset_format, preset.durationMinutes, preset.price))
                        IconButton(onClick = { presetToDelete = preset }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = presetDurationText,
                        onValueChange = { presetDurationText = it },
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(R.string.settings_preset_duration_hint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    TextField(
                        value = presetPriceText,
                        onValueChange = { presetPriceText = it },
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(R.string.settings_preset_price_hint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            val duration = presetDurationText.toIntOrNull()
                            val price = presetPriceText.toIntOrNull()
                            if (duration != null && duration > 0 && price != null && price >= 0) {
                                viewModel.addPreset(duration, price)
                                presetDurationText = ""
                                presetPriceText = ""
                            }
                        },
                    ) {
                        Text(stringResource(R.string.settings_add_preset))
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsSectionTitle(text = stringResource(R.string.settings_legal))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = stringResource(R.string.settings_about))
                    Text(text = stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME))
                }
                
                Text(
                    text = stringResource(R.string.settings_privacy_policy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tokiocv.blogspot.com/2026/07/privacy-policy.html"))
                            context.startActivity(intent)
                        },
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

    if (presetToDelete != null) {
        AlertDialog(
            onDismissRequest = { presetToDelete = null },
            title = { Text(stringResource(R.string.settings_delete_preset_title)) },
            text = { Text(stringResource(R.string.settings_delete_preset_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePreset(presetToDelete!!)
                        presetToDelete = null
                    },
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { presetToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
    )
}
