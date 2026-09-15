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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TerminalEntryEntity
import com.example.ui.theme.BoltAmber
import com.example.ui.theme.BoltCyanLight
import com.example.ui.theme.BoltDarkBorder
import com.example.ui.theme.BoltDarkElevated
import com.example.ui.theme.BoltDarkSurface
import com.example.ui.theme.BoltElectricCyan
import com.example.ui.theme.BoltEmerald
import com.example.ui.theme.BoltRose
import com.example.ui.theme.BoltTerminalBg
import com.example.ui.theme.BoltTextMuted
import com.example.ui.theme.BoltTextPrimary
import com.example.ui.theme.BoltTextSecondary

@Composable
fun TerminalView(
    entries: List<TerminalEntryEntity>,
    onExecuteCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var commandInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val quickScrollState = rememberScrollState()

    // Auto-scroll to bottom on new entry
    LaunchedEffect(entries.size) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(entries.size - 1)
        }
    }

    val quickCommands = listOf(
        "npm run dev",
        "npm install lucide-react",
        "node api/server.js",
        "ls -la",
        "git status",
        "clear",
        "help"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BoltTerminalBg)
    ) {
        // Terminal Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkSurface)
                .border(1.dp, BoltDarkBorder)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Window dots
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(BoltRose))
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(BoltAmber))
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(BoltEmerald))
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "bolt@container:~/app (bash)",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = BoltTextSecondary
                )
            }

            // Port and status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BoltEmerald)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Port 5173 Listening",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = BoltEmerald
                )
            }
        }

        // Quick Command Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkElevated.copy(alpha = 0.4f))
                .horizontalScroll(quickScrollState)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            quickCommands.forEach { cmd ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BoltDarkElevated)
                        .border(1.dp, BoltDarkBorder.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .clickable { onExecuteCommand(cmd) }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("quick_cmd_$cmd")
                ) {
                    Text(
                        text = cmd,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = BoltCyanLight
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
        }

        // Terminal Log Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(entries, key = { it.id }) { entry ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Prompt + Command line
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "bolt@container:~/app$ ",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BoltEmerald
                        )
                        Text(
                            text = entry.command,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BoltTextPrimary
                        )
                    }

                    // Command Output
                    if (entry.output.isNotBlank()) {
                        val outputColor = when (entry.status) {
                            "error" -> BoltRose
                            else -> BoltTextSecondary
                        }
                        Text(
                            text = entry.output,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = outputColor,
                            modifier = Modifier.padding(start = 6.dp, top = 2.dp)
                        )
                    }
                }
            }
        }

        // Terminal Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BoltDarkSurface)
                .border(1.dp, BoltDarkBorder)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$ ",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = BoltEmerald
            )

            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                placeholder = {
                    Text(
                        text = "Type command (e.g. npm run dev, ls, help)...",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = BoltTextMuted
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = BoltTextPrimary,
                    unfocusedTextColor = BoltTextPrimary
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = BoltTextPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_input")
            )

            IconButton(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        onExecuteCommand(commandInput)
                        commandInput = ""
                    }
                },
                modifier = Modifier.size(32.dp).testTag("terminal_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Run",
                    tint = BoltCyanLight,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
