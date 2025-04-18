package com.example.testapp.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testapp.ui.theme.BorderPrimaryColor

@Preview(widthDp = 320)
@Composable
fun SurfaceField() {
    Surface(
        content = { Text("P*&G+QRF, Sama marga, Kathmandu, 44600 Nepal, 203923 Baneshwor", style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            lineHeight = 18.sp) },
        border = BorderStroke(1.dp, BorderPrimaryColor),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
public fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onChange: (String) -> Unit,
    borderColor: Color = BorderPrimaryColor,
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    label: String = "",
    required: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minHeight: Dp = 48.dp
) {
    Column(verticalArrangement = Arrangement.spacedBy(1.5.dp)) {
        Row {
            Text(
                text = label, style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 3.dp)
            )
            if (required) Text(text = "*", color = Color.Red)
        }

        TextField(
            value = value,
            onValueChange = onChange,
            keyboardOptions = keyboardOptions,
            leadingIcon = leadingIcon?.let { { leadingIcon() } },
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = modifier
                .border(
                    border = BorderStroke(1.dp, borderColor),
                    shape = RoundedCornerShape(7.dp)
                )
                .heightIn(min = minHeight)
                .fillMaxWidth(),
            singleLine = singleLine,
            maxLines = maxLines,
        )
    }
}
