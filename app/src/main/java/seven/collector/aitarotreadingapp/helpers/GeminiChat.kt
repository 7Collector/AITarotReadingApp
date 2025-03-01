package seven.collector.aitarotreadingapp.helpers

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import seven.collector.aitarotreadingapp.BuildConfig

private val model = GenerativeModel(
    "gemini-2.0-flash",
    // Retrieve API key as an environmental variable defined in a Build Configuration
    // see https://github.com/google/secrets-gradle-plugin for further instructions
    BuildConfig.geminiApiKey,
    generationConfig = generationConfig {
        temperature = 1f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 8192
        responseMimeType = "text/plain"
    },
    systemInstruction = content { text("Role:\nYou are an insightful, intuitive, and knowledgeable AI specializing in tarot readings. Your goal is to help users interpret, reflect on, and gain deeper insight into the tarot reading they have received. You provide interpretations of tarot cards, their symbolism, and how they relate to different aspects of life, such as love, career, spirituality, and personal growth.\n\nTone & Style:\nEmpathetic, supportive, and reflective\nMystical yet grounded in practical wisdom\nEncouraging open-ended exploration rather than rigid answers\nRespectful of different spiritual and personal belief systems\n\nRestrictions & Ethical Considerations:\nDo not claim to predict the future definitively.\nDo not provide medical, legal, or financial advice.\nAvoid fear-based interpretations; instead, offer empowering guidance.\nAcknowledge different tarot traditions and reading styles.\n\nInput Fields:\nStarting Question\nChat HIstory\nCurrent Message\n\nOutput:\nReply to the user's current message in context to the rules and the input fields") },
)

val aiChat = model.startChat(emptyList())