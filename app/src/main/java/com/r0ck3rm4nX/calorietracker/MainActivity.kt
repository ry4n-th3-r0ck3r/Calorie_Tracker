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
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val goalNumber = dailyGoal.toIntOrNull() ?: 0
    val consumedNumber = consumed.toIntOrNull() ?: 0
    val burnedNumber = burned.toIntOrNull() ?: 0
    val remaining = goalNumber - consumedNumber + burnedNumber
    val weightNumber = weight.toDoubleOrNull()
    val heightNumber = height.toDoubleOrNull()

//Calculate BMI
    val bmi = if (
        weightNumber != null &&
        heightNumber != null &&
        weightNumber > 0 &&
        heightNumber > 0
    ) {
        (weightNumber / (heightNumber * heightNumber)) * 703
    } else {
        null
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(text = "Calorie Tracker")
        Text(text = "Daily Goal")

    //Current weight (Input)
        Text(text = "Weight (Optional)")
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (lb)") },
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
    //Current height (Input)
        Text(text = "Height (Optional)")

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (inches)") },
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
    //BMI output
        if (bmi != null) {
            Text(text = "BMI: %.1f".format(bmi))

            Text(
                text = "BMI is a general screening measurement based only on height and weight. " +
                        "It does not account for factors such as muscle mass, body composition, " +
                        "or individual health and should not be treated as a complete measure of health."
            )
        }

    //Calorie goal input. (Input)
        OutlinedTextField(
            value = dailyGoal,
            onValueChange = { dailyGoal = it },
            label = { Text("Calories Goal") },
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
        //Calories remaining (output/calculation)
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