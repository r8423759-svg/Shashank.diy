package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.local.entity.ProjectEntity
import com.example.ui.IdeViewMode
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

@Composable
fun BoltHeaderBar(
    currentProject: ProjectEntity?,
    currentViewMode: IdeViewMode,
    isGenerating: Boolean,
    enableHighThinking: Boolean,
    onViewModeChange: (IdeViewMode) -> Unit,
    onOpenProjectPicker: () -> Unit,
    onOpenNewProjectDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BoltDarkSurface)
    ) {
        // Top row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bolt Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onOpenProjectPicker() }
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(BoltElectricViolet, BoltElectricCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Bolt",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = BoltTextPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = ".diy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = BoltCyanLight,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Project name pill
                    Text(
                        text = currentProject?.name ?: "No Project",
                        fontSize = 11.sp,
                        color = BoltTextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Right side status & action buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // High thinking badge
                if (enableHighThinking) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BoltElectricViolet.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "High Thinking",
                                tint = BoltElectricViolet,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "THINKING HIGH",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BoltElectricViolet
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Port 5173 status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BoltDarkElevated)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BoltEmerald)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "5173",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BoltEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Projects List button
                IconButton(
                    onClick = onOpenProjectPicker,
                    modifier = Modifier.size(34.dp).testTag("header_projects_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Projects",
                        tint = BoltTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // New Project button
                IconButton(
                    onClick = onOpenNewProjectDialog,
                    modifier = Modifier.size(34.dp).testTag("header_new_project_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Project",
                        tint = BoltCyanLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // View Mode Navigation Tabs
        val modes = listOf(
            IdeViewMode.CHAT to ("AI Chat" to Icons.Default.SmartToy),
            IdeViewMode.CODE to ("Code" to Icons.Default.Code),
            IdeViewMode.PREVIEW to ("Preview" to Icons.Default.Language),
            IdeViewMode.TERMINAL to ("Terminal" to Icons.Default.Terminal)
        )

        val selectedIndex = modes.indexOfFirst { it.first == currentViewMode }.coerceAtLeast(0)

        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = BoltDarkSurface,
            contentColor = BoltElectricViolet,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = BoltElectricViolet,
                        height = 2.dp
                    )
                }
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BoltDarkBorder)
                )
            }
        ) {
            modes.forEachIndexed { index, (mode, data) ->
                val (label, icon) = data
                val isSelected = currentViewMode == mode

                Tab(
                    selected = isSelected,
                    onClick = { onViewModeChange(mode) },
                    modifier = Modifier.testTag("tab_${mode.name.lowercase()}"),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(15.dp),
                                tint = if (isSelected) BoltElectricViolet else BoltTextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BoltTextPrimary else BoltTextSecondary
                            )
                        }
                    }
                )
            }
        }
    }
}
