package seven.collector.aitarotreadingapp.screens.card

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import seven.collector.aitarotreadingapp.TarotApp
import seven.collector.aitarotreadingapp.database.models.Card
import seven.collector.aitarotreadingapp.theme.components.Background
import seven.collector.aitarotreadingapp.theme.components.CrystalBall
import seven.collector.aitarotreadingapp.theme.utilities.Typography
import seven.collector.aitarotreadingapp.theme.utilities.backgroundColor
import seven.collector.aitarotreadingapp.theme.utilities.primaryColor
import java.io.IOException
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CardScreen(
    id: Int,
    navController: NavController,
    viewModel: CardViewModel = viewModel(
        factory = CardViewModelFactory(
            id,
            (LocalContext.current.applicationContext as TarotApp).database.readingDao()
        )
    )
) {
    val context = LocalContext.current
    val cards = remember { loadCards(context) }
    val backImage = remember { loadImageFromAssets(context, "backside.png") }
    val selectedCards = remember { mutableIntStateOf(0) }
    val selectedCardImages = remember { mutableStateListOf<Bitmap>() }
    var aiInterpretationCompleted by remember { mutableStateOf(false) }
    val rotationAngle = remember { Animatable(0f) }

    // Start the rotation animation when 3 cards are selected.
    LaunchedEffect(selectedCards.value) {
        if (selectedCards.value == 3) {
            // This loop will rotate the loader indefinitely.
            while (true) {
                rotationAngle.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
                )
                rotationAngle.snapTo(0f)
            }
        }
    }

    // When both conditions are met, delay a little and then navigate.
    LaunchedEffect(selectedCards.value, aiInterpretationCompleted) {
        if (selectedCards.value == 3 && aiInterpretationCompleted) {
            kotlinx.coroutines.delay(1000) // optional delay to let users see the loader state
            navController.navigate("reading/$id") {
                popUpTo("cards/$id") { inclusive = true }
            }
        }
    }

    Background {
        if (selectedCards.value == 3) {
            // Show animated loader with the rotating cards.
            AnimatedTarotCards(cards = selectedCardImages, rotationAngle = rotationAngle)
        } else {
            // Card selection UI.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)  // use a smaller spacing if desired
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select 3 Cards", style = Typography.displayLarge
                    )
                    LinearProgressIndicator(
                        progress = { selectedCards.value / 3f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                shape = RoundedCornerShape(16.dp),
                                color = backgroundColor
                            ),
                        color = primaryColor,
                    )
                }
                // Use LazyVerticalGrid with an Adaptive cell size so items wrap nicely
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 102.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp), // reduced vertical gap
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cards) { card ->
                        val frontImage = loadImageFromAssets(context, card.fileName)
                        TarotCard(
                            key = card.name, // Use a stable key so that state is preserved
                            frontImage = frontImage,
                            backImage = backImage,
                            onCardSelected = {
                                if (selectedCards.value < 3) {
                                    selectedCards.value++
                                    selectedCardImages.add(frontImage)
                                    viewModel.selectCard(card)
                                    if (selectedCards.value == 3) {
                                        viewModel.sendMessage { _, _ ->
                                            aiInterpretationCompleted = true
                                        }
                                    }
                                }
                            },
                            selected = selectedCards.value
                        )
                    }
                }
            }
        }
    }
}



fun loadCards(context: Context): List<Card> {
    return try {
        val json = context.assets.open("cards.json").bufferedReader().use { it.readText() }
        val jsonObject = Gson().fromJson(json, Map::class.java)
        val cardsArray = jsonObject["cards"] as List<Map<String, Any>>
        val shuffledCards = cardsArray.map { cardMap ->
            Card(
                name = cardMap["name"] as String,
                fileName = cardMap["img"] as String
            )
        }.shuffled() // Shuffle the list before returning
        shuffledCards
    } catch (e: IOException) {
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
    key: String,
    frontImage: Bitmap,
    backImage: Bitmap,
    onCardSelected: () -> Unit,
    selected: Int,
    flipped: Boolean = false
) {
    var isFlipped by rememberSaveable(key = key) { mutableStateOf(flipped) }
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

@Composable
fun AnimatedTarotCards(cards: List<Bitmap>, rotationAngle: Animatable<Float, AnimationVector1D>) {
    val radius = 250f
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CrystalBall()

        cards.forEachIndexed { index, cardImage ->
            val angleOffset = 120f * index // Distribute cards evenly (3 cards → 120° apart)
            val angleInRadians = (rotationAngle.value + angleOffset) * (Math.PI / 180).toFloat()

            val xOffset = radius * cos(angleInRadians)
            val yOffset = radius * sin(angleInRadians)

            val yRotation = -(rotationAngle.value + angleOffset) // Invert to simulate orbiting

            Image(
                bitmap = cardImage.asImageBitmap(),
                contentDescription = "Rotating Tarot Card",
                modifier = Modifier
                    .size(100.dp, 150.dp)
                    .graphicsLayer {
                        translationX = xOffset
                        translationY = yOffset
                        rotationY = yRotation
                        cameraDistance = 8 * density // Enhances 3D depth effect
                        transformOrigin = TransformOrigin(0.5f, 0.5f) // Rotate from center
                    }
            )
        }
    }
}

