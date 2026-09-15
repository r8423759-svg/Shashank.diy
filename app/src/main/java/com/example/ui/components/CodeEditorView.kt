package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BoltAmber
import com.example.ui.theme.BoltCodeBg
import com.example.ui.theme.BoltDarkBg
import com.example.ui.theme.BoltDarkBorder
import com.example.ui.theme.BoltDarkElevated
import com.example.ui.theme.BoltDarkSurface
import com.example.ui.theme.BoltElectricCyan
import com.example.ui.theme.BoltElectricViolet
import com.example.ui.theme.BoltEmerald
import com.example.ui.theme.BoltTextMuted
import com.example.ui.theme.BoltTextPrimary
import com.example.ui.theme.BoltTextSecondary

@Composable
fun CodeEditorView(
    activeFilePath: String,
    openTabs: List<String>,
    content: String,
    hasUnsavedChanges: Boolean,
    onContentChange: (String) -> Unit,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onSaveFile: () -> Unit,
    onAskBoltAboutFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val tabsScrollState = rememberScrollState()

    val lineCount = remember(content) {
        content.lines().size.coerceAtLeast(1)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoltCodeBg)
    ) {
        // Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkSurface)
                .border(1.dp, BoltDarkBorder)
                .horizontalScroll(tabsScrollState)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            openTabs.forEach { tabPath ->
                val isSelected = tabPath == activeFilePath
                val fileName = tabPath.substringAfterLast("/")

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(if (isSelected) BoltCodeBg else BoltDarkSurface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) BoltDarkBorder else Color.Transparent,
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                        )
                        .clickable { onSelectTab(tabPath) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("tab_item_$fileName"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FileExtensionBadge(filePath = tabPath)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = fileName,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) BoltTextPrimary else BoltTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )

                    // Dirty indicator
                    if (isSelected && hasUnsavedChanges) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BoltAmber)
                        )
                    }

                    if (openTabs.size > 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Tab",
                            tint = BoltTextMuted,
                            modifier = Modifier
                                .size(12.dp)
                                .clickable { onCloseTab(tabPath) }
                        )
                    }
                }
            }
        }

        // Action Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkElevated.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Path and line info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = activeFilePath,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = BoltElectricCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$lineCount lines • ${content.length} chars",
                    fontSize = 10.sp,
                    color = BoltTextMuted
                )
            }

            // Right side buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Copy button
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(content))
                        Toast.makeText(context, "Copied $activeFilePath", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp).testTag("btn_copy_code")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = BoltTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Save button
                Button(
                    onClick = onSaveFile,
                    enabled = hasUnsavedChanges,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasUnsavedChanges) BoltEmerald else BoltDarkBorder,
                        contentColor = if (hasUnsavedChanges) Color.White else BoltTextMuted
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp).testTag("btn_save_file")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasUnsavedChanges) "Save" else "Saved",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Main Editor Text Area with Line Numbers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(BoltCodeBg)
        ) {
            // Line numbers column
            val lineNumbersString = remember(lineCount) {
                (1..lineCount).joinToString("\n")
            }

            Box(
                modifier = Modifier
                    .width(42.dp)
                    .fillMaxHeight()
                    .background(BoltDarkBg)
                    .border(1.dp, BoltDarkBorder.copy(alpha = 0.3f))
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = lineNumbersString,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 18.sp,
                    color = BoltTextMuted.copy(alpha = 0.6f)
                )
            }

            // Editable code input field
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("code_editor_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = BoltCodeBg,
                    unfocusedContainerColor = BoltCodeBg,
                    focusedTextColor = BoltTextPrimary,
                    unfocusedTextColor = BoltTextPrimary
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = BoltTextPrimary
                )
            )
        }
    }
}
