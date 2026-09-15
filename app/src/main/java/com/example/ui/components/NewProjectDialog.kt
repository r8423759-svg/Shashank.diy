package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.ProjectEntity
import com.example.data.template.DefaultTemplates
import com.example.data.template.TemplateDefinition
import com.example.ui.theme.BoltCyanLight
import com.example.ui.theme.BoltDarkBg
import com.example.ui.theme.BoltDarkBorder
import com.example.ui.theme.BoltDarkElevated
import com.example.ui.theme.BoltDarkSurface
import com.example.ui.theme.BoltElectricCyan
import com.example.ui.theme.BoltElectricViolet
import com.example.ui.theme.BoltRose
import com.example.ui.theme.BoltTextMuted
import com.example.ui.theme.BoltTextPrimary
import com.example.ui.theme.BoltTextSecondary

@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreateProject: (String, TemplateDefinition) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf(DefaultTemplates.REACT_SAAS) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BoltDarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BoltDarkBorder),
            modifier = Modifier.fillMaxWidth().testTag("dialog_new_project")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Bolt.diy Project",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BoltTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = BoltTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Project Name Input
                Text(
                    text = "Project Name",
                    fontSize = 12.sp,
                    color = BoltTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    placeholder = { Text(selectedTemplate.name, fontSize = 12.sp, color = BoltTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BoltElectricViolet,
                        unfocusedBorderColor = BoltDarkBorder,
                        focusedContainerColor = BoltDarkBg,
                        unfocusedContainerColor = BoltDarkBg,
                        focusedTextColor = BoltTextPrimary,
                        unfocusedTextColor = BoltTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_new_project_name")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Template selection
                Text(
                    text = "Select Starter Template",
                    fontSize = 12.sp,
                    color = BoltTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                DefaultTemplates.ALL.forEach { tmpl ->
                    val isSelected = selectedTemplate.id == tmpl.id
                    val icon = when (tmpl.iconName) {
                        "rocket" -> Icons.Default.RocketLaunch
                        "view_kanban" -> Icons.Default.ViewKanban
                        else -> Icons.Default.SportsEsports
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BoltElectricViolet.copy(alpha = 0.15f) else BoltDarkBg)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) BoltElectricViolet else BoltDarkBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTemplate = tmpl }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) BoltElectricViolet else BoltDarkElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = tmpl.name,
                                tint = if (isSelected) Color.White else BoltCyanLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tmpl.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BoltTextPrimary
                            )
                            Text(
                                text = tmpl.description,
                                fontSize = 11.sp,
                                color = BoltTextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Create Button
                Button(
                    onClick = {
                        val finalName = projectName.ifBlank { selectedTemplate.name }
                        onCreateProject(finalName, selectedTemplate)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BoltElectricViolet,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("btn_confirm_create_project")
                ) {
                    Text("Scaffold & Launch Project", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProjectPickerDialog(
    projects: List<ProjectEntity>,
    currentProjectId: String?,
    onDismiss: () -> Unit,
    onSelectProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit,
    onNewProject: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BoltDarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BoltDarkBorder),
            modifier = Modifier.fillMaxWidth().testTag("dialog_project_picker")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Bolt Projects",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BoltTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = BoltTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                    items(projects, key = { it.id }) { proj ->
                        val isSelected = proj.id == currentProjectId

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BoltElectricViolet.copy(alpha = 0.15f) else BoltDarkBg)
                                .border(
                                    1.dp,
                                    if (isSelected) BoltElectricViolet else BoltDarkBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectProject(proj.id) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = "Project",
                                    tint = if (isSelected) BoltElectricViolet else BoltCyanLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = proj.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BoltTextPrimary
                                    )
                                    Text(
                                        text = proj.description,
                                        fontSize = 10.sp,
                                        color = BoltTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }

                            if (projects.size > 1) {
                                IconButton(
                                    onClick = { onDeleteProject(proj.id) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = BoltRose.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onNewProject()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BoltElectricCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text("+ Create Another Project", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CreateFileDialog(
    onDismiss: () -> Unit,
    onCreateFile: (String) -> Unit
) {
    var filePath by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = BoltDarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BoltDarkBorder),
            modifier = Modifier.fillMaxWidth().testTag("dialog_create_file")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Create New File",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoltTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = filePath,
                    onValueChange = { filePath = it },
                    placeholder = { Text("e.g. src/components/Card.jsx", fontSize = 12.sp, color = BoltTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BoltElectricViolet,
                        unfocusedBorderColor = BoltDarkBorder,
                        focusedContainerColor = BoltDarkBg,
                        unfocusedContainerColor = BoltDarkBg,
                        focusedTextColor = BoltTextPrimary,
                        unfocusedTextColor = BoltTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_new_file_path")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = BoltTextSecondary
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (filePath.isNotBlank()) {
                                onCreateFile(filePath.trim())
                            }
                        },
                        enabled = filePath.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BoltElectricViolet,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}
