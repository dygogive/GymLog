package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.ui.feature.workout.model.ResultOfSet
import com.example.gymlog.ui.theme.MyAppTheme

@Composable
fun LogResultDialog(
    result: ResultOfSet? = null,
    onDismiss: () -> Unit,
    onConfirmResult: (ResultOfSet) -> Unit
) {

    var resultId: Long? = null
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var secs by remember { mutableStateOf("") }

    if (result != null){
        resultId = result.id
        reps = (result.iteration?: "-").toString()
        weight = (result.weight?: "-").toString()
        secs = (result.workTime?: "-").toString()
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = stringResource(R.string.write_results),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                SubtleTextField(
                    value = secs,
                    onValueChange = { secs = it },
                    label = stringResource(R.string.work_time),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(Modifier.height(16.dp))

                SubtleTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = stringResource(R.string.work_weight),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(Modifier.height(16.dp))

                SubtleTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = stringResource(R.string.work_reps),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirmResult(
                                ResultOfSet(
                                    id = resultId,
                                    weight = weight.toIntOrNull(),
                                    iteration = reps.toIntOrNull(),
                                    workTime = secs.toIntOrNull()
                                )
                            )
                            onDismiss()
                        }
                    )
                )
            }
        },
        confirmButton = {
            SubtleTextButton(
                text = stringResource(R.string.ok),
                onClick = {
                    onConfirmResult(
                        ResultOfSet(
                            id = resultId,
                            weight = weight.toIntOrNull(),
                            iteration = reps.toIntOrNull(),
                            workTime = secs.toIntOrNull()
                        )
                    )
                    onDismiss()
                },
                isPrimary = true
            )
        },
        dismissButton = {
            SubtleTextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                isPrimary = false
            )
        }
    )
}

@Composable
private fun SubtleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            tonalElevation = 0.dp
        ) {
            androidx.compose.material3.TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SubtleTextButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    val textColor = if (isPrimary) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }

    TextButton(
        onClick = onClick
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isPrimary) FontWeight.Medium else FontWeight.Normal
            )
        )
    }
}

@Preview
@Composable
fun LogResultDialogPreview() {
    MyAppTheme(useDarkTheme = false) {
        LogResultDialog(
            onDismiss = {},
            onConfirmResult = {}
        )
    }
}