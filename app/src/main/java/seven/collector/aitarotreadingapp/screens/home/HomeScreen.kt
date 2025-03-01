package seven.collector.aitarotreadingapp.screens.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import seven.collector.aitarotreadingapp.R
import seven.collector.aitarotreadingapp.TarotApp
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.helpers.SpeechToTextHelper
import seven.collector.aitarotreadingapp.theme.components.Background
import seven.collector.aitarotreadingapp.theme.components.CrystalBall
import seven.collector.aitarotreadingapp.theme.utilities.Typography
import seven.collector.aitarotreadingapp.theme.utilities.containerColor
import seven.collector.aitarotreadingapp.theme.utilities.headingsColor
import seven.collector.aitarotreadingapp.theme.utilities.primaryColor
import seven.collector.aitarotreadingapp.theme.utilities.textColor

@Composable
fun HomeScreen(
    speechToTextHelper: SpeechToTextHelper,
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory((LocalContext.current.applicationContext as TarotApp).database.readingDao(), speechToTextHelper)),
) {

    val previousReadings by viewModel.previousReadings.collectAsState()
    val inputMode by viewModel.inputMode.collectAsState()
    val listeningMode by viewModel.listeningMode.collectAsState()
    val context = LocalContext.current
    val activity = remember { context as? Activity }

    BackHandler(enabled = inputMode) {
        viewModel.stopSTT()
        viewModel.setInputMode(false)
    }

    Background {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TopBar()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CrystalBall(onClick = {
                    if (!inputMode) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity!!,
                                arrayOf(Manifest.permission.RECORD_AUDIO),
                                1001
                            )
                        } else {
                            Log.d("HomeScreen", "Starting Speech Recognition")
                            viewModel.startListening()
                        }
                    } else if (!listeningMode) {
                        viewModel.startListeningAgain()
                    }
                })
                if (listeningMode || !inputMode) {
                    Text(
                        text = if (inputMode) stringResource(id = R.string.listening) else stringResource(
                            id = R.string.crystal_ball_sub_heading
                        ),
                        style = Typography.headlineMedium,
                        color = textColor
                    )
                }
            }

            if (previousReadings.isNotEmpty() && !inputMode) {
                PreviousReadingsPart(previousReadings, navController)
            }
            if (inputMode) {
                InputBox(
                    viewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun TopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(50.dp),
            painter = painterResource(id = R.drawable.avatar),
            contentDescription = stringResource(
                id = R.string.avatar_content_description
            )
        )
        Text(
            style = Typography.headlineMedium,
            color = headingsColor,
            text = stringResource(R.string.gracias)
        )
        Text(style = Typography.bodyMedium, text = stringResource(R.string.homeSubText))
    }
}

@Composable
fun PreviousReadingsPart(previousReadings: List<Reading>, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.heading_previous),
                style = Typography.headlineMedium
            )
            Image(modifier = Modifier
                .size(24.dp)
                .clickable {
                    navController.navigate("previous")
                }
                .rotate(180f),
                painter = painterResource(R.drawable.back),
                contentDescription = stringResource(R.string.all_readings_button))
        }

        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(previousReadings) { reading ->
                PreviousReadingCard(reading, navController)
            }
        }
    }
}

@Composable
fun PreviousReadingCard(reading: Reading, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(12.dp))
            .clickable {
                navController.navigate("reading/${reading.id}")
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(style = Typography.bodyLarge, text = reading.title)
            // Text(style = Typography.bodyMedium, text = reading.date)
        }
        Text(style = Typography.bodyMedium, text = reading.question)
    }
}

@Composable
fun InputBox(viewModel: HomeViewModel, navController: NavController) {
    var userInput by remember { viewModel.speechText }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = userInput,
                onValueChange = {
                    userInput = it
                    if (viewModel.listeningMode.value) {
                        viewModel.stopSTT()
                    }
                },
                placeholder = { Text("Speak or type your question...", color = textColor.copy(alpha = 0.6f)) },
                modifier = Modifier
                    .weight(1f)
                    .background(containerColor, RoundedCornerShape(8.dp)),
                textStyle = Typography.bodyLarge.copy(color = textColor),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    disabledTextColor = textColor.copy(alpha = 0.5f),
                    errorTextColor = textColor,

                    focusedContainerColor = containerColor, // ✅ Background when focused
                    unfocusedContainerColor = containerColor, // ✅ Background when not focused
                    disabledContainerColor = containerColor,
                    errorContainerColor = containerColor,

                    cursorColor = primaryColor,
                    errorCursorColor = primaryColor,
                    selectionColors = TextSelectionColors(primaryColor, primaryColor.copy(alpha = 0.4f)),

                    focusedIndicatorColor = primaryColor, // ✅ Focused border color
                    unfocusedIndicatorColor = textColor.copy(alpha = 0.4f), // ✅ Unfocused border color
                    disabledIndicatorColor = textColor.copy(alpha = 0.2f),
                    errorIndicatorColor = primaryColor,

                    focusedTrailingIconColor = primaryColor,
                    unfocusedTrailingIconColor = textColor,
                    disabledTrailingIconColor = textColor.copy(alpha = 0.4f),
                    errorTrailingIconColor = primaryColor
                )
            )

            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(primaryColor, RoundedCornerShape(32.dp)) // Full corner radius
                    .clickable {
                        if (userInput.isNotEmpty()) {
                            viewModel.updateSpeechText(userInput)
                            val reading = Reading(
                                id = 0,
                                question = userInput,
                                aiInterpretation = "AI Interpretation",
                                date = "Day",
                                title = "Title",
                                cards = emptyList(),
                                chats = emptyList()
                            )

                            viewModel.addNewReading(reading) { i ->
                                val id = i.toInt()
                                Log.d("HomeScreen", "Reading ID after insertion: $id")
                                navController.navigate("cards/$id")
                            }
                            userInput = "" // Clear input after sending
                            viewModel.setInputMode(false)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.send), // send.png
                    contentDescription = "Send",
                    modifier = Modifier.size(24.dp) // Adjust icon size
                )
            }
        }
    }
}