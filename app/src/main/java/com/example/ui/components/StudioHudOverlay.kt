package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.Hexagon
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CameraView
import com.example.model.EngineTelemetry
import com.example.model.LightingPreset
import com.example.model.MaterialMode
import com.example.model.SceneConfig
import com.example.model.ScenePreset
import com.example.ui.theme.AmberPrism
import com.example.ui.theme.CyanElectric
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.MonoTelemetryStyle
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StudioHudOverlay(
  config: SceneConfig,
  telemetry: EngineTelemetry,
  onSelectScene: (ScenePreset) -> Unit,
  onSelectMaterial: (MaterialMode) -> Unit,
  onSelectLighting: (LightingPreset) -> Unit,
  onSelectColor: (String) -> Unit,
  onMetalnessChange: (Float) -> Unit,
  onRoughnessChange: (Float) -> Unit,
  onDistortionChange: (Float) -> Unit,
  onAnimSpeedChange: (Float) -> Unit,
  onToggleAutoRotate: () -> Unit,
  onToggleGrid: () -> Unit,
  onToggleParticles: () -> Unit,
  onToggleHud: () -> Unit,
  onToggleInspector: () -> Unit,
  onCloseInspector: () -> Unit,
  onCameraView: (CameraView) -> Unit,
  onResetCamera: () -> Unit,
  onResetAll: () -> Unit,
  onRetryEngine: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWideLayout = maxWidth >= 720.dp

    // Loading state overlay while WebGL initializes
    AnimatedVisibility(
      visible = !telemetry.isReady,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.Center),
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = StudioSurface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, StudioBorder),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.5.dp,
            color = CyanElectric,
          )
          Text(
            text = stringResource(R.string.engine_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
          )
        }
      }
    }

    // Zen Mode restore pill when HUD is hidden
    AnimatedVisibility(
      visible = !config.showHud,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(16.dp),
    ) {
      Surface(
        shape = CircleShape,
        color = StudioSurface.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, StudioBorder),
        modifier = Modifier.testTag("restore_hud_button"),
      ) {
        IconButton(onClick = onToggleHud, modifier = Modifier.size(48.dp)) {
          Icon(
            imageVector = Icons.Rounded.Visibility,
            contentDescription = stringResource(R.string.cd_toggle_hud),
            tint = CyanElectric,
          )
        }
      }
    }

    // Full Studio HUD
    AnimatedVisibility(
      visible = config.showHud,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.fillMaxSize(),
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        // Top Telemetry & Action Bar
        TopTelemetryBar(
          config = config,
          telemetry = telemetry,
          onToggleAutoRotate = onToggleAutoRotate,
          onResetCamera = onResetCamera,
          onToggleHud = onToggleHud,
          onRetryEngine = onRetryEngine,
          modifier =
            Modifier.align(Alignment.TopCenter)
              .statusBarsPadding()
              .padding(horizontal = 12.dp, vertical = 10.dp),
        )

        // Wide Layout: Docked Right Inspector Rail
        if (isWideLayout) {
          AnimatedVisibility(
            visible = config.inspectorOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier =
              Modifier.align(Alignment.CenterEnd)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 74.dp, bottom = 108.dp, end = 16.dp)
                .width(320.dp)
                .fillMaxHeight(),
          ) {
            InspectorPanel(
              config = config,
              onSelectMaterial = onSelectMaterial,
              onSelectLighting = onSelectLighting,
              onSelectColor = onSelectColor,
              onMetalnessChange = onMetalnessChange,
              onRoughnessChange = onRoughnessChange,
              onDistortionChange = onDistortionChange,
              onAnimSpeedChange = onAnimSpeedChange,
              onToggleGrid = onToggleGrid,
              onToggleParticles = onToggleParticles,
              onResetAll = onResetAll,
              onClose = onCloseInspector,
            )
          }
        }

        // Bottom Controls Stack (Inspector Sheet on Compact + Camera Bar + Scene Carousel)
        Column(
          modifier =
            Modifier.align(Alignment.BottomCenter)
              .navigationBarsPadding()
              .padding(bottom = 12.dp)
              .widthIn(max = 760.dp)
              .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          if (!isWideLayout) {
            AnimatedVisibility(
              visible = config.inspectorOpen,
              enter = slideInVertically { it / 2 } + fadeIn(),
              exit = slideOutVertically { it / 2 } + fadeOut(),
              modifier = Modifier.padding(horizontal = 12.dp).heightIn(max = 350.dp),
            ) {
              InspectorPanel(
                config = config,
                onSelectMaterial = onSelectMaterial,
                onSelectLighting = onSelectLighting,
                onSelectColor = onSelectColor,
                onMetalnessChange = onMetalnessChange,
                onRoughnessChange = onRoughnessChange,
                onDistortionChange = onDistortionChange,
                onAnimSpeedChange = onAnimSpeedChange,
                onToggleGrid = onToggleGrid,
                onToggleParticles = onToggleParticles,
                onResetAll = onResetAll,
                onClose = onCloseInspector,
              )
            }
          }

          // Camera Presets + Inspector Toggle Bar
          CameraAndInspectorBar(
            inspectorOpen = config.inspectorOpen,
            telemetry = telemetry,
            onCameraView = onCameraView,
            onToggleInspector = onToggleInspector,
            modifier = Modifier.padding(horizontal = 12.dp),
          )

          // Scene Preset Carousel
          ScenePresetCarousel(
            selectedScene = config.scene,
            onSelectScene = onSelectScene,
          )
        }
      }
    }
  }
}

@Composable
private fun TopTelemetryBar(
  config: SceneConfig,
  telemetry: EngineTelemetry,
  onToggleAutoRotate: () -> Unit,
  onResetCamera: () -> Unit,
  onToggleHud: () -> Unit,
  onRetryEngine: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.fillMaxWidth().widthIn(max = 840.dp),
    shape = RoundedCornerShape(20.dp),
    color = StudioSurface.copy(alpha = 0.78f),
    border = BorderStroke(1.dp, StudioBorder),
  ) {
    Row(
      modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      // Brand + Engine Status + Telemetry
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier =
            Modifier.size(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.linearGradient(listOf(CyanElectric.copy(alpha = 0.35f), AmberPrism.copy(alpha = 0.25f)))
              ),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.Hexagon,
            contentDescription = null,
            tint = CyanElectric,
            modifier = Modifier.size(20.dp),
          )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
              text = "PRISM 3D",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
            )
            // Engine Indicator Badge
            val statusColor = if (telemetry.isFallbackMode) AmberPrism else EmeraldTelemetry
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier =
                Modifier.clip(RoundedCornerShape(6.dp))
                  .background(statusColor.copy(alpha = 0.14f))
                  .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
              Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
              Text(
                text = telemetry.engineName,
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = "${telemetry.fps} FPS  •  ${telemetry.triangles} TRIS  •  ${telemetry.azimuthDeg}°/${telemetry.elevationDeg}°",
              style = MonoTelemetryStyle,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            if (telemetry.isFallbackMode) {
              TextButton(
                onClick = onRetryEngine,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                modifier = Modifier.testTag("retry_threejs_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Refresh,
                  contentDescription = stringResource(R.string.engine_retry),
                  modifier = Modifier.size(13.dp),
                  tint = AmberPrism,
                )
                Spacer(Modifier.width(3.dp))
                Text("Retry", style = MaterialTheme.typography.labelSmall, color = AmberPrism)
              }
            }
          }
        }
      }

      // Quick Viewport Action Icons (48dp touch targets)
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onToggleAutoRotate,
          modifier = Modifier.size(48.dp).testTag("toggle_autorotate_button"),
        ) {
          Icon(
            imageVector = if (config.autoRotate) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = stringResource(R.string.cd_toggle_autoplay),
            tint = if (config.autoRotate) CyanElectric else TextSecondary,
          )
        }
        IconButton(
          onClick = onResetCamera,
          modifier = Modifier.size(48.dp).testTag("reset_camera_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.CenterFocusStrong,
            contentDescription = stringResource(R.string.cd_reset_camera),
            tint = TextPrimary,
          )
        }
        IconButton(
          onClick = onToggleHud,
          modifier = Modifier.size(48.dp).testTag("toggle_hud_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.VisibilityOff,
            contentDescription = stringResource(R.string.cd_toggle_hud),
            tint = TextSecondary,
          )
        }
      }
    }
  }
}

@Composable
private fun CameraAndInspectorBar(
  inspectorOpen: Boolean,
  telemetry: EngineTelemetry,
  onCameraView: (CameraView) -> Unit,
  onToggleInspector: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    // Camera Angles Pill
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = StudioSurface.copy(alpha = 0.78f),
      border = BorderStroke(1.dp, StudioBorder),
      modifier = Modifier.weight(1f, fill = false),
    ) {
      Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        CameraView.entries.forEach { view ->
          Box(
            modifier =
              Modifier.heightIn(min = 38.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onCameraView(view) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("camera_view_${view.id}"),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = view.label,
              style = MaterialTheme.typography.labelMedium,
              color = TextPrimary,
            )
          }
        }
        Text(
          text = "${telemetry.distance}m",
          style = MonoTelemetryStyle,
          color = TextSecondary,
          modifier = Modifier.padding(horizontal = 8.dp),
        )
      }
    }

    Spacer(Modifier.width(8.dp))

    // Inspector Toggle Pill
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = if (inspectorOpen) CyanElectric else StudioSurface.copy(alpha = 0.85f),
      border = BorderStroke(1.dp, if (inspectorOpen) CyanElectric else StudioBorder),
      modifier =
        Modifier.clip(RoundedCornerShape(16.dp))
          .clickable(onClick = onToggleInspector)
          .testTag("open_inspector_button"),
    ) {
      Row(
        modifier = Modifier.heightIn(min = 48.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.Tune,
          contentDescription = stringResource(R.string.cd_open_inspector),
          tint = if (inspectorOpen) Color(0xFF042233) else CyanElectric,
          modifier = Modifier.size(18.dp),
        )
        Text(
          text = if (inspectorOpen) "Done" else "Studio",
          style = MaterialTheme.typography.labelLarge,
          color = if (inspectorOpen) Color(0xFF042233) else TextPrimary,
        )
      }
    }
  }
}

@Composable
private fun ScenePresetCarousel(
  selectedScene: ScenePreset,
  onSelectScene: (ScenePreset) -> Unit,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth().testTag("scene_preset_carousel"),
  ) {
    items(ScenePreset.entries, key = { it.id }) { preset ->
      val selected = preset == selectedScene
      Surface(
        modifier =
          Modifier.clip(RoundedCornerShape(18.dp))
            .clickable { onSelectScene(preset) }
            .testTag("scene_preset_${preset.id}"),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) StudioSurfaceElevated.copy(alpha = 0.94f) else StudioSurface.copy(alpha = 0.78f),
        border = BorderStroke(1.2.dp, if (selected) CyanElectric else StudioBorder),
      ) {
        Row(
          modifier = Modifier.heightIn(min = 52.dp).padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Icon(
            imageVector = preset.icon(),
            contentDescription = null,
            tint = if (selected) CyanElectric else TextSecondary,
            modifier = Modifier.size(20.dp),
          )
          Column {
            Text(
              text = preset.label,
              style = MaterialTheme.typography.labelLarge,
              color = if (selected) TextPrimary else TextSecondary,
            )
            Text(
              text = preset.subtitle,
              style = MaterialTheme.typography.labelSmall,
              color = if (selected) CyanElectric.copy(alpha = 0.9f) else TextSecondary.copy(alpha = 0.7f),
            )
          }
        }
      }
    }
  }
}

private fun ScenePreset.icon(): ImageVector =
  when (this) {
    ScenePreset.TORUS_KNOT -> Icons.Rounded.AutoAwesome
    ScenePreset.GEODESIC_CORE -> Icons.Rounded.Hexagon
    ScenePreset.TERRAIN_WAVE -> Icons.Rounded.Waves
    ScenePreset.SOLAR_ORRERY -> Icons.Rounded.Public
    ScenePreset.MORPH_SPHERE -> Icons.Rounded.Grain
  }
