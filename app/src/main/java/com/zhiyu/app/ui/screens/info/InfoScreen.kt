package com.zhiyu.app.ui.screens.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun InfoScreen(viewModel: InfoViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState(initial = InfoUiState())

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Text(
            text = state.currentDate,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        androidx.compose.material3.Text(
            text = state.weekday,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium
        )
        androidx.compose.material3.Text(
            text = state.currentTime,
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge
        )
        if (state.countdownVisible) {
            androidx.compose.material3.Text(
                text = state.countdownText,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
        }
    }
}
