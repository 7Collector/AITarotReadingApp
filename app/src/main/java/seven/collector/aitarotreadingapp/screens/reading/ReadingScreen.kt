package seven.collector.aitarotreadingapp.screens.reading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import seven.collector.aitarotreadingapp.theme.utilities.Typography
import seven.collector.aitarotreadingapp.theme.utilities.primaryColor

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
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Top bar
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        modifier = Modifier.size(24.dp),
                        contentDescription = stringResource(
                            R.string.back_button
                        )
                    )
                    Text(text = currentReading.title, style = Typography.displayLarge, modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.delete),
                        modifier = Modifier.size(24.dp),
                        contentDescription = stringResource(
                            R.string.delete_button
                        )
                    )
                }
                // Question and answer
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = currentReading.question, style = Typography.bodyLarge)
                    Text(text = currentReading.aiInterpretation, style = Typography.bodyMedium)
                }

                // Cards
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(currentReading.cards) { card ->
                        Column {
                            TarotCard(
                                loadImageFromAssets(LocalContext.current, card.fileName),
                                backImage = loadImageFromAssets(
                                    LocalContext.current,
                                    "backside.png"
                                ),
                                onCardSelected = {},
                                selected = 3,
                                flipped = true
                            )
                            Text(text = card.name)
                        }
                    }
                }

                // Chat with AI
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(primaryColor, shape = RoundedCornerShape(16.dp))
                ) {
                    Text(text = stringResource(R.string.chat_with_ai), style = Typography.bodyLarge)
                }
            }
        }
    }
}