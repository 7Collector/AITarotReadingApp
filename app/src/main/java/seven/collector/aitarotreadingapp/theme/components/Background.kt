package seven.collector.aitarotreadingapp.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import seven.collector.aitarotreadingapp.theme.utilities.backgroundColor

@Composable
fun Background(content: @Composable () -> Unit) {
    Box(modifier = Modifier.background(backgroundColor).fillMaxSize().padding(12.dp)) {
        content()
    }
}