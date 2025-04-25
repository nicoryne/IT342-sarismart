package edu.cit.sarismart.features.user.tabs.stores.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.cit.sarismart.R

@Composable
fun RegisterNewStoreButton(
    onRegisterStoreClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onRegisterStoreClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFF8B8A8A),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f),
                            0f
                        )
                    ),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.dp, color = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_register_new_store),
                contentDescription = "Register New Store Icon",
                tint = Color(0xFF8B8A8A),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Register new Store as an Owner",
                color = Color(0xFF8B8A8A),
                textAlign = TextAlign.Center
            )
        }
    }
}