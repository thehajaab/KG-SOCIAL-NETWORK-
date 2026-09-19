package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceEntity
import com.example.ui.components.KgTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.KgViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun HomeScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val services by viewModel.publishedServices.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val testimonials by viewModel.testimonials.collectAsState()
    val currentCustomer by viewModel.currentCustomer.collectAsState()
    val unreadNotifCount by viewModel.unreadCustomerNotifCount.collectAsState()

    Scaffold(
        topBar = {
            KgTopBar(
                title = "KG SOCIAL NETWORK",
                subtitle = "WE CAN DO EVERYTHING",
                unreadNotifCount = unreadNotifCount,
                onNotifClick = {
                    viewModel.navigateTo(ScreenDestination.CustomerNotifications)
                },
                isAdmin = false,
                onAdminToggleClick = {
                    viewModel.navigateTo(ScreenDestination.AdminDashboard)
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(innerPadding)
        ) {
            // 1. Hero Banner
            item {
                HeroSection(
                    onExploreClick = { viewModel.navigateTo(ScreenDestination.Services) },
                    onLearnClick = { viewModel.navigateTo(ScreenDestination.Courses) },
                    onContactClick = { viewModel.navigateTo(ScreenDestination.ContactSupport) }
                )
            }

            // 2. Founder & Business Info Card
            item {
                FounderCard(
                    onWhatsAppClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/919622102381?text=Hello%20Jahangir%20Sir%2C%20I%20am%20interested%20in%20KG%20Social%20Network%20services."))
                        context.startActivity(intent)
                    },
                    onEmailClick = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:KACHERGROUPC@GMAIL.COM"))
                        context.startActivity(intent)
                    }
                )
            }

            // 3. Categories Horizontal Scroll / Grid
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Core Digital Services",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DarkText
                        )
                        TextButton(
                            onClick = { viewModel.navigateTo(ScreenDestination.Services) },
                            modifier = Modifier.testTag("btn_view_all_services")
                        ) {
                            Text("View All (12)", color = ElectricBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    CategoryQuickGrid(onCategoryClick = {
                        viewModel.navigateTo(ScreenDestination.Services)
                    })
                }
            }

            // 4. Featured Services
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Popular Solutions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkText
                    )
                    Text(
                        text = "High-demand digital services trusted by creators and businesses",
                        fontSize = 12.sp,
                        color = SubtitleText
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(services.take(4)) { service ->
                FeaturedServiceCard(
                    service = service,
                    onClick = {
                        viewModel.navigateTo(ScreenDestination.ServiceDetail(service.id))
                    },
                    onBookNow = {
                        viewModel.navigateTo(ScreenDestination.BookService(service.id))
                    }
                )
            }

            // 5. Online Courses Teaser
            item {
                CoursesTeaserSection(
                    coursesCount = courses.size,
                    onExploreCourses = { viewModel.navigateTo(ScreenDestination.Courses) }
                )
            }

            // 6. Testimonials
            if (testimonials.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Client Testimonials",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(end = 16.dp)
                        ) {
                            items(testimonials) { t ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = PureWhite,
                                    border = BorderStroke(1.dp, BorderGray),
                                    modifier = Modifier.width(280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(ElectricBlue.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = t.customerName.take(1),
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElectricBlue
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(t.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(t.serviceName, fontSize = 11.sp, color = SubtitleText)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "\"${t.review}\"",
                                            fontSize = 13.sp,
                                            color = DarkText,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. Footer / Legal / Brand Statement
            item {
                BrandFooter(
                    onTermsClick = {},
                    onPrivacyClick = {}
                )
            }
        }
    }
}

@Composable
private fun HeroSection(
    onExploreClick: () -> Unit,
    onLearnClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DeepNavy,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Surface(
                color = ElectricBlue.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, SkyBlue.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "DIGITAL SERVICES • MARKETING • LEARNING • TECHNOLOGY",
                    color = SkyBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "WELCOME TO\nKG SOCIAL NETWORK",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "WE CAN DO EVERYTHING",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SkyBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "KG Social Network provides professional digital services, marketing solutions, online courses, creative services and digital solutions for individuals, creators and businesses.",
                fontSize = 13.sp,
                color = PureWhite.copy(alpha = 0.85f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onExploreClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_hero_explore")
                ) {
                    Text("Explore Services", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onContactClick,
                    border = BorderStroke(1.5.dp, SkyBlue),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_hero_contact")
                ) {
                    Text("Contact Us", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun FounderCard(
    onWhatsAppClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = PureWhite,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(DeepNavy, ElectricBlue)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JK",
                        fontWeight = FontWeight.Black,
                        color = PureWhite,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Jahangir Ahmed Kacher",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Founder",
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Founder & Owner • KG Social Network",
                        fontSize = 12.sp,
                        color = SubtitleText
                    )
                    Text(
                        text = "Jammu & Kashmir, India",
                        fontSize = 11.sp,
                        color = ElectricBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderGray)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onWhatsAppClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_founder_whatsapp")
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onEmailClick,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ElectricBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_founder_email")
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Email Us", fontSize = 12.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CategoryQuickGrid(onCategoryClick: (String) -> Unit) {
    val items = listOf(
        "Social Media" to Icons.Default.Share,
        "Digital Mktg" to Icons.Default.Campaign,
        "SEO Services" to Icons.Default.Search,
        "Web Dev" to Icons.Default.Code,
        "Graphic Design" to Icons.Default.Brush,
        "Video Editing" to Icons.Default.Movie,
        "Content" to Icons.Default.Edit,
        "WordPress" to Icons.Default.Web
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (i in 0 until items.size step 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (j in 0 until 4) {
                    if (i + j < items.size) {
                        val (title, icon) = items[i + j]
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PureWhite,
                            border = BorderStroke(1.dp, BorderGray),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onCategoryClick(title) }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedServiceCard(
    service: ServiceEntity,
    onClick: () -> Unit,
    onBookNow: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderGray),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = SkyBlue,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = service.description,
                    fontSize = 12.sp,
                    color = SubtitleText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Starts at ₹${service.basePrice.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${service.deliveryTime}",
                        fontSize = 11.sp,
                        color = SubtitleText
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onBookNow,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_book_${service.id}")
            ) {
                Text("Book", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CoursesTeaserSection(
    coursesCount: Int,
    onExploreCourses: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        color = DeepNavyDarker,
        border = BorderStroke(1.dp, DeepNavyLight)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KG Learning Academy",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Learn in-demand digital & freelancing skills",
                        color = SkyBlue,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    color = AccentPurple,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$coursesCount Courses",
                        color = PureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Taught directly by Jahangir Ahmed Kacher. From full-stack web engineering to agency client acquisition blueprints.",
                color = PureWhite.copy(alpha = 0.8f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onExploreCourses,
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_explore_courses")
            ) {
                Text("Start Learning Today", color = DeepNavyDarker, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BrandFooter(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "KG SOCIAL NETWORK",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = DarkText
        )
        Text(
            text = "WE CAN DO EVERYTHING",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = ElectricBlue
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Digital Services • Marketing • Learning • Technology",
            fontSize = 11.sp,
            color = SubtitleText
        )
        Text(
            text = "Jammu & Kashmir, India • KACHERGROUPC@GMAIL.COM",
            fontSize = 10.sp,
            color = SubtitleText
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "© 2026 KG Social Network. All rights reserved.",
            fontSize = 11.sp,
            color = SubtitleText
        )
    }
}
