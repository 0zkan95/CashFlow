package com.example.cashbookneo.navigation

sealed class Screen(val route: String) {
    object OnboardingStep1 : Screen("onboarding/step1")
    object OnboardingStep2 : Screen("onboarding/step2")
    object OnboardingStep3 : Screen("onboarding/step3")
    
    object AppLock : Screen("auth/lock")
    
    object Home : Screen("home")
    object Analytics : Screen("analytics")
    object Savings : Screen("savings")
    object Settings : Screen("settings")
    
    object Search : Screen("search")
    object TransactionDetail : Screen("transaction/{id}") {
        fun createRoute(id: String) = "transaction/$id"
    }
    object RecordEntry : Screen("transaction/add")

    object EditVault : Screen("savings/edit/{id}") {
        fun createRoute(id: String) = "savings/edit/$id"
    }
    object CreateVault : Screen("savings/create")
    
    object ManageAccounts : Screen("accounts/manage")
    object EditAccount : Screen("accounts/edit/{id}") {
        fun createRoute(id: String) = "accounts/edit/$id"
    }
    object BackupRestore : Screen("backup")
    object ConfigureSecurity : Screen("settings/security")
    object Notes : Screen("notes")
    object EditNote : Screen("notes/edit/{id}") {
        fun createRoute(id: String) = "notes/edit/$id"
    }
    object CreateNote : Screen("notes/create")
}

sealed class NavGroup(val route: String) {
    object Onboarding : NavGroup("onboarding_group")
    object MainApp : NavGroup("main_app_group")
}
