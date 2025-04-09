package edu.cit.sarismart

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import edu.cit.sarismart.ui.MainScreen
import edu.cit.sarismart.ui.theme.AppTheme


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme (
                dynamicColor = false
            ) {
                Surface (
                    color = MaterialTheme.colorScheme.background
                    ) {
                    MainScreen ()
                }
            }
        }
    }
}