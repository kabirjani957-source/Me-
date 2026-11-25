package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CameraView
import com.example.ui.ThreeViewportViewModel
import com.example.ui.components.StudioHudOverlay
import com.example.ui.components.ThreeWebView
import com.example.ui.theme.Prism3DTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
    )
    setContent {
      Prism3DTheme {
        ThreeViewportScreen()
      }
    }
  }
}

@Composable
fun ThreeViewportScreen(viewModel: ThreeViewportViewModel = viewModel()) {
  val config by viewModel.config.collectAsStateWithLifecycle()
  val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()

  // Handle back button gracefully for Inspector drawer and Zen Mode
  BackHandler(enabled = config.inspectorOpen || !config.showHud) {
    when {
      config.inspectorOpen -> viewModel.setInspectorOpen(false)
      !config.showHud -> viewModel.toggleHud()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    ThreeWebView(
      config = config,
      commands = viewModel.commands,
      onEngineReady = viewModel::onEngineReady,
      onEngineError = viewModel::onEngineError,
      onTelemetry = viewModel::onTelemetryUpdate,
      modifier = Modifier.fillMaxSize(),
    )

    StudioHudOverlay(
      config = config,
      telemetry = telemetry,
      onSelectScene = viewModel::selectScene,
      onSelectMaterial = viewModel::selectMaterialMode,
      onSelectLighting = viewModel::selectLightingPreset,
      onSelectColor = viewModel::selectColor,
      onMetalnessChange = viewModel::updateMetalness,
      onRoughnessChange = viewModel::updateRoughness,
      onDistortionChange = viewModel::updateDistortion,
      onAnimSpeedChange = viewModel::updateAnimSpeed,
      onToggleAutoRotate = viewModel::toggleAutoRotate,
      onToggleGrid = viewModel::toggleGrid,
      onToggleParticles = viewModel::toggleParticles,
      onToggleHud = viewModel::toggleHud,
      onToggleInspector = viewModel::toggleInspector,
      onCloseInspector = { viewModel.setInspectorOpen(false) },
      onCameraView = viewModel::requestCameraView,
      onResetCamera = { viewModel.requestCameraView(CameraView.PERSPECTIVE) },
      onResetAll = viewModel::resetAll,
      onRetryEngine = viewModel::retryEngine,
      modifier = Modifier.fillMaxSize(),
    )
  }
}
