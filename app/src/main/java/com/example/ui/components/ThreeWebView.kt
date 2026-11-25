package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.model.SceneConfig
import com.example.model.ViewportCommand
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONObject

private class ThreeJsBridge(
  private val onReady: (String, Boolean) -> Unit,
  private val onError: (String) -> Unit,
  private val onTelemetry: (Int, Int, Int, Int, Int, Float) -> Unit,
) {
  private val mainHandler = Handler(Looper.getMainLooper())

  @JavascriptInterface
  fun onEngineReady(engineName: String, isFallback: Boolean) {
    mainHandler.post { onReady(engineName, isFallback) }
  }

  @JavascriptInterface
  fun onEngineError(message: String) {
    mainHandler.post { onError(message) }
  }

  @JavascriptInterface
  fun onTelemetry(jsonString: String) {
    try {
      val obj = JSONObject(jsonString)
      val fps = obj.optInt("fps", 0)
      val triangles = obj.optInt("triangles", 0)
      val vertices = obj.optInt("vertices", 0)
      val azimuth = obj.optInt("azimuthDeg", 0)
      val elevation = obj.optInt("elevationDeg", 0)
      val distance = obj.optDouble("distance", 5.4).toFloat()
      mainHandler.post { onTelemetry(fps, triangles, vertices, azimuth, elevation, distance) }
    } catch (_: Exception) {
      // Ignore malformed telemetry frame
    }
  }
}

private fun SceneConfig.toJsonString(): String =
  JSONObject()
    .apply {
      put("scene", scene.id)
      put("materialMode", materialMode.id)
      put("lightingPreset", lightingPreset.id)
      put("primaryColorHex", primaryColorHex)
      put("metalness", metalness.toDouble())
      put("roughness", roughness.toDouble())
      put("distortion", distortion.toDouble())
      put("animSpeed", animSpeed.toDouble())
      put("autoRotate", autoRotate)
      put("showGrid", showGrid)
      put("showParticles", showParticles)
    }
    .toString()

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ThreeWebView(
  config: SceneConfig,
  commands: SharedFlow<ViewportCommand>,
  onEngineReady: (String, Boolean) -> Unit,
  onEngineError: (String) -> Unit,
  onTelemetry: (Int, Int, Int, Int, Int, Float) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val currentConfig by rememberUpdatedState(config)
  var pageLoaded by remember { mutableStateOf(false) }

  val htmlContent = remember {
    context.assets.open("three_scene.html").bufferedReader().use { it.readText() }
  }

  val webView = remember {
    WebView(context).apply {
      setLayerType(View.LAYER_TYPE_HARDWARE, null)
      setBackgroundColor(AndroidColor.parseColor("#07090F"))
      isFocusable = true
      isFocusableInTouchMode = true
      overScrollMode = View.OVER_SCROLL_NEVER

      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        allowFileAccess = false
        allowContentAccess = false
        mediaPlaybackRequiresUserGesture = false
        cacheMode = WebSettings.LOAD_DEFAULT
      }

      webChromeClient = WebChromeClient()
      addJavascriptInterface(
        ThreeJsBridge(
          onReady = onEngineReady,
          onError = onEngineError,
          onTelemetry = onTelemetry,
        ),
        "AndroidBridge",
      )

      webViewClient =
        object : WebViewClient() {
          override fun onPageFinished(view: WebView?, url: String?) {
            pageLoaded = true
            view?.evaluateJavascript(
              "window.applyConfig(${currentConfig.toJsonString()});",
              null,
            )
          }
        }

      loadDataWithBaseURL("https://prism3d.app/", htmlContent, "text/html", "UTF-8", null)
    }
  }

  LaunchedEffect(config, pageLoaded) {
    if (pageLoaded) {
      webView.evaluateJavascript("window.applyConfig(${config.toJsonString()});", null)
    }
  }

  LaunchedEffect(commands, pageLoaded) {
    commands.collect { command ->
      when (command) {
        is ViewportCommand.SetCamera -> {
          if (pageLoaded) {
            webView.evaluateJavascript("window.setCameraView('${command.view.id}');", null)
          }
        }
        ViewportCommand.ResetScene -> {
          if (pageLoaded) {
            webView.evaluateJavascript(
              "window.resetCamera(); window.applyConfig(${currentConfig.toJsonString()});",
              null,
            )
          }
        }
        ViewportCommand.ReloadEngine -> {
          pageLoaded = false
          webView.loadDataWithBaseURL("https://prism3d.app/", htmlContent, "text/html", "UTF-8", null)
        }
      }
    }
  }

  DisposableEffect(lifecycleOwner, webView) {
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_RESUME -> webView.onResume()
        Lifecycle.Event.ON_PAUSE -> webView.onPause()
        else -> Unit
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
      webView.removeJavascriptInterface("AndroidBridge")
      webView.destroy()
    }
  }

  AndroidView(
    factory = { webView },
    modifier = modifier.testTag("three_webgl_viewport"),
  )
}
