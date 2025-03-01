package seven.collector.aitarotreadingapp.screens.reading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import seven.collector.aitarotreadingapp.R
import seven.collector.aitarotreadingapp.TarotApp
import seven.collector.aitarotreadingapp.screens.card.TarotCard
import seven.collector.aitarotreadingapp.screens.card.loadImageFromAssets
import seven.collector.aitarotreadingapp.theme.components.Background
import seven.collector.aitarotreadingapp.theme.components.FormattedText
import seven.collector.aitarotreadingapp.theme.utilities.Typography
import seven.collector.aitarotreadingapp.theme.utilities.primaryColor
import seven.collector.aitarotreadingapp.theme.utilities.textColor

@Composable
fun ReadingScreen(
    id: Int,
    navController: NavController,
    viewModel: ReadingViewModel = viewModel(
        factory = ReadingViewModelFactory(
            id,
            (LocalContext.current.applicationContext as TarotApp).database.readingDao()
        )
    )
) {
    val reading by viewModel.reading.collectAsState()

    reading?.let { currentReading ->
        Background {
            LazyColumn(  // Changed from Column to LazyColumn for scrolling
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Top bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { navController.popBackStack() },
                            contentDescription = stringResource(R.string.back_button)
                        )
                        Text(
                            text = currentReading.title,
                            style = Typography.displayLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    viewModel.deleteReading(onResult = {
                                        navController.popBackStack()
                                    })
                                },
                            contentDescription = stringResource(R.string.delete_button)
                        )
                    }
                }

                item {
                    // Question and Answer
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = currentReading.question, style = Typography.bodyLarge)
                        FormattedText(text = currentReading.aiInterpretation, style = Typography.bodyMedium)
                    }
                }

                item {
                    // Cards (LazyRow remains for horizontal scrolling)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items(currentReading.cards) { card ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                TarotCard(
                                    loadImageFromAssets(LocalContext.current, card.fileName),
                                    backImage = loadImageFromAssets(LocalContext.current, "backside.png"),
                                    onCardSelected = {},
                                    selected = 3,
                                    flipped = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = card.name, style = Typography.bodyLarge)
                            }
                        }
                    }
                }

                item {
                    // Chat with AI Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable {
                                navController.navigate("chat/${id}")
                            }
                            .background(primaryColor, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.chat_with_ai),
                            style = Typography.headlineMedium,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}
