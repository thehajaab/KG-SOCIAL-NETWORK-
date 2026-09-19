package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val profilePhoto: String = "",
    val address: String = "",
    val city: String = "Srinagar",
    val state: String = "Jammu & Kashmir",
    val country: String = "India",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)

@Entity(tableName = "admins")
data class AdminEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminUsername: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val role: String = "SUPER_ADMIN", // SUPER_ADMIN, ADMIN, STAFF
    val permissions: String = "ALL",
    val status: String = "ACTIVE",
    val lastLogin: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val slug: String,
    val title: String,
    val description: String,
    val category: String,
    val thumbnailIcon: String, // icon name or vector type
    val features: String, // comma or newline separated
    val basePrice: Double,
    val deliveryTime: String,
    val requirementsPrompt: String,
    val status: String = "PUBLISHED", // PUBLISHED, DRAFT
    val isPopular: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "service_packages")
data class ServicePackageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceId: Long,
    val name: String, // Basic, Standard, Premium
    val price: Double,
    val deliveryDays: Int,
    val revisions: String,
    val description: String,
    val features: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String, // e.g. KG-ORD-2026-1042
    val customerId: Long,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val serviceId: Long,
    val serviceTitle: String,
    val serviceCategory: String,
    val packageId: Long,
    val packageName: String,
    val requirements: String,
    val instructions: String,
    val attachedFiles: String = "", // JSON or comma separated filenames
    val completedFiles: String = "",
    val amount: Double,
    val paymentStatus: String = "PENDING", // PENDING, PAID, REFUNDED
    val paymentMethod: String = "UPI / Razorpay",
    val paymentTxnId: String = "",
    val orderStatus: String = "PENDING", // PENDING, CONFIRMED, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED
    val assignedStaff: String = "Unassigned",
    val dueDate: String = "5 Business Days",
    val internalNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_activity")
data class OrderActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val actorId: Long,
    val actorName: String,
    val actorType: String, // CUSTOMER, ADMIN, SYSTEM
    val action: String,
    val oldStatus: String,
    val newStatus: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val senderId: Long,
    val senderName: String,
    val senderType: String, // CUSTOMER, ADMIN
    val receiverId: Long = 0,
    val message: String,
    val attachments: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientType: String, // ADMIN, CUSTOMER
    val recipientId: Long = 0, // 0 for all admins
    val type: String, // NEW_ORDER, ORDER_CONFIRMED, ORDER_STARTED, ORDER_REVIEW, ORDER_COMPLETED, ORDER_CANCELLED, NEW_MESSAGE, PAYMENT_SUCCESS
    val title: String,
    val message: String,
    val relatedOrderId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val slug: String,
    val description: String,
    val instructor: String = "Jahangir Ahmed Kacher",
    val category: String,
    val level: String = "Beginner to Pro",
    val duration: String,
    val price: Double,
    val lessonsCount: Int,
    val status: String = "PUBLISHED"
)

@Entity(tableName = "course_enrollments")
data class CourseEnrollmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long,
    val customerId: Long,
    val courseTitle: String,
    val progressPercent: Int = 0,
    val enrolledAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val fileName: String,
    val status: String = "PUBLISHED"
)

@Entity(tableName = "product_purchases")
data class ProductPurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val customerId: Long,
    val productTitle: String,
    val amount: Double,
    val downloadToken: String,
    val purchasedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_projects")
data class PortfolioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val description: String,
    val client: String,
    val projectUrl: String = "kgsocialnetwork.com",
    val status: String = "PUBLISHED"
)

@Entity(tableName = "blog_posts")
data class BlogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val slug: String,
    val category: String,
    val excerpt: String,
    val content: String,
    val author: String = "Jahangir Ahmed Kacher",
    val status: String = "PUBLISHED",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "testimonials")
data class TestimonialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val serviceName: String,
    val rating: Int = 5,
    val review: String,
    val verified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "contact_requests")
data class ContactRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val whatsapp: String,
    val serviceRequired: String,
    val budget: String,
    val message: String,
    val status: String = "NEW", // NEW, CONTACTED, IN_PROGRESS, CLOSED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_settings")
data class PlatformSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val websiteName: String = "KG SOCIAL NETWORK",
    val tagline: String = "WE CAN DO EVERYTHING",
    val subline: String = "Digital Services • Marketing • Learning • Technology",
    val founder: String = "Jahangir Ahmed Kacher",
    val email: String = "KACHERGROUPC@GMAIL.COM",
    val phone: String = "9622102381",
    val whatsapp: String = "9622102381",
    val location: String = "Jammu & Kashmir, India",
    val websiteUrl: String = "https://kgsocialnetwork.com",
    val upiId: String = "9622102381@upi",
    val maintenanceMode: Boolean = false
)
