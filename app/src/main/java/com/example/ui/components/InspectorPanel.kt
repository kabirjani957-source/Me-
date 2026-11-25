package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LightingPreset
import com.example.model.MaterialMode
import com.example.model.SceneConfig
import com.example.model.StudioColorSwatches
import com.example.ui.theme.CyanElectric
import com.example.ui.theme.MonoTelemetryStyle
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun InspectorPanel(
  config: SceneConfig,
  onSelectMaterial: (MaterialMode) -> Unit,
  onSelectLighting: (LightingPreset) -> Unit,
  onSelectColor: (String) -> Unit,
  onMetalnessChange: (Float) -> Unit,
  onRoughnessChange: (Float) -> Unit,
  onDistortionChange: (Float) -> Unit,
  onAnimSpeedChange: (Float) -> Unit,
  onToggleGrid: () -> Unit,
  onToggleParticles: () -> Unit,
  onResetAll: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.testTag("studio_inspector_panel"),
    shape = RoundedCornerShape(22.dp),
    color = StudioSurface.copy(alpha = 0.88f),
    border = BorderStroke(1.dp, StudioBorder),
    tonalElevation = 8.dp,
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "SHADER & SCENE STUDIO",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
          )
          Text(
            text = "Real-time WebGL uniforms & PBR properties",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
          )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          TextButton(
            onClick = onResetAll,
            modifier = Modifier.testTag("inspector_reset_all_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Refresh,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text("Reset")
          }
          IconButton(
            onClick = onClose,
            modifier = Modifier.size(48.dp).testTag("close_inspector_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Close,
              contentDescription = stringResource(R.string.cd_close_inspector),
              tint = TextSecondary,
            )
          }
        }
      }

      HorizontalDivider(color = StudioBorder.copy(alpha = 0.6f))

      // Material Mode
      SectionLabel("RENDER MATERIAL")
      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        MaterialMode.entries.forEach { mode ->
          val selected = config.materialMode == mode
          FilterChip(
            selected = selected,
            onClick = { onSelectMaterial(mode) },
            label = { Text(mode.label, style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.testTag("material_chip_${mode.id}"),
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyanElectric.copy(alpha = 0.22f),
                selectedLabelColor = CyanElectric,
                containerColor = StudioSurfaceElevated,
                labelColor = TextSecondary,
              ),
            border =
              FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = StudioBorder,
                selectedBorderColor = CyanElectric,
              ),
          )
        }
      }

      // Color Swatches
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        SectionLabel("SURFACE ALBEDO")
        Text(
          text = config.primaryColorHex,
          style = MonoTelemetryStyle,
          color = CyanElectric,
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        StudioColorSwatches.forEach { swatch ->
          val isSelected = config.primaryColorHex.equals(swatch.hex, ignoreCase = true)
          Box(
            modifier =
              Modifier.size(48.dp)
                .clip(CircleShape)
                .clickable { onSelectColor(swatch.hex) }
                .semantics { contentDescription = "Select ${swatch.name} color" }
                .testTag("color_swatch_${swatch.name.lowercase()}"),
            contentAlignment = Alignment.Center,
          ) {
            Box(
              modifier =
                Modifier.size(if (isSelected) 32.dp else 26.dp)
                  .clip(CircleShape)
                  .background(Color(swatch.argb))
                  .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) Color.White else StudioBorder,
                    shape = CircleShape,
                  )
            )
          }
        }
      }

      // Lighting Presets
      SectionLabel("LIGHTING RIG")
      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        LightingPreset.entries.forEach { preset ->
          val selected = config.lightingPreset == preset
          FilterChip(
            selected = selected,
            onClick = { onSelectLighting(preset) },
            label = { Text(preset.label, style = MaterialTheme.typography.labelMedium) },
            leadingIcon = {
              Box(
                modifier =
                  Modifier.size(8.dp).clip(CircleShape).background(Color(preset.accentHex))
              )
            },
            modifier = Modifier.testTag("lighting_chip_${preset.id}"),
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(preset.accentHex).copy(alpha = 0.2f),
                selectedLabelColor = TextPrimary,
                containerColor = StudioSurfaceElevated,
                labelColor = TextSecondary,
              ),
            border =
              FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = StudioBorder,
                selectedBorderColor = Color(preset.accentHex),
              ),
          )
        }
      }

      // Sliders
      AnimatedVisibility(visible = config.materialMode == MaterialMode.PHYSICAL) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          StudioSlider(
            label = "Metalness",
            valueText = "${(config.metalness * 100).roundToInt()}%",
            value = config.metalness,
            valueRange = 0f..1f,
            onValueChange = onMetalnessChange,
            testTag = "slider_metalness",
          )
          StudioSlider(
            label = "Roughness",
            valueText = "${(config.roughness * 100).roundToInt()}%",
            value = config.roughness,
            valueRange = 0.04f..1f,
            onValueChange = onRoughnessChange,
            testTag = "slider_roughness",
          )
        }
      }

      StudioSlider(
        label = "Harmonic Distortion",
        valueText = "${(config.distortion * 100).roundToInt()}%",
        value = config.distortion,
        valueRange = 0f..1f,
        onValueChange = onDistortionChange,
        testTag = "slider_distortion",
      )

      StudioSlider(
        label = "Motion Speed",
        valueText = String.format("%.1fx", config.animSpeed),
        value = config.animSpeed,
        valueRange = 0f..2.5f,
        onValueChange = onAnimSpeedChange,
        testTag = "slider_speed",
      )

      HorizontalDivider(color = StudioBorder.copy(alpha = 0.6f))

      // Environment Switches
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        EnvironmentToggle(
          label = "Floor Grid",
          checked = config.showGrid,
          onToggle = onToggleGrid,
          testTag = "switch_grid",
          modifier = Modifier.weight(1f),
        )
        EnvironmentToggle(
          label = "Particles",
          checked = config.showParticles,
          onToggle = onToggleParticles,
          testTag = "switch_particles",
          modifier = Modifier.weight(1f),
        )
      }
      Spacer(Modifier.height(2.dp))
    }
  }
}

@Composable
private fun SectionLabel(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelSmall,
    color = TextSecondary,
  )
}

@Composable
private fun StudioSlider(
  label: String,
  valueText: String,
  value: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  onValueChange: (Float) -> Unit,
  testTag: String,
) {
  Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
      Text(text = valueText, style = MonoTelemetryStyle, color = CyanElectric)
    }
    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = valueRange,
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      colors =
        SliderDefaults.colors(
          thumbColor = CyanElectric,
          activeTrackColor = CyanElectric,
          inactiveTrackColor = StudioBorder,
        ),
    )
  }
}

@Composable
private fun EnvironmentToggle(
  label: String,
  checked: Boolean,
  onToggle: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.clip(RoundedCornerShape(14.dp)).clickable(onClick = onToggle).testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    color = StudioSurfaceElevated,
    border = BorderStroke(1.dp, if (checked) CyanElectric.copy(alpha = 0.5f) else StudioBorder),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
      Switch(
        checked = checked,
        onCheckedChange = { onToggle() },
        colors =
          SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = CyanElectric,
            uncheckedTrackColor = StudioSurface,
          ),
      )
    }
  }
}
