package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.local.entity.ProjectFileEntity
import com.example.ui.PreviewDeviceMode
import com.example.ui.theme.BoltAmber
import com.example.ui.theme.BoltCodeBg
import com.example.ui.theme.BoltCyanLight
import com.example.ui.theme.BoltDarkBg
import com.example.ui.theme.BoltDarkBorder
import com.example.ui.theme.BoltDarkElevated
import com.example.ui.theme.BoltDarkSurface
import com.example.ui.theme.BoltElectricCyan
import com.example.ui.theme.BoltEmerald
import com.example.ui.theme.BoltRose
import com.example.ui.theme.BoltTextMuted
import com.example.ui.theme.BoltTextPrimary
import com.example.ui.theme.BoltTextSecondary

class WebConsoleBridge(private val onLog: (String) -> Unit) {
    @JavascriptInterface
    fun log(message: String) {
        onLog("[Console Log] $message")
    }

    @JavascriptInterface
    fun error(message: String) {
        onLog("[Console Error] $message")
    }

    @JavascriptInterface
    fun warn(message: String) {
        onLog("[Console Warn] $message")
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LivePreviewView(
    files: List<ProjectFileEntity>,
    reloadKey: Int,
    deviceMode: PreviewDeviceMode,
    consoleLogs: List<String>,
    onDeviceModeChange: (PreviewDeviceMode) -> Unit,
    onReload: () -> Unit,
    onLogReceived: (String) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConsoleDrawer by remember { mutableStateOf(false) }

    // Assemble the complete HTML bundle from virtual files
    val bundledHtml = remember(files, reloadKey) {
        buildPreviewHtml(files)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoltDarkBg)
    ) {
        // Browser URL & Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkSurface)
                .border(1.dp, BoltDarkBorder)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Address Pill
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BoltDarkBg)
                    .border(1.dp, BoltDarkBorder.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BoltEmerald)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "http://localhost:5173/",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = BoltCyanLight
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Device switcher buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onDeviceModeChange(PreviewDeviceMode.MOBILE) },
                    modifier = Modifier.size(28.dp).testTag("btn_preview_mobile")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Mobile View",
                        tint = if (deviceMode == PreviewDeviceMode.MOBILE) BoltCyanLight else BoltTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { onDeviceModeChange(PreviewDeviceMode.TABLET) },
                    modifier = Modifier.size(28.dp).testTag("btn_preview_tablet")
                ) {
                    Icon(
                        imageVector = Icons.Default.TabletAndroid,
                        contentDescription = "Tablet View",
                        tint = if (deviceMode == PreviewDeviceMode.TABLET) BoltCyanLight else BoltTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { onDeviceModeChange(PreviewDeviceMode.DESKTOP) },
                    modifier = Modifier.size(28.dp).testTag("btn_preview_desktop")
                ) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = "Desktop View",
                        tint = if (deviceMode == PreviewDeviceMode.DESKTOP) BoltCyanLight else BoltTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Refresh button
                IconButton(
                    onClick = onReload,
                    modifier = Modifier.size(28.dp).testTag("btn_preview_refresh")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = BoltTextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Console Drawer Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (showConsoleDrawer) BoltDarkElevated else Color.Transparent)
                        .clickable { showConsoleDrawer = !showConsoleDrawer }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("btn_toggle_console")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Console",
                            tint = if (consoleLogs.isNotEmpty()) BoltAmber else BoltTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        if (consoleLogs.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${consoleLogs.size}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = BoltAmber
                            )
                        }
                    }
                }
            }
        }

        // Webview Sandbox Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF020617)),
            contentAlignment = Alignment.TopCenter
        ) {
            val deviceModifier = when (deviceMode) {
                PreviewDeviceMode.MOBILE -> Modifier.width(375.dp).fillMaxHeight().border(1.dp, BoltDarkBorder)
                PreviewDeviceMode.TABLET -> Modifier.width(600.dp).fillMaxHeight().border(1.dp, BoltDarkBorder)
                PreviewDeviceMode.DESKTOP -> Modifier.fillMaxSize()
            }

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()

                        addJavascriptInterface(WebConsoleBridge { msg ->
                            post { onLogReceived(msg) }
                        }, "AndroidBridge")

                        loadDataWithBaseURL(
                            "http://localhost:5173/",
                            bundledHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL(
                        "http://localhost:5173/",
                        bundledHtml,
                        "text/html",
                        "UTF-8",
                        null
                    )
                },
                modifier = deviceModifier.testTag("live_preview_webview")
            )
        }

        // Collapsible In-Preview Console Drawer
        AnimatedVisibility(visible = showConsoleDrawer) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(BoltDarkSurface)
                    .border(1.dp, BoltDarkBorder)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CONSOLE OUTPUT",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = BoltTextSecondary
                    )
                    IconButton(
                        onClick = onClearLogs,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear",
                            tint = BoltTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                if (consoleLogs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No console events logged yet.",
                            fontSize = 11.sp,
                            color = BoltTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(consoleLogs) { log ->
                            val color = when {
                                log.contains("Error", ignoreCase = true) -> BoltRose
                                log.contains("Warn", ignoreCase = true) -> BoltAmber
                                else -> BoltCyanLight
                            }
                            Text(
                                text = log,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = color,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildPreviewHtml(files: List<ProjectFileEntity>): String {
    val indexHtmlFile = files.find { it.filePath == "index.html" || it.filePath == "public/index.html" }
    val appJsxFile = files.find { it.filePath == "src/App.jsx" || it.filePath == "src/App.tsx" || it.filePath == "App.jsx" }
    val stylesCssFile = files.find { it.filePath == "src/styles.css" || it.filePath == "styles.css" }

    val consoleScript = """
        <script>
            (function() {
                const originalLog = console.log;
                const originalError = console.error;
                const originalWarn = console.warn;
                console.log = function(...args) {
                    originalLog.apply(console, args);
                    if (window.AndroidBridge && window.AndroidBridge.log) {
                        window.AndroidBridge.log(args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' '));
                    }
                };
                console.error = function(...args) {
                    originalError.apply(console, args);
                    if (window.AndroidBridge && window.AndroidBridge.error) {
                        window.AndroidBridge.error(args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' '));
                    }
                };
                console.warn = function(...args) {
                    originalWarn.apply(console, args);
                    if (window.AndroidBridge && window.AndroidBridge.warn) {
                        window.AndroidBridge.warn(args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' '));
                    }
                };
            })();
        </script>
    """.trimIndent()

    if (indexHtmlFile != null) {
        var html = indexHtmlFile.content

        // Inject console bridge right after <head>
        if (html.contains("<head>", ignoreCase = true)) {
            html = html.replaceFirst("<head>", "<head>\n$consoleScript", ignoreCase = true)
        } else {
            html = "$consoleScript\n$html"
        }

        // If index.html references src/App.jsx via <script type="text/babel" src="src/App.jsx"></script>, replace it inline with actual App.jsx code!
        if (appJsxFile != null) {
            val scriptTagRegex = Regex("<script[^>]*src=[\"'](?:src\\/)?App\\.jsx[\"'][^>]*>\\s*<\\/script>", RegexOption.IGNORE_CASE)
            val inlineJsx = "<script type=\"text/babel\">\n${appJsxFile.content}\n</script>"
            html = if (scriptTagRegex.containsMatchIn(html)) {
                html.replace(scriptTagRegex, inlineJsx)
            } else if (!html.contains(appJsxFile.content)) {
                html.replace("</body>", "$inlineJsx\n</body>")
            } else {
                html
            }
        }

        // Inject CSS if available
        if (stylesCssFile != null && !html.contains(stylesCssFile.content)) {
            val styleTag = "<style>\n${stylesCssFile.content}\n</style>"
            html = html.replace("</head>", "$styleTag\n</head>")
        }

        return html
    }

    // Default Fallback HTML
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            $consoleScript
            <script src="https://cdn.tailwindcss.com"></script>
        </head>
        <body class="bg-slate-950 text-white p-6">
            <div class="max-w-md mx-auto text-center pt-10">
                <div class="text-4xl mb-4">⚡</div>
                <h1 class="text-xl font-bold">Bolt Sandbox</h1>
                <p class="text-xs text-slate-400 mt-2">No index.html found. Add an index.html or select a template to preview.</p>
            </div>
        </body>
        </html>
    """.trimIndent()
}
