package com.software.biliapp.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BiliLoginUiScreen(
    viewModel: BiliLoginUiViewModel = hiltViewModel(),
    onNavigateToRecommend: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is BiliLoginUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is BiliLoginUiEffect.NavigateToRecommend -> {
                    onNavigateToRecommend()
                }
            }
        }
    }
    BiliLoginUiQrCode(
        uiState = uiState.value,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun BiliLoginUiQrCode(
    uiState: BiliLoginUiState,
    onEvent: (BiliLoginUiEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "二维码")
        OutlinedButton(
            onClick = {
                onEvent(BiliLoginUiEvent.BiliQrCodeData)
            }
        ) {
            Text(text = "二维码登录")
        }
        if (uiState.qrBitmap != null) {
            Image(
                bitmap = uiState.qrBitmap.asImageBitmap(),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "请使用哔哩哔哩APP扫码登录")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = uiState.qrPollData?.code.toString())
        }
    }
}

@Composable
fun BiliLoginUiContentPassword(
    uiState: BiliLoginUiState,
    onEvent: (BiliLoginUiEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.currentUsername,
            onValueChange = {
                onEvent(BiliLoginUiEvent.UpdateCurrentUsername(it))
            },
            label = {
                Text(text = "用户名")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.currentPassword,
            onValueChange = {
                onEvent(BiliLoginUiEvent.UpdateCurrentPassword(it))
            },
            label = {
                Text(text = "密码")
            }
        )
    }
}