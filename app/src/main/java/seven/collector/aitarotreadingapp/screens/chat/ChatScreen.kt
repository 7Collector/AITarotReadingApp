package seven.collector.aitarotreadingapp.screens.chat

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import seven.collector.aitarotreadingapp.R
import seven.collector.aitarotreadingapp.TarotApp
import seven.collector.aitarotreadingapp.database.models.Chat
import seven.collector.aitarotreadingapp.theme.components.Background
import seven.collector.aitarotreadingapp.theme.components.FormattedText
import seven.collector.aitarotreadingapp.theme.utilities.Typography
import seven.collector.aitarotreadingapp.theme.utilities.containerColor
import seven.collector.aitarotreadingapp.theme.utilities.primaryColor
import seven.collector.aitarotreadingapp.theme.utilities.textColor

@Composable
fun ChatScreen(id: Int, navController: NavController, viewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(
    (LocalContext.current.applicationContext as TarotApp).database.readingDao(),
    readingId = id, LocalContext.current
))) {
    val reading by viewModel.reading.collectAsStateWithLifecycle()
    val isAiResponding by viewModel.isAiResponding.collectAsStateWithLifecycle()
    val chatInputText by viewModel.chatInputText.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(reading?.chats?.size) {
        reading?.chats?.let { if (it.isNotEmpty()) listState.scrollToItem(it.size - 1) }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopTTS()
        }
    }

    Background {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                    text = reading?.title ?: "Unknown",
                    style = Typography.displayLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                state = listState
            ) {
                items(reading?.chats ?: emptyList()) { chat ->
                    ChatCard(chat)
                    Spacer(Modifier.height(4.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerColor, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = chatInputText,
                    onValueChange = { viewModel.onChatInputChange(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.type_message)) },
                    textStyle = Typography.bodyLarge.copy(color = textColor),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        disabledTextColor = textColor.copy(alpha = 0.5f),
                        errorTextColor = textColor,

                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        disabledContainerColor = containerColor,
                        errorContainerColor = containerColor,

                        cursorColor = primaryColor,
                        errorCursorColor = primaryColor,
                        selectionColors = TextSelectionColors(primaryColor, primaryColor.copy(alpha = 0.4f)),

                        focusedIndicatorColor = primaryColor,
                        unfocusedIndicatorColor = textColor.copy(alpha = 0.4f),
                        disabledIndicatorColor = textColor.copy(alpha = 0.2f),
                        errorIndicatorColor = primaryColor,

                        focusedTrailingIconColor = primaryColor,
                        unfocusedTrailingIconColor = textColor,
                        disabledTrailingIconColor = textColor.copy(alpha = 0.4f),
                        errorTrailingIconColor = primaryColor
                    )
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(primaryColor, RoundedCornerShape(32.dp))
                        .clickable { viewModel.startListening() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mic),
                        contentDescription = "Send",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                if (chatInputText.isNotEmpty() && !isAiResponding) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(primaryColor, RoundedCornerShape(32.dp))
                            .clickable { viewModel.sendMessage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = "Send",
                            modifier = Modifier.size(24.dp)
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
                .widthIn(min = 50.dp, max = 300.dp)
                .background(
                    color = if (chat.sender == "user") primaryColor else containerColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            FormattedText(
                text = chat.text,
                style = Typography.bodyLarge,
                textAlign = if (chat.sender == "user") TextAlign.End else TextAlign.Start
            )
        }
    }
}
