package com.example.gymlog.ui.feature.workout.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.core.utils.getCurrentDateTime
import com.example.gymlog.ui.theme.MyAppTheme

//OK
@Composable
fun LogResultDialog(
    onDismiss: () -> Unit,
    onConfirmResult: (ResultOfSet) -> Unit
) {
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var secs by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        title = { Text("Запис результату") },
        text = {
            Column {
                OutlinedTextField(
                    value = secs,
                    onValueChange = { secs = it },
                    label = { Text(stringResource(R.string.work_time)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text(stringResource(R.string.work_weight)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text(stringResource(R.string.work_reps)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirmResult(
                                ResultOfSet(
                                    weight = weight.toIntOrNull(),
                                    iteration = reps.toIntOrNull(),
                                    workTime = secs.toIntOrNull(),
                                )
                            )
                            onDismiss() // Викликаємо функцію, а не просто посилаємось
                        }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmResult(
                    ResultOfSet(
                        weight = weight.toIntOrNull(),
                        iteration = reps.toIntOrNull(),
                        workTime = secs.toIntOrNull(),
                    )
                )
                onDismiss() // 🔥 Викликаємо функцію
            }) { Text("ОК") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { // Тут уже правильно, бо onClick сам викликає функцію
                Text("Скасувати")
            }
        }
    )
}




@Preview
@Composable
fun LogResultDialogPreview() {
    MyAppTheme(useDarkTheme = false) {
        LogResultDialog(
            onDismiss = {},
            {}
        )
    }
}

