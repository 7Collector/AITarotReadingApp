package seven.collector.aitarotreadingapp.screens.card

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.TarotApp
import seven.collector.aitarotreadingapp.database.models.Card
import seven.collector.aitarotreadingapp.helpers.tarotChat
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
    val selectedCards = remember { mutableStateOf(0) }
    val selectedCardImages = remember { mutableStateListOf<Bitmap>() }
    val rotationAngle = remember { Animatable(0f) }
    val transitionStarted = remember { mutableStateOf(false) }

    LaunchedEffect(selectedCards.value) {
        if (selectedCards.value == 3) {
            transitionStarted.value = true
            rotationAngle.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
            )
            navController.navigate("reading/$id") {
                popUpTo("cards/$id") { inclusive = true }
            }
        }
    }


    Background {
        if (transitionStarted.value) {
            AnimatedTarotCards(cards = selectedCardImages, rotationAngle = rotationAngle)
        } else {
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
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items((cards.size + 5) / 6) { rowIndex ->
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val startIndex = rowIndex * 6
                            val endIndex = minOf(startIndex + 6, cards.size)
                            items(endIndex - startIndex) { index ->
                                val card = cards[startIndex + index]
                                val frontImage = loadImageFromAssets(context, card.fileName)

                                TarotCard(
                                    frontImage = frontImage,
                                    backImage = backImage,
                                    onCardSelected = {
                                        if (selectedCards.value < 3) {
                                            selectedCards.value++
                                            selectedCardImages.add(frontImage)
                                            viewModel.selectCard(card)
                                            if (selectedCards.value == 3) {
                                                transitionStarted.value = true
                                                viewModel.saveReadingWithCards()
                                                viewModel.viewModelScope.launch {
                                                    try {
                                                        val reading = viewModel.reading
                                                        val question = reading.value?.question ?: ""
                                                        val selectedCards =
                                                            viewModel.getSelectedCards().toString()
                                                        val sendData = mapOf(
                                                            "question" to question,
                                                            "selectedCards" to selectedCards
                                                        )
                                                        val jsonData = Gson().toJson(sendData)
                                                        val response = tarotChat.sendMessage(
                                                            jsonData
                                                        )
                                                        val responseString =
                                                            response.text
                                                                ?: "" // Ensure it's a JSON string
                                                        Log.d("AIResponse", responseString)
                                                        val responseMap: Map<String, String> =
                                                            Gson().fromJson(
                                                                responseString,
                                                                object :
                                                                    TypeToken<Map<String, String>>() {}.type
                                                            )
                                                        val title =
                                                            responseMap["title"] ?: "Unknown Title"
                                                        val interpretation =
                                                            responseMap["interpretation"]
                                                                ?: "No interpretation available"
                                                        viewModel.saveReadingWithAI(
                                                            title,
                                                            interpretation
                                                        )
                                                        navController.navigate("reading/$id") {
                                                            popUpTo("cards/${id}") {
                                                                inclusive = true
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e(
                                                            "TarotChat",
                                                            "Error sending message",
                                                            e
                                                        )
                                                    }
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
    }
}


fun loadCards(context: Context): List<Card> {
    return try {
        val json = context.assets.open("cards.json").bufferedReader().use { it.readText() }
        val jsonObject = Gson().fromJson(json, Map::class.java)
        val cardsArray = jsonObject["cards"] as List<Map<String, Any>>
        cardsArray.map { cardMap ->
            Log.d("AAA", cardMap["img"].toString())
            Card(
                name = cardMap["name"] as String,
                fileName = cardMap["img"] as String
            )
        }
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList()
    }
}


fun loadImageFromAssets(context: Context, fileName: String): Bitmap {
    Log.d("LoadImageFromAssets", "Loading image from assets: $fileName")
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

