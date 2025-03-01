package seven.collector.aitarotreadingapp.screens.previous

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import seven.collector.aitarotreadingapp.R
import seven.collector.aitarotreadingapp.TarotApp
import seven.collector.aitarotreadingapp.screens.home.PreviousReadingCard
import seven.collector.aitarotreadingapp.theme.components.Background
import seven.collector.aitarotreadingapp.theme.utilities.Typography

@Composable
fun PreviousReadings(
    navController: NavController,
    viewModel: PreviousReadingsViewModel = viewModel(
        factory = PreviousReadingsViewModelFactory((LocalContext.current.applicationContext as TarotApp).database.readingDao())
    ),
) {

    val readings by viewModel.readings.collectAsStateWithLifecycle()

    Background {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

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
                        .clickable {
                            navController.popBackStack()
                        },
                    contentDescription = stringResource(
                        R.string.back_button
                    )
                )
                Text(
                    text = stringResource(R.string.previous_readings),
                    style = Typography.displayLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(readings) { reading ->
                    PreviousReadingCard(reading, navController)
                }
            }
        }
    }
}