package com.example.cashbookneo

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.cashbookneo.model.*
import com.example.cashbookneo.navigation.Screen
import com.example.cashbookneo.ui.screens.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.ui.components.AppDrawerContent
import com.example.cashbookneo.ui.components.AppShellTopBar
import com.example.cashbookneo.ui.components.AppShellBottomBar
import com.example.cashbookneo.viewmodel.OnboardingViewModel
import com.example.cashbookneo.data.PreferenceManager
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.abs
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cashbookneo.security.BiometricPromptManager
import com.example.cashbookneo.viewmodel.MainViewModel
import com.example.cashbookneo.viewmodel.ViewModelFactory
import com.example.cashbookneo.viewmodel.SavingsViewModel
import com.example.cashbookneo.viewmodel.CustomSearchViewModel
import com.example.cashbookneo.viewmodel.AnalyticsViewModel
import com.example.cashbookneo.viewmodel.NotesViewModel
import com.example.cashbookneo.viewmodel.EditNoteViewModel
import com.example.cashbookneo.viewmodel.SettingsViewModel
import com.example.cashbookneo.viewmodel.EditAccountViewModel
import com.example.cashbookneo.viewmodel.SecurityViewModel
import com.example.cashbookneo.data.repository.TransactionRepository
import com.example.cashbookneo.data.AppDatabase
import com.example.cashbookneo.data.DatabaseMaintenanceService
import com.example.cashbookneo.security.SecurityUtils

class MainActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var biometricPromptManager: BiometricPromptManager
    private val mainViewModel: MainViewModel by viewModels {
        val database = AppDatabase.getDatabase(this, byteArrayOf())
        ViewModelFactory(PreferenceManager(this), database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)
        biometricPromptManager = BiometricPromptManager(this)

        lifecycleScope.launch {
            biometricPromptManager.promptResults.collect { result ->
                if (result is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                    mainViewModel.unlock()
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            val themeState = remember { ThemeState() }
            CompositionLocalProvider(LocalTheme provides themeState) {
                CashFlowTheme(darkTheme = themeState.isDarkMode) {
                    val activeAccount by mainViewModel.activeAccountState.collectAsState()
                    val transactions by mainViewModel.transactions.collectAsState()
                    val memos by mainViewModel.memos.collectAsState()
                    val vaults = mainViewModel.vaults
                    
                    var entryIsCredit by remember { mutableStateOf(false) }
                    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
                    
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val navController = rememberNavController()
                    
                    val screenMasking by preferenceManager.screenMasking.collectAsState(initial = true)

                    LaunchedEffect(screenMasking) {
                        if (screenMasking) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        }
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = MidnightBackground,
                                drawerTonalElevation = 0.dp
                            ) {
                                AppDrawerContent(
                                    mainViewModel = mainViewModel,
                                    currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: Screen.Home.route,
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    onClose = { scope.launch { drawerState.close() } }
                                )
                            }
                        },
                        gesturesEnabled = mainViewModel.hasCompletedOnboarding && !mainViewModel.isAppLocked
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val showShell = mainViewModel.hasCompletedOnboarding && !mainViewModel.isAppLocked && currentRoute in listOf(
                            Screen.Home.route,
                            Screen.Analytics.route,
                            Screen.Savings.route,
                            Screen.Settings.route,
                            Screen.Search.route,
                            Screen.Notes.route,
                            Screen.ManageAccounts.route,
                            Screen.BackupRestore.route
                        )

                        Scaffold(
                            topBar = {
                                if (showShell) {
                                    AppShellTopBar(
                                        navController = navController,
                                        onMenuClick = { scope.launch { drawerState.open() } }
                                    )
                                }
                            },
                            bottomBar = {
                                if (showShell) {
                                    AppShellBottomBar(navController = navController)
                                }
                            },
                            containerColor = MidnightBackground,
                            contentWindowInsets = WindowInsets(0, 0, 0, 0)
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                NavHost(
                                    navController = navController,
                                    startDestination = if (mainViewModel.hasCompletedOnboarding) Screen.Home.route else Screen.OnboardingStep1.route
                                ) {
                                    composable(Screen.OnboardingStep1.route) {
                                        val onboardingViewModel: OnboardingViewModel = viewModel()
                                        OnboardingStep1Screen(
                                            viewModel = onboardingViewModel,
                                            onNavigateToStep2 = { navController.navigate(Screen.OnboardingStep2.route) },
                                            onRestoreBackup = { /* Placeholder */ }
                                        )
                                    }
                                    
                                    composable(Screen.OnboardingStep2.route) {
                                        val onboardingViewModel: OnboardingViewModel = viewModel()
                                        OnboardingStep2Screen(
                                            viewModel = onboardingViewModel,
                                            onNavigateToStep3 = { navController.navigate(Screen.OnboardingStep3.route) },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                    
                                    composable(Screen.OnboardingStep3.route) {
                                        val onboardingViewModel: OnboardingViewModel = viewModel()
                                        OnboardingStep3Screen(
                                            viewModel = onboardingViewModel,
                                            onComplete = { data ->
                                                scope.launch {
                                                    preferenceManager.setOnboardingCompleted(true)
                                                    if (data.securityMode == "Biometrics") {
                                                        val salt = SecurityUtils.generateSalt()
                                                        val hash = SecurityUtils.hashPin(data.pin, salt)
                                                        preferenceManager.savePin(hash, salt)
                                                    }
                                                    val timeoutMs = when (data.autoLockTimeout) {
                                                        "Immediately" -> 0L
                                                        "1 Min" -> 60_000L
                                                        "5 Min" -> 300_000L
                                                        else -> 0L
                                                    }
                                                    preferenceManager.setLockTimeout(timeoutMs)
                                                    mainViewModel.userData = data
                                                    mainViewModel.hasCompletedOnboarding = true
                                                    mainViewModel.createProfile(data)
                                                    navController.navigate(Screen.Home.route) {
                                                        popUpTo(Screen.OnboardingStep1.route) { inclusive = true }
                                                    }
                                                }
                                            },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                    
                                    composable(Screen.Home.route) {
                                        if (mainViewModel.isAppLocked) {
                                            AppLockScreen(
                                                userName = mainViewModel.userData?.fullName ?: "Hidayet Aslan",
                                                vaultName = "Primary Vault",
                                                pinHash = preferenceManager.getPinHash(),
                                                pinSalt = preferenceManager.getPinSalt(),
                                                onUnlock = { mainViewModel.unlock() }
                                            )
                                        } else {
                                            DashboardScreen(
                                                activeAccount = activeAccount,
                                                transactions = transactions,
                                                onNavigate = { screen ->
                                                    if (screen == "RecordEntryCredit" || screen == "RecordEntryDebit") {
                                                        entryIsCredit = screen == "RecordEntryCredit"
                                                        selectedTransaction = null
                                                        navController.navigate(Screen.RecordEntry.route)
                                                    } else {
                                                        navController.navigate(screen)
                                                    }
                                                },
                                                onTransactionClick = {
                                                    selectedTransaction = it
                                                    navController.navigate(Screen.TransactionDetail.createRoute(it.id))
                                                },
                                                onDeleteTransaction = { mainViewModel.removeTransaction(it) },
                                                onEditTransaction = {
                                                    selectedTransaction = it
                                                    navController.navigate(Screen.RecordEntry.route)
                                                }
                                            )
                                        }
                                    }

                                    composable(Screen.Analytics.route) {
                                        val analyticsViewModel: AnalyticsViewModel = viewModel(
                                            factory = AnalyticsViewModel.Factory(
                                                activeAccount = activeAccount,
                                                analyticsDao = AppDatabase.getDatabase(this@MainActivity, byteArrayOf()).analyticsDao()
                                            )
                                        )
                                        AnalyticsScreen(
                                            viewModel = analyticsViewModel,
                                            onSeeAllRecordsClick = { navController.navigate(Screen.Search.route) }
                                        )
                                    }

                                    composable(Screen.Savings.route) {
                                        val savingsViewModel: SavingsViewModel = viewModel()
                                        SavingsScreen(
                                            activeAccount = activeAccount,
                                            transactions = transactions,
                                            onNavigate = { navController.navigate(it) },
                                            viewModel = savingsViewModel
                                        )
                                    }

                                    composable(
                                        route = Screen.EditVault.route,
                                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                                    ) { backStackEntry ->
                                        val vaultId = backStackEntry.arguments?.getString("id")
                                        val savingsViewModel: SavingsViewModel = viewModel()
                                        EditVaultScreen(
                                            vaultId = vaultId,
                                            viewModel = savingsViewModel,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(Screen.CreateVault.route) {
                                        val savingsViewModel: SavingsViewModel = viewModel()
                                        EditVaultScreen(
                                            vaultId = null,
                                            viewModel = savingsViewModel,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(Screen.Settings.route) {
                                        val database = remember { AppDatabase.getDatabase(this@MainActivity, byteArrayOf()) }
                                        val dbMaintenanceService = remember { DatabaseMaintenanceService(database.openHelper.writableDatabase, this@MainActivity) }
                                        val settingsViewModel: SettingsViewModel = viewModel(
                                            factory = SettingsViewModel.Factory(
                                                activeAccount = activeAccount,
                                                preferenceManager = preferenceManager,
                                                dbMaintenanceService = dbMaintenanceService
                                            )
                                        )
                                        SettingsScreen(
                                            viewModel = settingsViewModel,
                                            onNavigate = { navController.navigate(it) },
                                            onNavigateToSecurity = { navController.navigate(Screen.ConfigureSecurity.route) }
                                        )
                                    }

                                    composable(Screen.ConfigureSecurity.route) {
                                        val securityViewModel: SecurityViewModel = viewModel(
                                            factory = SecurityViewModel.Factory(
                                                activeAccount = activeAccount,
                                                preferenceManager = preferenceManager
                                            )
                                        )
                                        ConfigureSecurityScreen(
                                            viewModel = securityViewModel,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(Screen.Search.route) {
                                        val repository = remember { TransactionRepository(AppDatabase.getDatabase(this@MainActivity, byteArrayOf())) }
                                        val searchViewModel: CustomSearchViewModel = viewModel(factory = CustomSearchViewModel.Factory(repository))
                                        SearchScreen(
                                            activeAccount = activeAccount,
                                            viewModel = searchViewModel,
                                            onTransactionClick = { txId ->
                                                navController.navigate(Screen.TransactionDetail.createRoute(txId))
                                            }
                                        )
                                    }

                                    composable(Screen.TransactionDetail.route) { backStackEntry ->
                                        val txId = backStackEntry.arguments?.getString("id")
                                        val transaction = transactions.find { it.id == txId } ?: selectedTransaction
                                        transaction?.let { tx ->
                                            TransactionDetailScreen(
                                                activeAccount = activeAccount,
                                                transaction = tx,
                                                onBack = { navController.popBackStack() },
                                                onEdit = {
                                                    selectedTransaction = it
                                                    navController.navigate(Screen.RecordEntry.route)
                                                },
                                                onDelete = {
                                                    mainViewModel.removeTransaction(it)
                                                    navController.popBackStack()
                                                },
                                                onDuplicate = {
                                                    val duplicated = it.copy(
                                                        id = UUID.randomUUID().toString(),
                                                        dateTime = LocalDateTime.now()
                                                    )
                                                    mainViewModel.addTransaction(duplicated)
                                                    navController.popBackStack()
                                                },
                                                onSplitBill = {
                                                    val split1 = it.copy(id = UUID.randomUUID().toString(), amount = it.amount / 2)
                                                    val split2 = it.copy(id = UUID.randomUUID().toString(), amount = it.amount / 2)
                                                    mainViewModel.removeTransaction(it)
                                                    mainViewModel.addTransaction(split1)
                                                    mainViewModel.addTransaction(split2)
                                                    navController.popBackStack()
                                                },
                                                onExport = { /* Export */ },
                                                onAttachNote = {
                                                    val updated = it.copy(memo = (it.memo ?: "") + "\n[Internal Note added]")
                                                    mainViewModel.updateTransaction(updated)
                                                    selectedTransaction = updated
                                                }
                                            )
                                        }
                                    }

                                    composable(Screen.RecordEntry.route) {
                                        val suggestions by mainViewModel.payeeSuggestions.collectAsState()
                                        RecordEntryScreen(
                                            activeAccount = activeAccount,
                                            transactionToEdit = selectedTransaction,
                                            initialIsCredit = entryIsCredit,
                                            payeeSuggestions = suggestions,
                                            onBack = { 
                                                selectedTransaction = null
                                                navController.popBackStack() 
                                            },
                                            onSave = { newTransaction ->
                                                val index = transactions.indexOfFirst { it.id == newTransaction.id }
                                                if (index != -1) {
                                                    mainViewModel.updateTransaction(newTransaction)
                                                } else {
                                                    mainViewModel.addTransaction(newTransaction)
                                                }
                                                selectedTransaction = null
                                                navController.popBackStack()
                                            },
                                            onSaveAndAddAnother = { newTransaction ->
                                                mainViewModel.addTransaction(newTransaction)
                                                selectedTransaction = null
                                            }
                                        )
                                    }

                                    composable(Screen.ManageAccounts.route) {
                                        ManageAccountsScreen(
                                            mainViewModel = mainViewModel,
                                            onNavigate = { navController.navigate(it) },
                                            onEditAccount = { id ->
                                                navController.navigate(Screen.EditAccount.createRoute(id))
                                            }
                                        )
                                    }

                                    composable(
                                        route = Screen.EditAccount.route,
                                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                                    ) { backStackEntry ->
                                        val profileId = backStackEntry.arguments?.getString("id") ?: "acc1"
                                        val editAccountViewModel: EditAccountViewModel = viewModel(
                                            factory = EditAccountViewModel.Factory(
                                                profileId = profileId,
                                                profileDao = AppDatabase.getDatabase(this@MainActivity, byteArrayOf()).profileDao()
                                            )
                                        )
                                        EditAccountScreen(
                                            viewModel = editAccountViewModel,
                                            onBack = { navController.popBackStack() },
                                            onNavigateToSecurity = { navController.navigate(Screen.ConfigureSecurity.route) }
                                        )
                                    }

                                    composable(Screen.BackupRestore.route) {
                                        BackupRestoreScreen()
                                    }
                                    
                                    composable(Screen.Notes.route) {
                                        val repository = remember { TransactionRepository(AppDatabase.getDatabase(this@MainActivity, byteArrayOf())) }
                                        val notesViewModel: NotesViewModel = viewModel(
                                            factory = NotesViewModel.Factory(
                                                profileId = activeAccount.id,
                                                memoDao = AppDatabase.getDatabase(this@MainActivity, byteArrayOf()).financialMemoDao(),
                                                transactionRepository = repository
                                            )
                                        )
                                        NotesScreen(
                                            viewModel = notesViewModel,
                                            onNavigate = { navController.navigate(it) }
                                        )
                                    }

                                    composable(
                                        route = Screen.EditNote.route,
                                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                                    ) { backStackEntry ->
                                        val noteId = backStackEntry.arguments?.getString("id")
                                        val editViewModel: EditNoteViewModel = viewModel(
                                            factory = EditNoteViewModel.Factory(
                                                memoDao = AppDatabase.getDatabase(this@MainActivity, byteArrayOf()).financialMemoDao(),
                                                profileId = activeAccount.id,
                                                initialNoteId = noteId
                                            )
                                        )
                                        EditNoteScreen(
                                            viewModel = editViewModel,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(Screen.CreateNote.route) {
                                        val editViewModel: EditNoteViewModel = viewModel(
                                            factory = EditNoteViewModel.Factory(
                                                memoDao = AppDatabase.getDatabase(this@MainActivity, byteArrayOf()).financialMemoDao(),
                                                profileId = activeAccount.id,
                                                initialNoteId = null
                                            )
                                        )
                                        EditNoteScreen(
                                            viewModel = editViewModel,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.checkLockOnStart()
        if (mainViewModel.isAppLocked) {
            biometricPromptManager.showBiometricPrompt(
                "Unlock CashFlow",
                "Verify identity to access your encrypted ledger."
            )
        }
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.onAppStop()
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    CashFlowTheme {
        DashboardScreen(activeAccount = DummyUser, transactions = DummyTransactions, onNavigate = {}, onTransactionClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SavingsPreview() {
    CashFlowTheme {
        SavingsScreen(activeAccount = DummyUser, transactions = DummyTransactions, onNavigate = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    CashFlowTheme {
        SettingsScreen(viewModel = viewModel(), onNavigate = {}, onNavigateToSecurity = {})
    }
}

@Preview(showBackground = true)
@Composable
fun BackupRestorePreview() {
    CashFlowTheme {
        BackupRestoreScreen()
    }
}

/*
@Preview(showBackground = true)
@Composable
fun ManageAccountsPreview() {
    CashFlowTheme {
        ManageAccountsScreen(onNavigate = {}, onBack = {})
    }
}
*/

@Preview(showBackground = true)
@Composable
fun RecordEntryDebitPreview() {
    CashFlowTheme {
        RecordEntryScreen(activeAccount = DummyUser, initialIsCredit = false, onBack = {}, onSave = {}, onSaveAndAddAnother = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RecordEntryCreditPreview() {
    CashFlowTheme {
        RecordEntryScreen(activeAccount = DummyUser, initialIsCredit = true, onBack = {}, onSave = {}, onSaveAndAddAnother = {})
    }
}

@Preview(showBackground = true)
@Composable
fun NotesPreview() {
    // Preview with dummy data placeholder
}

@Preview(showBackground = true)
@Composable
fun AppLockPreview() {
    CashFlowTheme {
        AppLockScreen(pinHash = null, pinSalt = null, onUnlock = {})
    }
}
