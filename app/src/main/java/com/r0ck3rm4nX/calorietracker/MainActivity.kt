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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager

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

    var dailyGoal by remember { mutableStateOf("2500") }
    var consumed by remember { mutableStateOf("0") }
    var burned by remember { mutableStateOf("0") }

    val focusManager = LocalFocusManager.current

    val goalNumber = dailyGoal.toIntOrNull() ?: 0
    val consumedNumber = consumed.toIntOrNull() ?: 0
    val burnedNumber = burned.toIntOrNull() ?: 0
    val remaining = goalNumber - consumedNumber + burnedNumber

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(text = "Calorie Tracker")
        Text(text = "Daily Goal")

//Calorie goal input. (Input)
        OutlinedTextField(
            value = dailyGoal,
            onValueChange = { dailyGoal = it },
            label = { Text("Calories") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )

//Calories consumed field. (Input)
        OutlinedTextField(
            value = consumed,
            onValueChange = { consumed = it },
            label = { Text("Calories Consumed") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )

        //Calorie goal input. (Input)
        OutlinedTextField(
            value = burned,
            onValueChange = { burned = it },
            label = { Text("Calories Burned") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )

        Text(text = "Remaining: $remaining kcal")
    }
}

@Preview(showBackground = true)
@Composable
fun CalorieTrackerPreview() {
    CalorieTrackerTheme {
        CalorieTrackerScreen()
    }
}