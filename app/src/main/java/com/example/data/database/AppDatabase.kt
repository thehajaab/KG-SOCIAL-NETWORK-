package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [
        UserEntity::class,
        AdminEntity::class,
        ServiceEntity::class,
        ServicePackageEntity::class,
        OrderEntity::class,
        OrderActivityEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        CourseEntity::class,
        CourseEnrollmentEntity::class,
        ProductEntity::class,
        ProductPurchaseEntity::class,
        PortfolioEntity::class,
        BlogEntity::class,
        TestimonialEntity::class,
        ContactRequestEntity::class,
        PlatformSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun adminDao(): AdminDao
    abstract fun serviceDao(): ServiceDao
    abstract fun orderDao(): OrderDao
    abstract fun orderActivityDao(): OrderActivityDao
    abstract fun messageDao(): MessageDao
    abstract fun notificationDao(): NotificationDao
    abstract fun courseDao(): CourseDao
    abstract fun productDao(): ProductDao
    abstract fun contentDao(): ContentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kg_social_network.db"
                ).addCallback(DatabaseCallback())
                 .fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    seedProductionData(database)
                }
            }
        }
    }
}

suspend fun seedProductionData(database: AppDatabase) {
    val adminDao = database.adminDao()
    val userDao = database.userDao()
    val serviceDao = database.serviceDao()
    val courseDao = database.courseDao()
    val productDao = database.productDao()
    val contentDao = database.contentDao()

    // 1. Seed Super Admin: Jahangir Ahmed Kacher
    adminDao.insertAdmin(
        AdminEntity(
            adminUsername = "admin",
            fullName = "Jahangir Ahmed Kacher",
            email = "KACHERGROUPC@GMAIL.COM",
            passwordHash = AppDatabase.hashPassword("admin123"),
            role = "SUPER_ADMIN",
            permissions = "ALL",
            status = "ACTIVE"
        )
    )

    // 2. Seed Default Verified Customer
    userDao.insertUser(
        UserEntity(
            fullName = "Ayaan Malik",
            email = "customer@kgsocialnetwork.com",
            phone = "9876543210",
            passwordHash = AppDatabase.hashPassword("customer123"),
            address = "Residency Road",
            city = "Srinagar",
            state = "Jammu & Kashmir",
            country = "India"
        )
    )

    // 3. Seed 12 Core Services & Packages
    val services = listOf(
        ServiceEntity(
            slug = "social-media-marketing",
            title = "Social Media Marketing",
            description = "High-impact social media management, organic reach amplification, brand viral reels, and targeted ad campaigns.",
            category = "Social Media Marketing",
            thumbnailIcon = "Share",
            features = "Strategy formulation\nAudience targeting\nHigh-converting Ad creatives\nAnalytics & reporting",
            basePrice = 4999.0,
            deliveryTime = "7 Days",
            requirementsPrompt = "Provide your business links, target audience, and specific promotion goals.",
            isPopular = true
        ),
        ServiceEntity(
            slug = "digital-marketing",
            title = "Digital Marketing",
            description = "360-degree digital transformation for your brand, omni-channel campaigns, lead funnels, and ROI acceleration.",
            category = "Digital Marketing",
            thumbnailIcon = "Campaign",
            features = "PPC Google Ads\nConversion rate optimization\nFunnel building\nMarket research",
            basePrice = 7999.0,
            deliveryTime = "10 Days",
            requirementsPrompt = "Detail your business model, current monthly revenue, and monthly ad budget.",
            isPopular = true
        ),
        ServiceEntity(
            slug = "seo-services",
            title = "SEO Services",
            description = "Dominate Google search results with technical SEO audits, keyword research, backlink building, and on-page optimization.",
            category = "SEO Services",
            thumbnailIcon = "Search",
            features = "Technical SEO audit\nKeyword density optimization\nHigh DA/PA backlinks\nCore Web Vitals tuning",
            basePrice = 5999.0,
            deliveryTime = "14 Days",
            requirementsPrompt = "Provide your website URL and top 5 competitor websites.",
            isPopular = true
        ),
        ServiceEntity(
            slug = "website-development",
            title = "Website Development",
            description = "Modern, responsive, ultra-fast websites, e-commerce stores, and custom web applications built with modern frameworks.",
            category = "Website Development",
            thumbnailIcon = "Code",
            features = "Mobile-first responsive UI\nFast loading speeds\nCustom admin panel\nPayment gateway integration",
            basePrice = 11999.0,
            deliveryTime = "14 Days",
            requirementsPrompt = "Describe the website purpose, required pages, reference links, and branding preferences.",
            isPopular = true
        ),
        ServiceEntity(
            slug = "graphic-design",
            title = "Graphic Design",
            description = "Creative branding, modern logos, vector graphics, marketing banners, and visual identities that capture attention.",
            category = "Graphic Design",
            thumbnailIcon = "Brush",
            features = "Vector source files\nUnlimited revisions\nHigh-res print ready\nSocial media kit",
            basePrice = 2999.0,
            deliveryTime = "3 Days",
            requirementsPrompt = "Share brand colors, text to include, and design samples you admire.",
            isPopular = false
        ),
        ServiceEntity(
            slug = "video-editing",
            title = "Video Editing",
            description = "Cinematic video editing, dynamic Instagram Reels, TikTok/Shorts pacing, color grading, sound design, and motion graphics.",
            category = "Video Editing",
            thumbnailIcon = "Movie",
            features = "4K rendering\nSound effects & licensed music\nKinetic captions & motion graphics\nFast turnaround",
            basePrice = 3499.0,
            deliveryTime = "4 Days",
            requirementsPrompt = "Provide raw footage links (Google Drive/Dropbox) and pacing instructions.",
            isPopular = true
        ),
        ServiceEntity(
            slug = "content-creation",
            title = "Content Creation",
            description = "Engaging copywriting, viral social media hooks, blog articles, video scripts, and sales email sequences.",
            category = "Content Creation",
            thumbnailIcon = "Edit",
            features = "SEO-optimized articles\nAudience hook techniques\nPlagiarism-free guarantees\nBrand tone alignment",
            basePrice = 2499.0,
            deliveryTime = "3 Days",
            requirementsPrompt = "Specify your target topic, tone of voice (e.g. professional vs casual), and word count.",
            isPopular = false
        ),
        ServiceEntity(
            slug = "blogger-services",
            title = "Blogger Services",
            description = "Specialized Google Blogger setup, custom XML theme styling, AdSense compliance fix, and speed optimization.",
            category = "Blogger Services",
            thumbnailIcon = "Article",
            features = "Custom Blogger XML design\nAdSense policy audit\nClean mobile AMP layout\nMeta tags & canonical setup",
            basePrice = 1999.0,
            deliveryTime = "3 Days",
            requirementsPrompt = "Share your Blogspot address and list of issues or features needed.",
            isPopular = false
        ),
        ServiceEntity(
            slug = "wordpress-services",
            title = "WordPress Services",
            description = "Custom WordPress themes, WooCommerce stores, speed optimization, security hardening, and plugin customization.",
            category = "WordPress Services",
            thumbnailIcon = "Web",
            features = "WooCommerce checkout setup\nTheme styling & bug fixing\nMalware cleanup & firewall\n0.8s load speed caching",
            basePrice = 4499.0,
            deliveryTime = "5 Days",
            requirementsPrompt = "Provide WordPress admin credentials or staging site access.",
            isPopular = false
        ),
        ServiceEntity(
            slug = "youtube-services",
            title = "YouTube Services",
            description = "Channel growth audits, high-CTR thumbnail design, video SEO tags, title optimization, and subscriber scaling.",
            category = "YouTube Services",
            thumbnailIcon = "PlayArrow",
            features = "High CTR thumbnail pack\nKeyword ranking tags & descriptions\nChannel art overhaul\nMonetization roadmap",
            basePrice = 3999.0,
            deliveryTime = "5 Days",
            requirementsPrompt = "Provide your YouTube channel link and 3 recent upload video links.",
            isPopular = true
        ),
        ServiceEntity(
            slug = "freelancing-services",
            title = "Freelancing Services",
            description = "Upwork/Fiverr profile optimization, high-converting proposal frameworks, client acquisition funnels, and contract negotiation.",
            category = "Freelancing Services",
            thumbnailIcon = "Work",
            features = "Profile rewrite for 10x views\nCold outreach templates\nPortfolio curation\n1-on-1 strategy briefing",
            basePrice = 2999.0,
            deliveryTime = "3 Days",
            requirementsPrompt = "Share your current freelancing profile links and skill stack.",
            isPopular = false
        ),
        ServiceEntity(
            slug = "business-digital-solutions",
            title = "Business Digital Solutions",
            description = "Enterprise digital operations, CRM integrations, billing systems, automated WhatsApp workflows, and cloud databases.",
            category = "Business Digital Solutions",
            thumbnailIcon = "Business",
            features = "WhatsApp API automation\nCustom CRM setup\nCloud backup infrastructure\nTeam training & documentation",
            basePrice = 14999.0,
            deliveryTime = "15 Days",
            requirementsPrompt = "Detail your existing business process, team size, and manual bottlenecks.",
            isPopular = true
        )
    )

    for (s in services) {
        val sId = serviceDao.insertService(s)
        serviceDao.insertPackages(
            listOf(
                ServicePackageEntity(
                    serviceId = sId,
                    name = "Basic Starter",
                    price = s.basePrice,
                    deliveryDays = 3,
                    revisions = "2 Revisions",
                    description = "Essential entry package for quick delivery and foundational results.",
                    features = "Core deliverables\nStandard support\nSource files included"
                ),
                ServicePackageEntity(
                    serviceId = sId,
                    name = "Standard Pro",
                    price = s.basePrice * 1.75,
                    deliveryDays = 7,
                    revisions = "5 Revisions",
                    description = "Most popular choice with advanced features, prioritized execution, and extended scope.",
                    features = "All Starter features\nExtended scope & strategy\nPriority execution\nDedicated manager"
                ),
                ServicePackageEntity(
                    serviceId = sId,
                    name = "Enterprise Premium",
                    price = s.basePrice * 3.0,
                    deliveryDays = 14,
                    revisions = "Unlimited Revisions",
                    description = "Comprehensive enterprise-grade solution with 30-day post-delivery support and full customization.",
                    features = "Complete omni-solution\n30-day post-delivery support\nUnlimited revisions\nVIP 24/7 hotline"
                )
            )
        )
    }

    // 4. Seed Courses
    courseDao.insertCourse(
        CourseEntity(
            title = "Full-Stack Web Development Bootcamp",
            slug = "full-stack-web-bootcamp",
            description = "Master frontend, backend, PostgreSQL, and Android mobile development from scratch with Jahangir Ahmed Kacher.",
            instructor = "Jahangir Ahmed Kacher",
            category = "Technology",
            duration = "45 Hours",
            price = 4999.0,
            lessonsCount = 68
        )
    )
    courseDao.insertCourse(
        CourseEntity(
            title = "Digital Marketing & Social Media Dominance",
            slug = "digital-marketing-dominance",
            description = "Learn modern paid advertising, viral content algorithms, SEO, and sales funnels to scale any business.",
            instructor = "Jahangir Ahmed Kacher",
            category = "Marketing",
            duration = "30 Hours",
            price = 3499.0,
            lessonsCount = 42
        )
    )
    courseDao.insertCourse(
        CourseEntity(
            title = "Freelancing & Agency Blueprint 2026",
            slug = "freelancing-agency-blueprint",
            description = "Step-by-step framework to land high-paying global clients, write winning proposals, and build your digital agency.",
            instructor = "Jahangir Ahmed Kacher",
            category = "Freelancing",
            duration = "22 Hours",
            price = 2999.0,
            lessonsCount = 35
        )
    )

    // 5. Seed Products
    productDao.insertProduct(
        ProductEntity(
            title = "KG Premium Social Media Design Kit",
            description = "Over 500+ customizable Canva & Photoshop templates for Instagram, LinkedIn, and Facebook marketing.",
            category = "Design Assets",
            price = 999.0,
            fileName = "KG-Social-Design-Kit-v2026.zip"
        )
    )
    productDao.insertProduct(
        ProductEntity(
            title = "High-Converting Proposal Mastery Pack",
            description = "Battle-tested Upwork & email outreach templates that have generated over $50,000 in freelance contracts.",
            category = "Templates",
            price = 799.0,
            fileName = "KG-Proposal-Templates-Mastery.pdf"
        )
    )
    productDao.insertProduct(
        ProductEntity(
            title = "Ultimate SEO & Speed Optimization Blueprint",
            description = "Actionable checklist, schema templates, and robots.txt configurations used by KG Social Network for rank 1 dominance.",
            category = "Guides",
            price = 599.0,
            fileName = "KG-SEO-Rank1-Blueprint.pdf"
        )
    )

    // 6. Seed Portfolio
    contentDao.insertPortfolio(
        PortfolioEntity(
            title = "Kashmir Crafts E-Commerce Portal",
            category = "Website Development",
            description = "Full-stack e-commerce marketplace featuring artisanal handicrafts with international multi-currency checkout.",
            client = "Kashmir Handicrafts Guild"
        )
    )
    contentDao.insertPortfolio(
        PortfolioEntity(
            title = "Himalayan Tourism Lead Gen Campaign",
            category = "Digital Marketing",
            description = "Targeted digital advertising generating 4,200 qualified luxury tour bookings across North India.",
            client = "Himalayan Expeditions"
        )
    )
    contentDao.insertPortfolio(
        PortfolioEntity(
            title = "TechVibe Media Branding & Motion",
            category = "Graphic Design & Video",
            description = "Complete visual identity, 3D logo animation, and high-CTR YouTube packaging for a fast-growing tech channel.",
            client = "TechVibe Studios"
        )
    )

    // 7. Seed Testimonials
    contentDao.insertTestimonial(
        TestimonialEntity(
            customerName = "Tariq Ahmad",
            serviceName = "Website Development & SEO",
            rating = 5,
            review = "KG Social Network delivered our platform in record time. Our search traffic grew 300% in 90 days. Jahangir and his team are world-class professionals!"
        )
    )
    contentDao.insertTestimonial(
        TestimonialEntity(
            customerName = "Simran Kaur",
            serviceName = "Social Media Marketing",
            rating = 5,
            review = "The most reliable digital agency we have worked with. Their creative reels and targeted ads scaled our brand across India."
        )
    )
    contentDao.insertTestimonial(
        TestimonialEntity(
            customerName = "Bilal Dar",
            serviceName = "Freelancing Mastery Course",
            rating = 5,
            review = "Thanks to Jahangir Sir's training, I closed my first $1,500 international web project within three weeks of enrolling!"
        )
    )

    // 8. Seed Platform Settings
    contentDao.saveSettings(
        PlatformSettingsEntity(
            id = 1,
            websiteName = "KG SOCIAL NETWORK",
            tagline = "WE CAN DO EVERYTHING",
            subline = "Digital Services • Marketing • Learning • Technology",
            founder = "Jahangir Ahmed Kacher",
            email = "KACHERGROUPC@GMAIL.COM",
            phone = "9622102381",
            whatsapp = "9622102381",
            location = "Jammu & Kashmir, India",
            websiteUrl = "https://kgsocialnetwork.com",
            upiId = "9622102381@upi"
        )
    )
}
