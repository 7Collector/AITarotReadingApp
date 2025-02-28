package seven.collector.aitarotreadingapp.theme.utilities

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import seven.collector.aitarotreadingapp.R

val Garamond = FontFamily(
    Font(R.font.garamond_variable, FontWeight.Thin),        // 100
    Font(R.font.garamond_variable, FontWeight.ExtraLight),   // 200
    Font(R.font.garamond_variable, FontWeight.Light),        // 300
    Font(R.font.garamond_variable, FontWeight.Normal),       // 400
    Font(R.font.garamond_variable, FontWeight.Medium),       // 500
    Font(R.font.garamond_variable, FontWeight.SemiBold),     // 600
    Font(R.font.garamond_variable, FontWeight.Bold),         // 700
    Font(R.font.garamond_variable, FontWeight.ExtraBold),    // 800
    Font(R.font.garamond_variable, FontWeight.Black)         // 900
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = Garamond,
        color = headingsColor
    ),
    headlineMedium = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = Garamond,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        color = textColor
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = secondaryTextColor
    )
)