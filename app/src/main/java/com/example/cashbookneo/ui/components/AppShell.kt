package com.example.cashbookneo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cashbookneo.navigation.Screen
import com.example.cashbookneo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppShellTopBar(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isSubScreen = currentRoute != Screen.Home.route && 
                     currentRoute != Screen.Analytics.route && 
                     currentRoute != Screen.Savings.route && 
                     currentRoute != Screen.Settings.route &&
                     currentRoute != Screen.Notes.route

    val subtitle = when {
        currentRoute == Screen.Home.route -> "Transactions"
        currentRoute == Screen.Analytics.route -> "Analytics"
        currentRoute == Screen.Savings.route -> "Savings"
        currentRoute == Screen.Settings.route -> "Settings"
        currentRoute == Screen.Search.route -> "Search Matrix"
        currentRoute == Screen.Notes.route -> "Financial Notes"
        currentRoute == Screen.CreateNote.route -> "New Note"
        currentRoute?.startsWith("notes/edit") == true -> "Edit Note"
        currentRoute?.startsWith("transaction/") == true -> "Record Detail"
        currentRoute?.startsWith("savings/edit") == true -> "Edit Vault"
        else -> "Transactions"
    }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = "CashFlow",
                    style = Typography.headlineSmall,
                    color = MidnightOnSurface
                )
                Text(
                    text = subtitle,
                    style = Typography.labelSmall,
                    color = MidnightOnSurfaceVariant
                )
            }
        },
        navigationIcon = {
            if (isSubScreen) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = MidnightOnSurface)
                }
            } else {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Open Navigation Drawer", tint = MidnightOnSurface)
                }
            }
        },
        actions = {
            if (currentRoute == Screen.Search.route) {
                IconButton(onClick = { /* OCR */ }) {
                    Icon(Icons.Rounded.DocumentScanner, contentDescription = "OCR Scan", tint = MidnightOnSurface)
                }
            } else {
                IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                    Icon(Icons.Rounded.Search, contentDescription = "Search", tint = MidnightOnSurface)
                }
            }
            IconButton(onClick = { navController.navigate(Screen.ManageAccounts.route) }) {
                Surface(
                    shape = CircleShape,
                    color = MidnightPrimary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = "Profile",
                        tint = MidnightOnPrimary,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
    )
}

@Composable
fun AppShellBottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        AppShellNavItem(Screen.Home.route, "Transactions", Icons.Rounded.ReceiptLong),
        AppShellNavItem(Screen.Analytics.route, "Analytics", Icons.Rounded.QueryStats),
        AppShellNavItem(Screen.Savings.route, "Savings", Icons.Rounded.Savings),
        AppShellNavItem(Screen.Settings.route, "Settings", Icons.Rounded.Settings)
    )

    NavigationBar(
        containerColor = MidnightSurfaceContainerLow,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(text = item.label, style = Typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MidnightPrimary,
                    selectedTextColor = MidnightPrimary,
                    unselectedIconColor = MidnightOnSurfaceVariant,
                    unselectedTextColor = MidnightOnSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class AppShellNavItem(val route: String, val label: String, val icon: ImageVector)
