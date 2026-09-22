package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*

@Composable
fun BackupRestoreScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            GoogleDriveStatusCard(
                userName = "Hidayet Aslan",
                userEmail = "hidayetaslan2315@gmail.com",
                lastSnapshot = "Today, 01:45 AM",
                backupSize = "14.2 KB",
                driveUsage = "99.9% drive free",
                onSwitchAccount = {}
            )
        }

        item {
            AutomaticBackupCard(
                isAutoBackupEnabled = true,
                onAutoBackupToggle = {},
                selectedFrequency = "Daily",
                onFrequencySelected = {},
                isWifiOnlyEnabled = true,
                onWifiOnlyToggle = {}
            )
        }

        item {
            EncryptionSettingsCard(
                isEncryptionEnabled = true,
                onEncryptionToggle = {},
                password = "••••••••••••••••••••",
                onPasswordChange = {}
            )
        }

        item {
            BackupRestoreActions(
                onBackupNow = {},
                onRestoreData = {}
            )
        }

        item {
            OfflineRawExportsSection()
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
