package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class KgRepository(private val db: AppDatabase) {
    val userDao = db.userDao()
    val adminDao = db.adminDao()
    val serviceDao = db.serviceDao()
    val orderDao = db.orderDao()
    val orderActivityDao = db.orderActivityDao()
    val messageDao = db.messageDao()
    val notificationDao = db.notificationDao()
    val courseDao = db.courseDao()
    val productDao = db.productDao()
    val contentDao = db.contentDao()

    // --- Authentication ---
    suspend fun registerCustomer(
        fullName: String,
        email: String,
        phone: String,
        passwordPlain: String,
        address: String = "",
        city: String = "Srinagar",
        state: String = "Jammu & Kashmir"
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()

        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (cleanPhone.length < 10) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit phone number."))
        }
        if (passwordPlain.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters long."))
        }

        val existing = userDao.getUserByEmailOrPhone(cleanEmail) ?: userDao.getUserByEmailOrPhone(cleanPhone)
        if (existing != null) {
            return Result.failure(IllegalStateException("An account with this email or phone number already exists."))
        }

        val hash = AppDatabase.hashPassword(passwordPlain)
        val user = UserEntity(
            fullName = fullName.trim(),
            email = cleanEmail,
            phone = cleanPhone,
            passwordHash = hash,
            address = address.trim(),
            city = city.trim(),
            state = state.trim(),
            country = "India",
            createdAt = System.currentTimeMillis()
        )
        val newId = userDao.insertUser(user)
        val createdUser = user.copy(id = newId)

        // Create welcome notification
        notificationDao.insertNotification(
            NotificationEntity(
                recipientType = "CUSTOMER",
                recipientId = newId,
                type = "SYSTEM_NOTIFICATION",
                title = "Welcome to KG Social Network!",
                message = "Your account has been created. Explore our digital services and start growing your brand today."
            )
        )

        return Result.success(createdUser)
    }

    suspend fun loginCustomer(identifier: String, passwordPlain: String): Result<UserEntity> {
        val cleanIdentifier = identifier.trim().lowercase()
        val user = userDao.getUserByEmailOrPhone(cleanIdentifier)
            ?: return Result.failure(IllegalArgumentException("Invalid email/phone or password."))

        val hash = AppDatabase.hashPassword(passwordPlain)
        if (user.passwordHash != hash) {
            return Result.failure(IllegalArgumentException("Invalid email/phone or password."))
        }

        // Update last login
        userDao.updateUser(user.copy(lastLogin = System.currentTimeMillis()))
        return Result.success(user)
    }

    suspend fun loginAdmin(username: String, passwordPlain: String): Result<AdminEntity> {
        val cleanUsername = username.trim().lowercase()
        val admin = adminDao.getAdminByUsername(cleanUsername)
            ?: return Result.failure(IllegalArgumentException("Invalid admin credentials."))

        val hash = AppDatabase.hashPassword(passwordPlain)
        if (admin.passwordHash != hash) {
            return Result.failure(IllegalArgumentException("Invalid admin credentials."))
        }

        adminDao.updateAdmin(admin.copy(lastLogin = System.currentTimeMillis()))
        return Result.success(admin)
    }

    // --- Order Lifecycle (Real-Time Pipeline) ---
    suspend fun placeOrder(
        customer: UserEntity,
        service: ServiceEntity,
        pkg: ServicePackageEntity,
        requirements: String,
        instructions: String,
        attachedFiles: String
    ): Result<OrderEntity> {
        val uniqueSuffix = (1000 + (Math.random() * 9000).toInt()).toString()
        val orderId = "KG-ORD-${System.currentTimeMillis() % 100000}-$uniqueSuffix"

        val order = OrderEntity(
            id = orderId,
            customerId = customer.id,
            customerName = customer.fullName,
            customerEmail = customer.email,
            customerPhone = customer.phone,
            serviceId = service.id,
            serviceTitle = service.title,
            serviceCategory = service.category,
            packageId = pkg.id,
            packageName = pkg.name,
            requirements = requirements.trim(),
            instructions = instructions.trim(),
            attachedFiles = attachedFiles.trim(),
            amount = pkg.price,
            paymentStatus = "PENDING",
            orderStatus = "PENDING",
            dueDate = "${pkg.deliveryDays} Business Days",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        orderDao.insertOrder(order)

        // Activity log
        orderActivityDao.insertActivity(
            OrderActivityEntity(
                orderId = orderId,
                actorId = customer.id,
                actorName = customer.fullName,
                actorType = "CUSTOMER",
                action = "ORDER_CREATED",
                oldStatus = "NONE",
                newStatus = "PENDING",
                message = "Order placed by customer for ${service.title} (${pkg.name}) - ₹${pkg.price}."
            )
        )

        // Admin notification (Real-Time notification table)
        notificationDao.insertNotification(
            NotificationEntity(
                recipientType = "ADMIN",
                recipientId = 0,
                type = "NEW_ORDER",
                title = "New Order: $orderId",
                message = "${customer.fullName} ordered ${service.title} (${pkg.name}) for ₹${pkg.price}.",
                relatedOrderId = orderId
            )
        )

        // Customer confirmation notification
        notificationDao.insertNotification(
            NotificationEntity(
                recipientType = "CUSTOMER",
                recipientId = customer.id,
                type = "ORDER_CONFIRMED",
                title = "Order Confirmed: $orderId",
                message = "Your order for ${service.title} has been received. Our team will review the requirements promptly.",
                relatedOrderId = orderId
            )
        )

        return Result.success(order)
    }

    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        admin: AdminEntity,
        note: String = ""
    ): Result<Unit> {
        val order = orderDao.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found."))

        val oldStatus = order.orderStatus
        orderDao.updateOrderStatus(orderId, newStatus)

        // Log activity
        orderActivityDao.insertActivity(
            OrderActivityEntity(
                orderId = orderId,
                actorId = admin.id,
                actorName = admin.fullName,
                actorType = "ADMIN",
                action = "STATUS_CHANGED",
                oldStatus = oldStatus,
                newStatus = newStatus,
                message = if (note.isNotEmpty()) "Status updated from $oldStatus to $newStatus. Note: $note" else "Status updated from $oldStatus to $newStatus."
            )
        )

        // Customer real-time notification
        val notifType = when (newStatus) {
            "CONFIRMED" -> "ORDER_CONFIRMED"
            "IN_PROGRESS" -> "ORDER_STARTED"
            "REVIEW" -> "ORDER_REVIEW"
            "COMPLETED" -> "ORDER_COMPLETED"
            "CANCELLED" -> "ORDER_CANCELLED"
            else -> "SYSTEM_NOTIFICATION"
        }

        notificationDao.insertNotification(
            NotificationEntity(
                recipientType = "CUSTOMER",
                recipientId = order.customerId,
                type = notifType,
                title = "Order Update: $orderId",
                message = "Your order status is now $newStatus. ${if (note.isNotEmpty()) "Message: $note" else ""}",
                relatedOrderId = orderId
            )
        )

        return Result.success(Unit)
    }

    suspend fun completeOrderWithDeliverables(
        orderId: String,
        files: String,
        admin: AdminEntity,
        note: String
    ): Result<Unit> {
        val order = orderDao.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found."))

        orderDao.completeOrderWithFiles(orderId, files)

        orderActivityDao.insertActivity(
            OrderActivityEntity(
                orderId = orderId,
                actorId = admin.id,
                actorName = admin.fullName,
                actorType = "ADMIN",
                action = "ORDER_COMPLETED",
                oldStatus = order.orderStatus,
                newStatus = "COMPLETED",
                message = "Deliverables uploaded by admin: $files. $note"
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                recipientType = "CUSTOMER",
                recipientId = order.customerId,
                type = "ORDER_COMPLETED",
                title = "Order Completed! Deliverables Ready",
                message = "Deliverables for order $orderId are ready for download. Thank you for choosing KG Social Network!",
                relatedOrderId = orderId
            )
        )

        return Result.success(Unit)
    }

    suspend fun assignOrderStaff(orderId: String, staffName: String, admin: AdminEntity): Result<Unit> {
        orderDao.updateAssignedStaff(orderId, staffName)
        orderActivityDao.insertActivity(
            OrderActivityEntity(
                orderId = orderId,
                actorId = admin.id,
                actorName = admin.fullName,
                actorType = "ADMIN",
                action = "STAFF_ASSIGNED",
                oldStatus = "",
                newStatus = "",
                message = "Assigned to $staffName by ${admin.fullName}."
            )
        )
        return Result.success(Unit)
    }

    suspend fun recordOrderPayment(orderId: String, txnId: String, amount: Double): Result<Unit> {
        orderDao.updatePaymentStatus(orderId, "PAID", txnId)
        val order = orderDao.getOrderById(orderId)
        if (order != null) {
            orderActivityDao.insertActivity(
                OrderActivityEntity(
                    orderId = orderId,
                    actorId = order.customerId,
                    actorName = order.customerName,
                    actorType = "CUSTOMER",
                    action = "PAYMENT_RECEIVED",
                    oldStatus = "PENDING",
                    newStatus = "PAID",
                    message = "Payment of ₹$amount confirmed via UPI/Razorpay. Txn Ref: $txnId."
                )
            )
            notificationDao.insertNotification(
                NotificationEntity(
                    recipientType = "ADMIN",
                    recipientId = 0,
                    type = "PAYMENT_SUCCESS",
                    title = "Payment Received: $orderId",
                    message = "Payment of ₹$amount verified for order $orderId. Txn: $txnId",
                    relatedOrderId = orderId
                )
            )
        }
        return Result.success(Unit)
    }

    // --- Real-Time Messaging ---
    suspend fun sendMessage(
        orderId: String,
        senderId: Long,
        senderName: String,
        senderType: String,
        receiverId: Long,
        text: String
    ): Result<MessageEntity> {
        if (text.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("Message cannot be empty."))
        }

        val message = MessageEntity(
            orderId = orderId,
            senderId = senderId,
            senderName = senderName,
            senderType = senderType,
            receiverId = receiverId,
            message = text.trim(),
            createdAt = System.currentTimeMillis()
        )

        val id = messageDao.insertMessage(message)
        val created = message.copy(id = id)

        // Notify other party
        val isSenderAdmin = senderType == "ADMIN"
        notificationDao.insertNotification(
            NotificationEntity(
                recipientType = if (isSenderAdmin) "CUSTOMER" else "ADMIN",
                recipientId = if (isSenderAdmin) receiverId else 0,
                type = "NEW_MESSAGE",
                title = "New Message: $orderId",
                message = "$senderName: ${if (text.length > 50) text.take(50) + "..." else text}",
                relatedOrderId = orderId
            )
        )

        return Result.success(created)
    }

    // --- Services CRUD ---
    suspend fun saveService(service: ServiceEntity): Long {
        return if (service.id == 0L) {
            serviceDao.insertService(service)
        } else {
            serviceDao.updateService(service)
            service.id
        }
    }

    // --- Contact Inquiry ---
    suspend fun submitContactRequest(
        fullName: String,
        email: String,
        phone: String,
        whatsapp: String,
        service: String,
        budget: String,
        message: String
    ): Result<Long> {
        val req = ContactRequestEntity(
            fullName = fullName.trim(),
            email = email.trim(),
            phone = phone.trim(),
            whatsapp = whatsapp.trim(),
            serviceRequired = service.trim(),
            budget = budget.trim(),
            message = message.trim(),
            createdAt = System.currentTimeMillis()
        )
        val id = contentDao.insertContactRequest(req)
        notificationDao.insertNotification(
            NotificationEntity(
                recipientType = "ADMIN",
                recipientId = 0,
                type = "SYSTEM_NOTIFICATION",
                title = "New Project Inquiry",
                message = "$fullName submitted an inquiry for $service (Budget: $budget).",
                relatedOrderId = ""
            )
        )
        return Result.success(id)
    }
}
