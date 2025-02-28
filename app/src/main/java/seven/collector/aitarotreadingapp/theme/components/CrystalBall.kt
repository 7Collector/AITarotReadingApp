package seven.collector.aitarotreadingapp.theme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import seven.collector.aitarotreadingapp.theme.utilities.glowColor
import seven.collector.aitarotreadingapp.theme.utilities.primaryColor
import seven.collector.aitarotreadingapp.theme.utilities.radialStartColor

@Composable
fun CrystalBall(onClick: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize().clickable { onClick() }, contentAlignment = Alignment.Center) {
        // Main Circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.radialGradient(
                colors = listOf(radialStartColor, primaryColor, Color.Black),
                center = center,
                radius = 100.dp.toPx()
            )

            drawCircle(
                brush = gradient,
                radius = 100.dp.toPx(),
                center = center
            )
        }

        // Glow Circle
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(30.dp)
        ) {
            drawCircle(
                color = glowColor.copy(alpha = 0.6f),
                radius = 110.dp.toPx(),
                center = center
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun CrystalBallPreview() {
    CrystalBall()
}
