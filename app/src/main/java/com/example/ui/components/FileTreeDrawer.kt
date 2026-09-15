package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.local.entity.ProjectFileEntity
import com.example.ui.theme.BoltAmber
import com.example.ui.theme.BoltCyanLight
import com.example.ui.theme.BoltDarkBg
import com.example.ui.theme.BoltDarkBorder
import com.example.ui.theme.BoltDarkElevated
import com.example.ui.theme.BoltDarkSurface
import com.example.ui.theme.BoltElectricCyan
import com.example.ui.theme.BoltElectricViolet
import com.example.ui.theme.BoltEmerald
import com.example.ui.theme.BoltRose
import com.example.ui.theme.BoltTextMuted
import com.example.ui.theme.BoltTextPrimary
import com.example.ui.theme.BoltTextSecondary

@Composable
fun FileTreeDrawer(
    files: List<ProjectFileEntity>,
    activeFilePath: String,
    onSelectFile: (String) -> Unit,
    onAddNewFile: () -> Unit,
    onDeleteFile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredFiles = remember(files, searchQuery) {
        if (searchQuery.isBlank()) files
        else files.filter { it.filePath.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(BoltDarkSurface)
            .border(1.dp, BoltDarkBorder)
            .padding(8.dp)
    ) {
        // Explorer Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = "Files",
                    tint = BoltCyanLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "EXPLORER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoltTextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(${files.size})",
                    fontSize = 10.sp,
                    color = BoltTextMuted
                )
            }

            IconButton(
                onClick = onAddNewFile,
                modifier = Modifier.size(26.dp).testTag("btn_add_file")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add File",
                    tint = BoltCyanLight,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter files...", fontSize = 11.sp, color = BoltTextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = BoltTextMuted,
                    modifier = Modifier.size(14.dp)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BoltElectricViolet,
                unfocusedBorderColor = BoltDarkBorder,
                focusedContainerColor = BoltDarkBg,
                unfocusedContainerColor = BoltDarkBg,
                focusedTextColor = BoltTextPrimary,
                unfocusedTextColor = BoltTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(vertical = 2.dp),
            shape = RoundedCornerShape(6.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Files List
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(filteredFiles, key = { it.filePath }) { file ->
                val isSelected = file.filePath == activeFilePath

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) BoltDarkElevated else Color.Transparent)
                        .clickable { onSelectFile(file.filePath) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("file_item_${file.filePath}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        FileExtensionBadge(filePath = file.filePath)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = file.filePath,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BoltTextPrimary else BoltTextSecondary,
                            maxLines = 1
                        )
                    }

                    // Delete button (visible on selected or hover)
                    if (isSelected && files.size > 1) {
                        IconButton(
                            onClick = { onDeleteFile(file.filePath) },
                            modifier = Modifier.size(22.dp).testTag("delete_file_${file.filePath}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete File",
                                tint = BoltRose.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileExtensionBadge(filePath: String) {
    val (label, color) = when {
        filePath.endsWith(".jsx", ignoreCase = true) || filePath.endsWith(".tsx", ignoreCase = true) ->
            "JSX" to BoltCyanLight
        filePath.endsWith(".js", ignoreCase = true) || filePath.endsWith(".ts", ignoreCase = true) ->
            "JS" to BoltAmber
        filePath.endsWith(".html", ignoreCase = true) ->
            "HTML" to Color(0xFFFB923C)
        filePath.endsWith(".css", ignoreCase = true) ->
            "CSS" to Color(0xFF38BDF8)
        filePath.endsWith(".json", ignoreCase = true) ->
            "JSON" to Color(0xFFA3E635)
        filePath.endsWith(".md", ignoreCase = true) ->
            "MD" to Color(0xFFC084FC)
        else ->
            "FILE" to BoltTextMuted
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
