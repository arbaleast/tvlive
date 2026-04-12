package com.example.netflixtv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.netflixtv.data.ContentRepository
import com.example.netflixtv.ui.AppNav

class MainActivity : ComponentActivity() {

    private lateinit var repository: ContentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = ContentRepository(applicationContext)

        setContent {
            val navController = rememberNavController()

            Box(modifier = Modifier.background(Color.Black)) {
                AppNav(
                    repository = repository,
                    navController = navController
                )
            }
        }
    }
}