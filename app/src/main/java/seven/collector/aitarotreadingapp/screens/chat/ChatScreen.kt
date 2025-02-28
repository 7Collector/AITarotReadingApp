package seven.collector.aitarotreadingapp.screens.chat

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import seven.collector.aitarotreadingapp.R
import seven.collector.aitarotreadingapp.TarotApp
import seven.collector.aitarotreadingapp.database.models.Chat
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.theme.components.Background
import seven.collector.aitarotreadingapp.theme.utilities.Typography
import seven.collector.aitarotreadingapp.theme.utilities.containerColor
import seven.collector.aitarotreadingapp.theme.utilities.primaryColor

@Composable
fun ChatScreen(id: Int, navController: NavController, viewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(
    (LocalContext.current.applicationContext as TarotApp).database.readingDao(),
    readingId = id
))) {

    val reading by viewModel.reading.collectAsStateWithLifecycle()
    val isAiResponding by viewModel.isAiResponding.collectAsStateWithLifecycle()
    val chatInputText by viewModel.chatInputText.collectAsStateWithLifecycle()

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
                Text(
                    text = reading.title,
                    style = Typography.displayLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(reading.chats) { chat ->
                    ChatCard(chat)
                }
            }

            // Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerColor, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = chatInputText,
                    onValueChange = { /* Update state here */ },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.type_message)) }
                )

                if (chatInputText.text.isNotEmpty() && isAiResponding) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(primaryColor, RoundedCornerShape(16.dp))
                            .padding(6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = stringResource(R.string.send_button)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatCard(chat: Chat) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (chat.sender == "user") Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(min = 80.dp, max = 250.dp)
                .background(
                    color = if (chat.sender == "user") primaryColor else containerColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = chat.text,
                style = Typography.bodyLarge,
                textAlign = if (chat.sender == "user") TextAlign.End else TextAlign.Start
            )
        }
    }
}