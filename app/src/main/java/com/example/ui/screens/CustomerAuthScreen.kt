package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KgTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.KgViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun CustomerAuthScreen(
    isRegisterInitial: Boolean = false,
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    var isRegister by remember { mutableStateOf(isRegisterInitial) }

    // Form states
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Srinagar") }
    var state by remember { mutableStateOf("Jammu & Kashmir") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            KgTopBar(
                title = if (isRegister) "Customer Registration" else "Customer Sign In",
                subtitle = "KG Social Network",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = if (isRegister) "Create Customer Account" else "Welcome Back",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DarkText
                        )
                        Text(
                            text = if (isRegister) "Join KG Social Network to book digital services, enroll in courses, and access digital products." else "Enter your registered email or phone to access your orders and dashboard.",
                            fontSize = 13.sp,
                            color = SubtitleText
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isRegister) {
                            // Register Fields
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name *") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().testTag("input_reg_fullname"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(if (isRegister) "Email Address *" else "Email or Phone Number *") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_auth_identifier"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (isRegister) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone Number (10 digits) *") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().testTag("input_reg_phone"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("City") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = state,
                                    onValueChange = { state = it },
                                    label = { Text("State") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("input_auth_password"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (isRegister) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password *") },
                                leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("input_reg_confirm_password"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (isRegister) {
                                    if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                                        viewModel.showMessage("Please fill in all mandatory fields (*).")
                                        return@Button
                                    }
                                    if (password != confirmPassword) {
                                        viewModel.showMessage("Passwords do not match.")
                                        return@Button
                                    }
                                    viewModel.registerCustomer(
                                        fullName = fullName,
                                        email = email,
                                        phone = phone,
                                        passwordPlain = password,
                                        city = city,
                                        state = state
                                    ) {
                                        viewModel.navigateTo(ScreenDestination.Home)
                                    }
                                } else {
                                    if (email.isBlank() || password.isBlank()) {
                                        viewModel.showMessage("Please enter your email/phone and password.")
                                        return@Button
                                    }
                                    viewModel.loginCustomer(email, password) {
                                        viewModel.navigateTo(ScreenDestination.Home)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_auth_submit")
                        ) {
                            Text(
                                text = if (isRegister) "Create Account" else "Sign In",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isRegister) "Already have an account?" else "Don't have an account?",
                                fontSize = 13.sp,
                                color = SubtitleText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRegister) "Sign In" else "Register Now",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue,
                                modifier = Modifier
                                    .clickable { isRegister = !isRegister }
                                    .testTag("btn_switch_auth_mode")
                            )
                        }

                        // Demo credentials helper
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = SoftGray,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("💡 Quick Customer Test Account:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                Text("Email: customer@kgsocialnetwork.com", fontSize = 11.sp, color = SubtitleText)
                                Text("Password: customer123", fontSize = 11.sp, color = SubtitleText)
                            }
                        }
                    }
                }
            }
        }
    }
}
