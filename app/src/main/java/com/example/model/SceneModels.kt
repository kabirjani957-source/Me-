package com.example.model

enum class ScenePreset(val id: String, val label: String, val subtitle: String) {
  TORUS_KNOT("torus_knot", "Torus Knot", "Twisted manifold"),
  GEODESIC_CORE("geodesic_core", "Geodesic", "Crystal orbital core"),
  TERRAIN_WAVE("terrain_wave", "Harmonic", "3D wave surface"),
  SOLAR_ORRERY("solar_orrery", "Orrery", "Planetary system"),
  MORPH_SPHERE("morph_sphere", "Plasma Orb", "Noise sculpture"),
}

enum class MaterialMode(val id: String, val label: String) {
  PHYSICAL("physical", "PBR Metal"),
  HOLOGRAM("hologram", "Hologram"),
  WIREFRAME("wireframe", "Wireframe"),
  CLAY("clay", "Studio Clay"),
}

enum class LightingPreset(val id: String, val label: String, val accentHex: Long) {
  CYBER("cyber", "Cyber", 0xFF38BDF8),
  SOLAR("solar", "Sunset", 0xFFF59E0B),
  ARCTIC("arctic", "Arctic", 0xFF7DD3FC),
  VOID("void", "Void", 0xFFA78BFA),
}

enum class CameraView(val id: String, val label: String) {
  PERSPECTIVE("perspective", "3D"),
  FRONT("front", "Front"),
  TOP("top", "Top"),
  ISO("iso", "Iso"),
}

data class ColorSwatch(val name: String, val hex: String, val argb: Long)

val StudioColorSwatches =
  listOf(
    ColorSwatch("Cyan", "#38BDF8", 0xFF38BDF8),
    ColorSwatch("Amber", "#F59E0B", 0xFFF59E0B),
    ColorSwatch("Violet", "#A78BFA", 0xFFA78BFA),
    ColorSwatch("Emerald", "#34D399", 0xFF34D399),
    ColorSwatch("Coral", "#FB7185", 0xFFFB7185),
    ColorSwatch("Titanium", "#E2E8F0", 0xFFE2E8F0),
  )

data class SceneConfig(
  val scene: ScenePreset = ScenePreset.TORUS_KNOT,
  val materialMode: MaterialMode = MaterialMode.PHYSICAL,
  val lightingPreset: LightingPreset = LightingPreset.CYBER,
  val primaryColorHex: String = "#38BDF8",
  val metalness: Float = 0.72f,
  val roughness: Float = 0.18f,
  val distortion: Float = 0.35f,
  val animSpeed: Float = 1.0f,
  val autoRotate: Boolean = true,
  val showGrid: Boolean = true,
  val showParticles: Boolean = true,
  val showHud: Boolean = true,
  val inspectorOpen: Boolean = false,
)

data class EngineTelemetry(
  val isReady: Boolean = false,
  val engineName: String = "Initializing…",
  val isFallbackMode: Boolean = false,
  val fps: Int = 0,
  val triangles: Int = 0,
  val vertices: Int = 0,
  val azimuthDeg: Int = 35,
  val elevationDeg: Int = 24,
  val distance: Float = 5.4f,
  val errorMessage: String? = null,
)

sealed interface ViewportCommand {
  data class SetCamera(val view: CameraView) : ViewportCommand

  data object ResetScene : ViewportCommand

  data object ReloadEngine : ViewportCommand
}
