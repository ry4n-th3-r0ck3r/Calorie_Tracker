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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.util.TimeZone
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import java.util.Calendar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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


fun isValidDecimalInput(value: String): Boolean {
    return value.isEmpty() ||
            (value.count { it == '.' } <= 1 &&
                    value.all { it.isDigit() || it == '.' })
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
    var showDatePicker by remember { mutableStateOf(false) }

    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    var selectedDate by remember { mutableStateOf(today) }

    val displayDate = remember(selectedDate) {
        val storageFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val displayFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)

        storageFormat.parse(selectedDate)?.let {
            displayFormat.format(it)
        } ?: selectedDate
    }

    fun changeDateBy(days: Int) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val currentDate = formatter.parse(selectedDate) ?: return

        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, days)

        selectedDate = formatter.format(calendar.time)
    }

    LaunchedEffect(selectedDate) {

        recordLoaded = false

        val record = dailyRecordDao.getByDate(selectedDate)

        if (record != null) {
            dailyGoal = record.calorieGoal.toString()
            consumed = record.caloriesConsumed.toString()
            burned = record.caloriesBurned.toString()
            weight = record.weight?.toString() ?: ""
            height = record.height?.toString() ?: ""
            age = record.age?.toString() ?: ""
            sex = record.sex ?: ""
        } else {
            dailyGoal = "2500"
            consumed = "0"
            burned = "0"
            weight = ""
            height = ""
            age = ""
            sex = ""
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

            delay(500.milliseconds)

            val record = DailyRecord(
                date = selectedDate,
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
    val weightError = weight.isNotEmpty() &&
            (weightNumber == null || weightNumber <= 0)
    val heightNumber = height.toDoubleOrNull()
    val heightError = height.isNotEmpty() &&
            (heightNumber == null || heightNumber <= 0)
    val ageNumber = age.toIntOrNull()
    val ageError = age.isNotEmpty() &&
            (ageNumber == null || ageNumber <= 0)
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
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Text(
            text = "Calorie Tracker",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { changeDateBy(-1) }
            ) {
                Text(
                    text = "‹",
                    fontSize = 32.sp
                )
            }

            TextButton(
                onClick = { showDatePicker = true }
            ) {
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = { changeDateBy(1) },
                enabled = selectedDate < today
            ) {
                Text(
                    text = "›",
                    fontSize = 32.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

//Calorie Card Display
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "REMAINING",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = "$remaining",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CalorieSummaryItem("Goal", goalNumber)
                    CalorieSummaryItem("Consumed", consumedNumber)
                    CalorieSummaryItem("Burned", burnedNumber)
                }
            }
        }

        Text(
            text = "Daily Calories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Calorie goal input. (Input)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = dailyGoal,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    dailyGoal = newValue
                }
            },
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
            modifier = Modifier.fillMaxWidth(),
            value = consumed,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    consumed = newValue
                }
            },
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
            modifier = Modifier.fillMaxWidth(),
            value = burned,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    burned = newValue
                }
            },
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Body Information",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Optional",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = weight,
            onValueChange = { newValue ->
                if (isValidDecimalInput(newValue)) {
                    weight = newValue
                }
            },
            label = { Text("Weight (lb)") },
            isError = weightError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
        if (weightError) {
            Text("Weight must be greater than 0.")
        }

    //Current height (Input)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = height,
            onValueChange = { newValue ->
                if (isValidDecimalInput(newValue)) {
                    height = newValue
                }
            },
            label = { Text("Height (inches)") },
            isError = heightError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
        if (heightError) {
            Text("Height must be greater than 0.")
        }

    //BMI output
        if (bmi != null) {
            Text(text = "BMI: %.1f".format(bmi))

            Text(
                text = "BMI is a general screening measurement based only on height and weight. " +
                        "It does not account for factors such as muscle mass, body composition, " +
                        "or individual health and should not be treated as a complete measure of health."
            )
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = age,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    age = newValue
                }
            },
            label = { Text("Age in years (optional)") },
            isError = ageError,
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
        if (ageError) {
            Text("Age must be greater than 0.")
        }

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



    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            formatter.timeZone = TimeZone.getTimeZone("UTC")

                            selectedDate = formatter.format(Date(millis))
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@Composable
fun CalorieSummaryItem(
    label: String,
    value: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

