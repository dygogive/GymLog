package com.example.gymlog.ui.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gymlog.R

@Composable
fun EditDeleteButtonMenu(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {

    var contextMenuExpanded by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
        .size(24.dp)
        .alpha(0.4f)
        //on long press detection
    ){
        IconButton(
            //on click detection
            onClick = {contextMenuExpanded = true}
        ){
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.context_menu),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }



        //show menu for short click
        DropdownMenu(
            expanded = contextMenuExpanded,
            onDismissRequest = { contextMenuExpanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
        ) {
        DropdownMenuItem(
            onClick = {
                onEditClick()
                contextMenuExpanded = false
            },
            text = { Text(stringResource(R.string.edit)) },
            // Використовуємо MenuDefaults для встановлення кольорів

        )


        DropdownMenuItem(
            onClick = {
                onDeleteClick()
                contextMenuExpanded = false
            },
            text = { Text(stringResource(R.string.delete)) },

        )


    }


    }
}







