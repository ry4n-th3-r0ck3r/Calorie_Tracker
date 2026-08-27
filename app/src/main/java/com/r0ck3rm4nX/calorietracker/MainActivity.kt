package com.r0ck3rm4nX.calorietracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.r0ck3rm4nX.calorietracker.ui.theme.CalorieTrackerTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalorieTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalorieTrackerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CalorieTrackerScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(text = "Calorie Tracker")
        Text(text = "Daily Goal: 2500 kcal")
        Text(text = "Consumed: 0 kcal")
        Text(text = "Remaining: 2500 kcal")
    }
}

@Preview(showBackground = true)
@Composable
fun CalorieTrackerPreview() {
    CalorieTrackerTheme {
        CalorieTrackerScreen()
    }
}