package seven.collector.aitarotreadingapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import seven.collector.aitarotreadingapp.helpers.SpeechToTextHelper
import seven.collector.aitarotreadingapp.screens.card.CardScreen
import seven.collector.aitarotreadingapp.screens.chat.ChatScreen
import seven.collector.aitarotreadingapp.screens.home.HomeScreen
import seven.collector.aitarotreadingapp.screens.onboarding.OnboardingScreen
import seven.collector.aitarotreadingapp.screens.previous.PreviousReadings
import seven.collector.aitarotreadingapp.screens.reading.ReadingScreen

class MainActivity : ComponentActivity() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        //val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)
        val isFirstLaunch = false
        setContent {
            TarotApp(isFirstLaunch)
        }
    }
}

@Composable
fun TarotApp(isFirstLaunch: Boolean) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (isFirstLaunch) "onboarding" else "home"
    ) {
        composable("onboarding") { OnboardingScreen(navController) }
        composable("home") { HomeScreen(
            SpeechToTextHelper(LocalContext.current),
            navController,
        ) }
        composable("previous") { PreviousReadings(navController) }
        composable("cards/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            CardScreen(id, navController)
        }
        composable("reading/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            ReadingScreen(id, navController)
        }
        composable("chat/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            ChatScreen(id, navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    TarotApp(false)
}