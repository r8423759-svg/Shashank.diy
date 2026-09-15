package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChatMessageEntity
import com.example.ui.theme.BoltAmber
import com.example.ui.theme.BoltCyanLight
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
import com.example.ui.theme.BoltVioletLight

@Composable
fun ChatAssistantView(
    messages: List<ChatMessageEntity>,
    isGenerating: Boolean,
    currentStepText: String?,
    currentThinkingText: String?,
    enableHighThinking: Boolean,
    selectedModel: String,
    onHighThinkingToggle: (Boolean) -> Unit,
    onModelSelect: (String) -> Unit,
    onSendPrompt: (String) -> Unit,
    onNavigateToCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }
    var showModelMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val quickPromptsScroll = rememberScrollState()

    // Auto-scroll on new message
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "Add dark / light theme switcher",
        "Build simulated backend REST API routes",
        "Add search and filter bar",
        "Add Web Audio synthesizer effects",
        "Make layout fully responsive for mobile"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoltDarkBg)
    ) {
        // Model & Thinking Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkSurface)
                .border(1.dp, BoltDarkBorder)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Model Selector Dropdown
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BoltDarkElevated)
                        .clickable { showModelMenu = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Model",
                        tint = BoltElectricViolet,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedModel == "gemini-3.1-pro-preview") "Gemini 3.1 Pro (Thinking)" else "Gemini 3.5 Flash",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BoltTextPrimary
                    )
                }

                DropdownMenu(
                    expanded = showModelMenu,
                    onDismissRequest = { showModelMenu = false },
                    modifier = Modifier.background(BoltDarkElevated)
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Gemini 3.1 Pro Preview", fontWeight = FontWeight.Bold, color = BoltTextPrimary)
                                Text("High-Thinking Autonomous Fullstack Agent", fontSize = 10.sp, color = BoltTextSecondary)
                            }
                        },
                        onClick = {
                            onModelSelect("gemini-3.1-pro-preview")
                            showModelMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Gemini 3.5 Flash", fontWeight = FontWeight.Bold, color = BoltTextPrimary)
                                Text("Rapid Code Generation & Q&A", fontSize = 10.sp, color = BoltTextSecondary)
                            }
                        },
                        onClick = {
                            onModelSelect("gemini-3.5-flash")
                            showModelMenu = false
                        }
                    )
                }
            }

            // Thinking Mode Switch
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Thinking",
                    tint = if (enableHighThinking) BoltVioletLight else BoltTextMuted,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "High Thinking",
                    fontSize = 11.sp,
                    color = if (enableHighThinking) BoltVioletLight else BoltTextMuted
                )
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = enableHighThinking,
                    onCheckedChange = onHighThinkingToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BoltElectricViolet,
                        uncheckedThumbColor = BoltTextMuted,
                        uncheckedTrackColor = BoltDarkElevated
                    ),
                    modifier = Modifier.size(24.dp).testTag("switch_high_thinking")
                )
            }
        }

        // Messages Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    onNavigateToCode = onNavigateToCode
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Live Generation / Thinking Progress Indicator
            if (isGenerating) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BoltDarkElevated.copy(alpha = 0.8f))
                            .border(1.dp, BoltElectricViolet.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = BoltElectricViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = currentStepText ?: "⚡ Bolt AI is reasoning...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BoltTextPrimary
                            )
                        }

                        if (!currentThinkingText.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            ThinkingCard(
                                thoughtText = currentThinkingText,
                                isOngoing = true
                            )
                        }
                    }
                }
            }
        }

        // Quick Suggestion Prompts Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkSurface)
                .horizontalScroll(quickPromptsScroll)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            quickPrompts.forEach { qp ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(BoltDarkElevated)
                        .border(1.dp, BoltDarkBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .clickable {
                            promptInput = qp
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("quick_prompt_$qp")
                ) {
                    Text(
                        text = qp,
                        fontSize = 11.sp,
                        color = BoltCyanLight
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
        }

        // Bottom Prompt Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkSurface)
                .border(1.dp, BoltDarkBorder)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = {
                    Text(
                        text = "Ask Bolt to create or modify anything...",
                        fontSize = 12.sp,
                        color = BoltTextMuted
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BoltElectricViolet,
                    unfocusedBorderColor = BoltDarkBorder,
                    focusedContainerColor = BoltDarkBg,
                    unfocusedContainerColor = BoltDarkBg,
                    focusedTextColor = BoltTextPrimary,
                    unfocusedTextColor = BoltTextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_prompt_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (promptInput.isNotBlank() && !isGenerating) {
                        val p = promptInput.trim()
                        promptInput = ""
                        onSendPrompt(p)
                    }
                },
                enabled = promptInput.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (promptInput.isNotBlank() && !isGenerating)
                            Brush.linearGradient(listOf(BoltElectricViolet, BoltElectricCyan))
                        else
                            Brush.linearGradient(listOf(BoltDarkElevated, BoltDarkElevated))
                    )
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (promptInput.isNotBlank() && !isGenerating) Color.White else BoltTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessageEntity,
    onNavigateToCode: () -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Sender header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 3.dp)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(BoltElectricViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Bolt AI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoltVioletLight
                )
            } else {
                Text(
                    text = "You",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoltCyanLight
                )
            }
        }

        // Message bubble
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .background(if (isUser) BoltElectricViolet.copy(alpha = 0.25f) else BoltDarkElevated)
                .border(
                    width = 1.dp,
                    color = if (isUser) BoltElectricViolet.copy(alpha = 0.4f) else BoltDarkBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                // If message has a thinking trace, show the ThinkingCard
                if (!message.thoughtProcess.isNullOrBlank()) {
                    ThinkingCard(
                        thoughtText = message.thoughtProcess,
                        durationSeconds = message.thinkingDurationSec,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Main Message Text
                Text(
                    text = message.content,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = BoltTextPrimary
                )

                // Artifact Card if action files were applied
                if (message.hasArtifact) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BoltDarkBg)
                            .border(1.dp, BoltEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onNavigateToCode() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Applied",
                                tint = BoltEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Files mounted into virtual container",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BoltEmerald
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "View Code",
                                fontSize = 11.sp,
                                color = BoltCyanLight
                            )
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Code",
                                tint = BoltCyanLight,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
