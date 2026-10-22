package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.CyberRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PinConfigDialog(
  currentPin: String,
  onDismiss: () -> Unit,
  onSavePin: (String) -> Unit
) {
  var newPin by remember { mutableStateOf("") }
  var confirmPin by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("pin_config_dialog"),
      colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
      shape = RoundedCornerShape(20.dp),
      border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Key, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Set Emergency Master PIN", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
          }
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_pin_config")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Text(
          text = "Enter a 4-8 digit emergency PIN used to recover from stealth lockdown if you forget your secret gesture or pattern.",
          color = TextSecondary,
          fontSize = 12.sp,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = newPin,
          onValueChange = { if (it.length <= 8 && it.all { c -> c.isDigit() }) newPin = it },
          label = { Text("New PIN (4-8 digits)") },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_new_pin"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyanPrimary,
            unfocusedBorderColor = CyberOutline,
            focusedContainerColor = CyberDarkSurfaceVariant,
            unfocusedContainerColor = CyberDarkSurfaceVariant,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = confirmPin,
          onValueChange = { if (it.length <= 8 && it.all { c -> c.isDigit() }) confirmPin = it },
          label = { Text("Confirm PIN") },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_confirm_pin"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyanPrimary,
            unfocusedBorderColor = CyberOutline,
            focusedContainerColor = CyberDarkSurfaceVariant,
            unfocusedContainerColor = CyberDarkSurfaceVariant,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        if (errorText != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = errorText!!, color = CyberRed, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            if (newPin.length < 4) {
              errorText = "PIN must be at least 4 digits"
            } else if (newPin != confirmPin) {
              errorText = "PINs do not match"
            } else {
              onSavePin(newPin)
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("save_pin_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = CyberCyanPrimary,
            contentColor = Color.Black
          )
        ) {
          Text("Update Master PIN", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
