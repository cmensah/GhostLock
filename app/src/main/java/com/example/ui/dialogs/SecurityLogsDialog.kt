package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.EventType
import com.example.data.model.SecurityEvent
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberOutline
import com.example.ui.theme.CyberRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecurityLogsDialog(
  events: List<SecurityEvent>,
  onClearLogs: () -> Unit,
  onDismiss: () -> Unit
) {
  val dateFormat = SimpleDateFormat("HH:mm:ss - MMM dd", Locale.getDefault())

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("security_logs_dialog"),
      colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
      shape = RoundedCornerShape(20.dp),
      border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberOutline))
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Security Audit Trail", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
          }

          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_logs_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Text(
          text = "Chronological record of lock activations, successful entries, and tamper/failure detections.",
          color = TextSecondary,
          fontSize = 12.sp,
          modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (events.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .background(CyberDarkSurfaceVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text("No security events recorded yet.", color = TextSecondary, fontSize = 13.sp)
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .height(280.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(events) { event ->
              val (color, title) = when (event.eventType) {
                EventType.ARMED -> CyberCyanPrimary to "Device Armed"
                EventType.DISARMED -> TextSecondary to "Device Disarmed"
                EventType.UNLOCKED_SYMBOL -> CyberGreen to "Unlocked via Symbol"
                EventType.UNLOCKED_TAPS -> CyberGreen to "Unlocked via Taps"
                EventType.UNLOCKED_QUADRANTS -> CyberGreen to "Unlocked via Quadrants"
                EventType.UNLOCKED_FAILSAFE_PIN -> CyberAmber to "Unlocked via Failsafe PIN"
                EventType.FAILED_ATTEMPT -> CyberRed to "Tamper / Failed Unlock"
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(CyberDarkSurfaceVariant, RoundedCornerShape(10.dp))
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = title,
                    color = color,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = event.details,
                    color = TextSecondary,
                    fontSize = 11.sp
                  )
                }

                Text(
                  text = dateFormat.format(Date(event.timestamp)),
                  color = TextSecondary.copy(alpha = 0.7f),
                  fontSize = 10.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (events.isNotEmpty()) {
          OutlinedButton(
            onClick = onClearLogs,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("clear_logs_button")
          ) {
            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Logs", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Clear Event History", color = TextSecondary, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
