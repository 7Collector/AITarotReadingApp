package seven.collector.aitarotreadingapp.screens.card

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import seven.collector.aitarotreadingapp.theme.components.Background
import seven.collector.aitarotreadingapp.theme.utilities.Typography
import seven.collector.aitarotreadingapp.theme.utilities.backgroundColor
import seven.collector.aitarotreadingapp.theme.utilities.primaryColor

@Composable
fun CardScreen(id: String, navController: NavController) {
    val context = LocalContext.current
    val cardImages = remember { loadCardImages(context) }
    val backImage = remember { loadImageFromAssets(context, "backside.png") }
    val selectedCards = remember { mutableIntStateOf(0) }
    //val scrollState = rememberScrollState()

    Background {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select 3 Cards", style = Typography.displayLarge
                )
                LinearProgressIndicator(
                    progress = { selectedCards.intValue / 3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(16.dp),
                            color = backgroundColor
                        ),
                    color = primaryColor,
                )

            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items((cardImages.size + 5) / 6) { rowIndex ->
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val startIndex = rowIndex * 6
                        val endIndex = minOf(startIndex + 6, cardImages.size)
                        items(endIndex - startIndex) { index ->
                            TarotCard(
                                frontImage = cardImages[startIndex + index],
                                backImage = backImage,
                                onCardSelected = {
                                    if (selectedCards.intValue < 3) {
                                        selectedCards.intValue++
                                        if (selectedCards.intValue == 3) {
                                            navController.navigate("reading/${id}")
                                        }
                                    }
                                },
                                selected = selectedCards.intValue
                            )
                        }
                    }
                }
            }
        }
    }

}


fun loadCardImages(context: Context): List<Bitmap> {
    return try {
        context.assets.list("cards")?.map { fileName ->
            context.assets.open("cards/$fileName").use { BitmapFactory.decodeStream(it) }
        } ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun loadImageFromAssets(context: Context, fileName: String): Bitmap {
    // Log.d("LoadImageFromAssets", "Loading image from assets: $fileName")
    return context.assets.open("cards/$fileName").use { BitmapFactory.decodeStream(it) }
}

@Composable
fun TarotCard(
    frontImage: Bitmap,
    backImage: Bitmap,
    onCardSelected: () -> Unit,
    selected: Int,
    flipped: Boolean = false
) {
    var isFlipped by remember { mutableStateOf(flipped) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isFlipped) {
        rotation.animateTo(
            targetValue = if (isFlipped) 180f else 0f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    val image = if (rotation.value <= 90f) backImage else frontImage

    Box(
        modifier = Modifier
            .size(102.dp, 153.dp)
            .clickable {
                if (!isFlipped && selected < 3) {
                    isFlipped = true
                    onCardSelected()
                }
            }
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
            }
    ) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "Tarot Card",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationY = -180f },
            contentScale = ContentScale.Fit
        )
    }
}