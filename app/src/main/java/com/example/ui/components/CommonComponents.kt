package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KgTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    unreadNotifCount: Int = 0,
    onNotifClick: () -> Unit = {},
    isAdmin: Boolean = false,
    onAdminToggleClick: () -> Unit = {}
) {
    Surface(
        color = DeepNavy,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .testTag("btn_back")
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PureWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        // Monogram Logo Badge
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(ElectricBlue, SkyBlue)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "KG",
                                fontWeight = FontWeight.Black,
                                color = PureWhite,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isAdmin) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AccentPurple,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        color = PureWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                color = SkyBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Notification Bell with dynamic badge
                    IconButton(
                        onClick = onNotifClick,
                        modifier = Modifier
                            .testTag("btn_topbar_notif")
                            .size(42.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(
                                        containerColor = ElectricBlue,
                                        contentColor = PureWhite
                                    ) {
                                        Text(text = if (unreadNotifCount > 9) "9+" else unreadNotifCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = PureWhite
                            )
                        }
                    }

                    // Admin / Customer portal switch button
                    IconButton(
                        onClick = onAdminToggleClick,
                        modifier = Modifier
                            .testTag("btn_toggle_admin_mode")
                            .size(42.dp)
                    ) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Default.Person else Icons.Default.Shield,
                            contentDescription = if (isAdmin) "Switch to Customer Portal" else "Switch to Admin Portal",
                            tint = if (isAdmin) SkyBlue else AccentPurple
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "PENDING" -> Pair(StatusPending.copy(alpha = 0.15f), StatusPending)
        "CONFIRMED" -> Pair(StatusConfirmed.copy(alpha = 0.15f), StatusConfirmed)
        "IN_PROGRESS" -> Pair(StatusInProgress.copy(alpha = 0.15f), StatusInProgress)
        "REVIEW" -> Pair(StatusReview.copy(alpha = 0.15f), StatusReview)
        "COMPLETED" -> Pair(StatusCompleted.copy(alpha = 0.15f), StatusCompleted)
        "CANCELLED" -> Pair(StatusCancelled.copy(alpha = 0.15f), StatusCancelled)
        "PAID" -> Pair(StatusCompleted.copy(alpha = 0.15f), StatusCompleted)
        else -> Pair(SubtitleText.copy(alpha = 0.15f), SubtitleText)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = status.replace("_", " "),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    subText: String? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = SubtitleText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            if (subText != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subText,
                    fontSize = 11.sp,
                    color = StatusCompleted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun OrderTimelineView(currentStatus: String) {
    val steps = listOf(
        "PENDING" to "Order Placed",
        "CONFIRMED" to "Confirmed",
        "IN_PROGRESS" to "Work Started",
        "REVIEW" to "In Review",
        "COMPLETED" to "Completed"
    )

    val currentIndex = when (currentStatus.uppercase()) {
        "PENDING" -> 0
        "CONFIRMED" -> 1
        "IN_PROGRESS" -> 2
        "REVIEW" -> 3
        "COMPLETED" -> 4
        "CANCELLED" -> -1
        else -> 0
    }

    if (currentStatus.uppercase() == "CANCELLED") {
        Surface(
            color = StatusCancelled.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null, tint = StatusCancelled)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Order Cancelled", fontWeight = FontWeight.Bold, color = StatusCancelled)
                    Text("This order has been cancelled and closed.", fontSize = 12.sp, color = SubtitleText)
                }
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, (key, label) ->
            val isDone = index <= currentIndex
            val isCurrent = index == currentIndex

            Row(verticalAlignment = Alignment.Top) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDone) ElectricBlue else BorderGray
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SubtitleText)
                            )
                        }
                    }

                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(26.dp)
                                .background(if (index < currentIndex) ElectricBlue else BorderGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Text(
                        text = label,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isDone) DarkText else SubtitleText,
                        fontSize = 14.sp
                    )
                    if (isCurrent) {
                        Text(
                            text = "Current State",
                            fontSize = 11.sp,
                            color = ElectricBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
