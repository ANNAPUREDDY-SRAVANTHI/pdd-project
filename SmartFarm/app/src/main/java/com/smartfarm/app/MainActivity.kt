package com.smartfarm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.smartfarm.app.api.RetrofitClient
import com.smartfarm.app.navigation.AppNavigation
import com.smartfarm.app.ui.theme.SmartFarmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- THIS LINE UNLOCKS THE WORKER LIST ---
        RetrofitClient.init(this)

        setContent {
            SmartFarmTheme {
                AppNavigation()
            }
        }
    }
}