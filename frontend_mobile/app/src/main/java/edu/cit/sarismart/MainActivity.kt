package edu.cit.sarismart

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint
import edu.cit.sarismart.core.navigation.CoreNavigationController
import edu.cit.sarismart.core.theme.AppTheme


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
                    CoreNavigationController ()
                }
            }
        }
    }
}