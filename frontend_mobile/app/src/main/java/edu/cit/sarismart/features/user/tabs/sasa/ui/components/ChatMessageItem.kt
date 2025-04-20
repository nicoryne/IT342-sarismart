package edu.cit.sarismart.features.user.tabs.sasa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import edu.cit.sarismart.R
import edu.cit.sarismart.features.user.tabs.sasa.data.models.Message

@Composable
fun ChatMessageItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = if (message.isUserMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column () {
            if (!message.isUserMessage) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sasa_icon),
                    contentDescription = "Sasa Icon",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp),
                    tint = Color(0xFF009393)
                )
            }

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)),
                shadowElevation = 12.dp,
                tonalElevation = 10.dp,
                color = if (message.isUserMessage) MaterialTheme.colorScheme.primary else Color.White
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isUserMessage) Color.White else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}