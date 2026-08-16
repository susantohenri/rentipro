package com.henrisusanto.rentipro.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrisusanto.rentipro.R

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel) {
    val step by viewModel.step.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
        ) {
            when (step) {
                OnboardingStep.WELCOME -> WelcomeStep(
                    onGetStarted = viewModel::onGetStarted,
                    modifier = Modifier.weight(1f),
                )
                OnboardingStep.UNITS -> UnitsStep(
                    viewModel = viewModel,
                    error = error,
                    isSaving = isSaving,
                    onBack = viewModel::goBack,
                    onContinue = viewModel::saveUnitsAndContinue,
                    modifier = Modifier.weight(1f),
                )
                OnboardingStep.PRESETS -> PresetsStep(
                    viewModel = viewModel,
                    error = error,
                    isSaving = isSaving,
                    onBack = viewModel::goBack,
                    onFinish = { viewModel.finishOnboarding {} },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WelcomeStep(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.onboarding_subtitle),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.onboarding_get_started))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitsStep(
    viewModel: OnboardingViewModel,
    error: OnboardingError?,
    isSaving: Boolean,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unitNames by viewModel.unitNames.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            title = { Text(stringResource(R.string.onboarding_setup_units_title)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.onboarding_setup_units_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            unitNames.forEachIndexed { index, name ->
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.updateUnitName(index, it) },
                    label = { Text(stringResource(R.string.onboarding_unit_name_label, index + 1)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                )
            }
            OnboardingErrorText(error = error)
        }
        OnboardingBottomBar(
            primaryLabel = stringResource(R.string.onboarding_continue),
            onPrimaryClick = onContinue,
            isLoading = isSaving,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetsStep(
    viewModel: OnboardingViewModel,
    error: OnboardingError?,
    isSaving: Boolean,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val presets by viewModel.presets.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            title = { Text(stringResource(R.string.onboarding_setup_presets_title)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                    )
                }
            },
            actions = {
                IconButton(onClick = viewModel::addPreset) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.onboarding_add_preset),
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.onboarding_setup_presets_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            presets.forEach { preset ->
                PresetDraftRow(
                    preset = preset,
                    onDurationChange = { viewModel.updatePresetDuration(preset.id, it) },
                    onPriceChange = { viewModel.updatePresetPrice(preset.id, it) },
                    onDelete = { viewModel.removePreset(preset.id) },
                    canDelete = presets.size > 1,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            TextButton(
                onClick = viewModel::addPreset,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(
                    text = stringResource(R.string.onboarding_add_preset),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            OnboardingErrorText(error = error)
        }
        OnboardingBottomBar(
            primaryLabel = stringResource(R.string.onboarding_finish),
            onPrimaryClick = onFinish,
            isLoading = isSaving,
        )
    }
}

@Composable
private fun PresetDraftRow(
    preset: OnboardingPresetDraft,
    onDurationChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = preset.durationMinutes,
            onValueChange = onDurationChange,
            label = { Text(stringResource(R.string.onboarding_preset_duration_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.onboarding_preset_arrow),
            style = MaterialTheme.typography.titleMedium,
        )
        OutlinedTextField(
            value = preset.price,
            onValueChange = onPriceChange,
            label = { Text(stringResource(R.string.onboarding_preset_price_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        if (canDelete) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                )
            }
        }
    }
}

@Composable
private fun OnboardingErrorText(error: OnboardingError?) {
    if (error != null) {
        Text(
            text = stringResource(error.messageRes),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun OnboardingBottomBar(
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Button(
            onClick = onPrimaryClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Text(primaryLabel)
            }
        }
    }
}
