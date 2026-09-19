package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :emailOrPhone OR phone = :emailOrPhone LIMIT 1")
    suspend fun getUserByEmailOrPhone(emailOrPhone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserByIdFlow(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCountFlow(): Flow<Int>
}

@Dao
interface AdminDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminEntity): Long

    @Query("SELECT * FROM admins WHERE adminUsername = :username LIMIT 1")
    suspend fun getAdminByUsername(username: String): AdminEntity?

    @Query("SELECT * FROM admins WHERE id = :id LIMIT 1")
    suspend fun getAdminById(id: Long): AdminEntity?

    @Query("SELECT * FROM admins ORDER BY createdAt DESC")
    fun getAllAdminsFlow(): Flow<List<AdminEntity>>

    @Update
    suspend fun updateAdmin(admin: AdminEntity)
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE status = 'PUBLISHED' ORDER BY id ASC")
    fun getPublishedServicesFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services ORDER BY id ASC")
    fun getAllServicesFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: Long): ServiceEntity?

    @Query("SELECT * FROM services WHERE slug = :slug LIMIT 1")
    suspend fun getServiceBySlug(slug: String): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteService(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: ServicePackageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(pkgs: List<ServicePackageEntity>)

    @Query("SELECT * FROM service_packages WHERE serviceId = :serviceId ORDER BY price ASC")
    fun getPackagesForServiceFlow(serviceId: Long): Flow<List<ServicePackageEntity>>

    @Query("SELECT * FROM service_packages WHERE serviceId = :serviceId ORDER BY price ASC")
    suspend fun getPackagesForService(serviceId: Long): List<ServicePackageEntity>

    @Query("SELECT * FROM service_packages WHERE id = :id LIMIT 1")
    suspend fun getPackageById(id: Long): ServicePackageEntity?
}

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getOrdersByCustomerFlow(customerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET assignedStaff = :staff, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updateAssignedStaff(orderId: String, staff: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET completedFiles = :files, orderStatus = 'COMPLETED', updatedAt = :timestamp WHERE id = :orderId")
    suspend fun completeOrderWithFiles(orderId: String, files: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET paymentStatus = :status, paymentTxnId = :txnId, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: String, status: String, txnId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM orders")
    fun getTotalOrdersCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE orderStatus = :status")
    fun getOrdersCountByStatusFlow(status: String): Flow<Int>

    @Query("SELECT SUM(amount) FROM orders WHERE paymentStatus = 'PAID'")
    fun getTotalRevenueFlow(): Flow<Double?>
}

@Dao
interface OrderActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: OrderActivityEntity)

    @Query("SELECT * FROM order_activity WHERE orderId = :orderId ORDER BY createdAt ASC")
    fun getActivitiesForOrderFlow(orderId: String): Flow<List<OrderActivityEntity>>
}

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE orderId = :orderId ORDER BY createdAt ASC")
    fun getMessagesForOrderFlow(orderId: String): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET isRead = 1 WHERE orderId = :orderId AND senderType != :currentUserType")
    suspend fun markMessagesAsRead(orderId: String, currentUserType: String)
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE recipientType = 'ADMIN' ORDER BY createdAt DESC")
    fun getAdminNotificationsFlow(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE recipientType = 'CUSTOMER' AND (recipientId = :customerId OR recipientId = 0) ORDER BY createdAt DESC")
    fun getCustomerNotificationsFlow(customerId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientType = 'ADMIN' AND isRead = 0")
    fun getUnreadAdminNotificationsCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientType = 'CUSTOMER' AND (recipientId = :customerId OR recipientId = 0) AND isRead = 0")
    fun getUnreadCustomerNotificationsCountFlow(customerId: Long): Flow<Int>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientType = 'ADMIN'")
    suspend fun markAllAdminAsRead()
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE status = 'PUBLISHED' ORDER BY id ASC")
    fun getPublishedCoursesFlow(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: Long): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enrollStudent(enrollment: CourseEnrollmentEntity): Long

    @Query("SELECT * FROM course_enrollments WHERE customerId = :customerId ORDER BY enrolledAt DESC")
    fun getCustomerEnrollmentsFlow(customerId: Long): Flow<List<CourseEnrollmentEntity>>

    @Query("SELECT COUNT(*) FROM course_enrollments")
    fun getTotalStudentsCountFlow(): Flow<Int>
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE status = 'PUBLISHED' ORDER BY id ASC")
    fun getPublishedProductsFlow(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun purchaseProduct(purchase: ProductPurchaseEntity): Long

    @Query("SELECT * FROM product_purchases WHERE customerId = :customerId ORDER BY purchasedAt DESC")
    fun getCustomerPurchasesFlow(customerId: Long): Flow<List<ProductPurchaseEntity>>
}

@Dao
interface ContentDao {
    @Query("SELECT * FROM portfolio_projects WHERE status = 'PUBLISHED' ORDER BY id ASC")
    fun getPortfolioFlow(): Flow<List<PortfolioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(item: PortfolioEntity): Long

    @Query("SELECT * FROM blog_posts WHERE status = 'PUBLISHED' ORDER BY createdAt DESC")
    fun getBlogsFlow(): Flow<List<BlogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlog(blog: BlogEntity): Long

    @Query("SELECT * FROM testimonials WHERE verified = 1 ORDER BY createdAt DESC")
    fun getTestimonialsFlow(): Flow<List<TestimonialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestimonial(testimonial: TestimonialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactRequest(request: ContactRequestEntity): Long

    @Query("SELECT * FROM contact_requests ORDER BY createdAt DESC")
    fun getAllContactRequestsFlow(): Flow<List<ContactRequestEntity>>

    @Query("UPDATE contact_requests SET status = :status WHERE id = :id")
    suspend fun updateContactStatus(id: Long, status: String)

    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<PlatformSettingsEntity?>

    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): PlatformSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: PlatformSettingsEntity)
}
