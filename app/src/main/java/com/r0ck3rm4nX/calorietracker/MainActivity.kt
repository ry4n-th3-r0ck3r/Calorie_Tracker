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
import androidx.compose.material3.RadioButton
import androidx.compose.foundation.layout.Row
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.room.Room
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            CalorieDatabase::class.java,
            "calorie_database"
        ).build()

        val dailyRecordDao = database.dailyRecordDao()

        setContent {
            CalorieTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalorieTrackerScreen(
                        dailyRecordDao = dailyRecordDao,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CalorieTrackerScreen(
    dailyRecordDao: DailyRecordDao,
    modifier: Modifier = Modifier
) {

    var dailyGoal by remember { mutableStateOf("2500") }
    var consumed by remember { mutableStateOf("0") }
    var burned by remember { mutableStateOf("0") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var recordLoaded by remember { mutableStateOf(false) }

    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    LaunchedEffect(today) {
        val record = dailyRecordDao.getByDate(today)

        if (record != null) {
            dailyGoal = record.calorieGoal.toString()
            consumed = record.caloriesConsumed.toString()
            burned = record.caloriesBurned.toString()
            weight = record.weight?.toString() ?: ""
            height = record.height?.toString() ?: ""
            age = record.age?.toString() ?: ""
            sex = record.sex ?: ""
        }
        recordLoaded = true
    }

    LaunchedEffect(
        dailyGoal,
        consumed,
        burned,
        weight,
        height,
        age,
        sex,
        recordLoaded
    ) {
        if (recordLoaded) {
            val record = DailyRecord(
                date = today,
                calorieGoal = dailyGoal.toIntOrNull() ?: 0,
                caloriesConsumed = consumed.toIntOrNull() ?: 0,
                caloriesBurned = burned.toIntOrNull() ?: 0,
                weight = weight.toDoubleOrNull(),
                height = height.toDoubleOrNull(),
                age = age.toIntOrNull(),
                sex = sex.ifBlank { null }
            )

            dailyRecordDao.save(record)
        }
    }

    val focusManager = LocalFocusManager.current

    val goalNumber = dailyGoal.toIntOrNull() ?: 0
    val consumedNumber = consumed.toIntOrNull() ?: 0
    val burnedNumber = burned.toIntOrNull() ?: 0
    val remaining = goalNumber - consumedNumber + burnedNumber
    val weightNumber = weight.toDoubleOrNull()
    val heightNumber = height.toDoubleOrNull()
    val ageNumber = age.toIntOrNull()
    val weightKg = weightNumber?.times(0.45359237)
    val heightCm = heightNumber?.times(2.54)

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

//Calculate BMR
    val bmr = if (
        weightKg != null &&
        heightCm != null &&
        ageNumber != null &&
        weightKg > 0 &&
        heightCm > 0 &&
        ageNumber > 0
    ) {
        when (sex.lowercase()) {
            "male" -> 10 * weightKg + 6.25 * heightCm - 5 * ageNumber + 5
            "female" -> 10 * weightKg + 6.25 * heightCm - 5 * ageNumber - 161
            else -> null
        }
    } else {
        null
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(text = "Calorie Tracker")
        Text(text = "Today: $today")
        Text(text = "BMI (Optional)")

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

        Text(text = "BMR (Optional)")

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age (optional)") },
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

        Text(text = "Sex used for BMR equation")

        Row {
            RadioButton(
                selected = sex == "male",
                onClick = { sex = "male" }
            )

            Text(text = "Male")

            RadioButton(
                selected = sex == "female",
                onClick = { sex = "female" }
            )

            Text(text = "Female")
        }

        if (bmr != null) {
            Text(text = "Estimated BMR: %.0f kcal/day".format(bmr))

            Text(
                text = "BMR is estimated using the Mifflin-St Jeor equation. " +
                        "This calculation estimates resting energy expenditure using " +
                        "weight, height, age, and sex. Actual energy requirements may vary."
            )
        }

        Text(text = "Calories")

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

