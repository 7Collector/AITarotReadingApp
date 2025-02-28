package seven.collector.aitarotreadingapp.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import seven.collector.aitarotreadingapp.R
import seven.collector.aitarotreadingapp.TarotApp
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.theme.components.Background
import seven.collector.aitarotreadingapp.theme.components.CrystalBall
import seven.collector.aitarotreadingapp.theme.utilities.Typography
import seven.collector.aitarotreadingapp.theme.utilities.containerColor
import seven.collector.aitarotreadingapp.theme.utilities.headingsColor

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory((LocalContext.current.applicationContext as TarotApp).database.readingDao())),
) {

    val previousReadings by viewModel.previousReadings.collectAsState()
    var inputMode by remember { viewModel.inputMode }

    Background {
        Column {
            TopBar()
            CrystalBallPart(inputMode, onInputModeChange = {
                inputMode = it
                val reading = Reading(
                    id = 0,
                    question = "",
                    aiInterpretation = "",
                    date = "",
                    title = "",
                    cards = emptyList(),
                    chats = emptyList()
                )
                var id = 0
                viewModel.addNewReading(reading, onResult = { i -> id = i.toInt() })
                Log.d("HomeScreen", "Reading iD: $id")
                navController.navigate("cards/${id}")
            })
            if (previousReadings.isNotEmpty()) {
                PreviousReadingsPart(previousReadings, navController)
            }
            if (inputMode) {
                InputBox()
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
fun CrystalBallPart(inputMode: Boolean, onInputModeChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CrystalBall(onClick = {
            if (!inputMode) {
                onInputModeChange(true)
            }
        })
        Text(
            text = if (inputMode) stringResource(id = R.string.listening) else stringResource(id = R.string.crystal_ball_sub_heading),
            style = Typography.bodyLarge
        )
    }
}

@Composable
fun PreviousReadingsPart(previousReadings: List<Reading>, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = R.string.heading_previous))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(previousReadings) { reading ->
                PreviousReadingCard(reading, navController)
            }
        }
    }
}

@Composable
fun PreviousReadingCard(reading: Reading, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(12.dp))
            .clickable {
                navController.navigate("cards/${reading.id}")
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(style = Typography.headlineMedium, text = reading.title)
            Text(style = Typography.bodyMedium, text = reading.date)
        }
        Text(style = Typography.bodyLarge, text = reading.question)
    }
}

@Composable
fun InputBox() {

}