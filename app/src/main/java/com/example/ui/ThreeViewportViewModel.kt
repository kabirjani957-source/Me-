package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CameraView
import com.example.model.EngineTelemetry
import com.example.model.LightingPreset
import com.example.model.MaterialMode
import com.example.model.SceneConfig
import com.example.model.ScenePreset
import com.example.model.ViewportCommand
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThreeViewportViewModel : ViewModel() {

  private val _config = MutableStateFlow(SceneConfig())
  val config: StateFlow<SceneConfig> = _config.asStateFlow()

  private val _telemetry = MutableStateFlow(EngineTelemetry())
  val telemetry: StateFlow<EngineTelemetry> = _telemetry.asStateFlow()

  private val _commands = MutableSharedFlow<ViewportCommand>(extraBufferCapacity = 8)
  val commands: SharedFlow<ViewportCommand> = _commands.asSharedFlow()

  fun selectScene(scene: ScenePreset) {
    _config.update { it.copy(scene = scene) }
  }

  fun selectMaterialMode(mode: MaterialMode) {
    _config.update { it.copy(materialMode = mode) }
  }

  fun selectLightingPreset(preset: LightingPreset) {
    _config.update { it.copy(lightingPreset = preset) }
  }

  fun selectColor(hex: String) {
    _config.update { it.copy(primaryColorHex = hex) }
  }

  fun updateMetalness(value: Float) {
    _config.update { it.copy(metalness = value.coerceIn(0f, 1f)) }
  }

  fun updateRoughness(value: Float) {
    _config.update { it.copy(roughness = value.coerceIn(0.04f, 1f)) }
  }

  fun updateDistortion(value: Float) {
    _config.update { it.copy(distortion = value.coerceIn(0f, 1f)) }
  }

  fun updateAnimSpeed(value: Float) {
    _config.update { it.copy(animSpeed = value.coerceIn(0f, 2.5f)) }
  }

  fun toggleAutoRotate() {
    _config.update { it.copy(autoRotate = !it.autoRotate) }
  }

  fun toggleGrid() {
    _config.update { it.copy(showGrid = !it.showGrid) }
  }

  fun toggleParticles() {
    _config.update { it.copy(showParticles = !it.showParticles) }
  }

  fun toggleHud() {
    _config.update { current ->
      val nextHud = !current.showHud
      current.copy(showHud = nextHud, inspectorOpen = if (!nextHud) false else current.inspectorOpen)
    }
  }

  fun setInspectorOpen(open: Boolean) {
    _config.update { it.copy(inspectorOpen = open) }
  }

  fun toggleInspector() {
    _config.update { it.copy(inspectorOpen = !it.inspectorOpen) }
  }

  fun requestCameraView(view: CameraView) {
    viewModelScope.launch { _commands.emit(ViewportCommand.SetCamera(view)) }
  }

  fun resetAll() {
    _config.value = SceneConfig()
    viewModelScope.launch { _commands.emit(ViewportCommand.ResetScene) }
  }

  fun retryEngine() {
    _telemetry.update { it.copy(isReady = false, errorMessage = null, engineName = "Retrying…") }
    viewModelScope.launch { _commands.emit(ViewportCommand.ReloadEngine) }
  }

  fun onEngineReady(engineName: String, isFallback: Boolean) {
    _telemetry.update {
      it.copy(
        isReady = true,
        engineName = engineName,
        isFallbackMode = isFallback,
        errorMessage = null,
      )
    }
  }

  fun onEngineError(message: String) {
    _telemetry.update { it.copy(errorMessage = message) }
  }

  fun onTelemetryUpdate(
    fps: Int,
    triangles: Int,
    vertices: Int,
    azimuthDeg: Int,
    elevationDeg: Int,
    distance: Float,
  ) {
    _telemetry.update {
      it.copy(
        fps = fps,
        triangles = triangles,
        vertices = vertices,
        azimuthDeg = azimuthDeg,
        elevationDeg = elevationDeg,
        distance = distance,
      )
    }
  }
}
