package com.example.beehive

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.beehive.ui.theme.BeehiveTheme
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeehiveTheme {
                BeehiveApp(
                    restartApp = {
                        val packageManager: PackageManager = this.packageManager
                        val intent =
                            packageManager.getLaunchIntentForPackage(this.packageName)
                                ?: return@BeehiveApp

                        val componentName = intent.component
                        val mainIntent = Intent.makeRestartActivityTask(componentName)

                        mainIntent.setPackage(this.packageName)
                        this.startActivity(mainIntent)
                        exitProcess(0)
                    }
                )
            }
        }
    }
}





