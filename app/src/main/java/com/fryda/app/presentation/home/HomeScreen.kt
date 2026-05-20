package com.fryda.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fryda.app.core.root.RootChecker
import com.fryda.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToLogs: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Header
            Column {
                Text(
                    text = "Fryda",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Frida Server Manager",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Root Status Section
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is HomeUiState.Success -> {
                    RootStatusCard(status = state.rootStatus)
                }
            }

            // Active Servers Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("ACTIVE SERVERS")
                ActiveServerCard()
            }

            // Overview Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("OVERVIEW")
                OverviewCard()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RootStatusCard(status: RootChecker.RootStatus) {
    val isDark = isSystemInDarkTheme()

    val bgColor: Color
    val iconTint: Color
    val title: String
    val textColor: Color

    if (status.isRooted) {
        bgColor = if (isDark) RootedGreenDark else RootedGreenLight
        iconTint = if (isDark) AccentTealDark else AccentTealLight
        title = "Device Rooted"
        textColor = if (isDark) Color.White else Color.Black
    } else {
        bgColor = if (isDark) NotRootedBackgroundDark else NotRootedBackgroundLight
        iconTint = if (isDark) NotRootedIconDark else NotRootedIconLight
        title = "No Root Access"
        textColor = if (isDark) Color.White else Color.Black
    }

    val chipBg = if (status.isRooted) {
        if (isDark) DarkGreenChip else LightGreenChip
    } else {
        if (isDark) RedChipDark else RedChipLight
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(iconTint.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = if (status.isRooted) "frida-server can be started" else "Superuser access required",
                        fontSize = 14.sp,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }

            HorizontalDivider(color = textColor.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    label = "MAGISK",
                    // Fix: Added ?.substringBefore(":") here
                    value = status.magiskVersion?.substringBefore(":") ?: "N/A",
                    bgColor = chipBg,
                    valueColor = iconTint,
                    modifier = Modifier.weight(1f)
                )
                InfoChip(
                    label = "SU",
                    value = status.suBinaryPath ?: "N/A",
                    bgColor = chipBg,
                    valueColor = iconTint,
                    modifier = Modifier.weight(1.3f)
                )
                InfoChip(
                    label = "ARCH",
                    value = status.architecture.substringBefore("-"),
                    bgColor = chipBg,
                    valueColor = iconTint,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InfoChip(
    label: String,
    value: String,
    bgColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (isSystemInDarkTheme()) TextGrayDark else TextGrayLight
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1, // Forces single line
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActiveServerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "frida-server 16.3.3", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                Text(text = "Port 27042 • PID 4821", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text(text = "Running", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OverviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            OverviewItem(
                icon = Icons.Rounded.Info,
                iconBgColor = Color(0xFF1E3A5F),
                iconColor = Color(0xFF64B5F6),
                title = "Installed Versions",
                value = "3"
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            OverviewItem(
                icon = Icons.Outlined.PlayArrow,
                iconBgColor = Color(0xFF1E4620),
                iconColor = MaterialTheme.colorScheme.primary,
                title = "Running Servers",
                value = "1"
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            OverviewItem(
                icon = Icons.Rounded.Info,
                iconBgColor = Color(0xFF3F1D53),
                iconColor = Color(0xFFBA68C8),
                title = "Device ABI",
                value = "arm64-v8a",
                showArrow = false
            )
        }
    }
}

@Composable
private fun OverviewItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    value: String,
    showArrow: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBgColor, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
        if (showArrow) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}