package edu.cit.sarismart.features.auth.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill

@Composable
fun BackgroundPattern(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // top right curved shape
            val path1 = Path().apply {
                moveTo(width * 0.7f, 0f)
                cubicTo(
                    width * 0.8f, height * 0.1f,
                    width * 1.1f, height * 0.05f,
                    width, height * 0.3f
                )
                lineTo(width, 0f)
                close()
            }
            drawPath(path1, primaryColor, style = Fill)

            // bottom left blob
            val path2 = Path().apply {
                moveTo(0f, height * 0.7f)
                cubicTo(
                    width * 0.3f, height * 0.9f,
                    width * 0.6f, height * 1.1f,
                    width, height
                )
                lineTo(0f, height)
                close()
            }
            drawPath(path2, primaryColor, style = Fill)

            // middle right accent
            val path3 = Path().apply {
                moveTo(width * 0.85f, height * 0.45f)
                cubicTo(
                    width * 0.9f, height * 0.4f,
                    width * 1.1f, height * 0.5f,
                    width, height * 0.55f
                )
                lineTo(width, height * 0.45f)
                close()
            }
            drawPath(path3, secondaryColor, style = Fill)

            // draw scattered circles
            val circlePositions = listOf(
                // top-left
                Offset(width * 0.1f, height * 0.1f) to 40f,
                // center
                Offset(width * 0.8f, height * 0.7f) to 60f,
                // bottom-right
                Offset(width * 0.3f, height * 0.5f) to 30f,
                // off-center
                Offset(width * 0.6f, height * 0.4f) to 25f,
                // low left
                Offset(width * 0.2f, height * 0.8f) to 45f
            )

            circlePositions.forEach { (position, radius) ->
                drawCircle(
                    color = secondaryColor,
                    radius = radius,
                    center = position
                )
            }

            // draw small dots pattern
            for (i in 0 until 40) {
                val x = (width * (0.1f + 0.8f * (i % 8) / 8f))
                val y = (height * (0.1f + 0.8f * (i / 8) / 5f))
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.3f),
                    radius = 3f,
                    center = Offset(x, y)
                )
            }
        }
    }
}