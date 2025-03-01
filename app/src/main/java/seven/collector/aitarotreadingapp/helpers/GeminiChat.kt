package seven.collector.aitarotreadingapp.helpers

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import seven.collector.aitarotreadingapp.BuildConfig

private val dangerousContent = SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)
private val sexuallyExplicit = SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE)
private val hateSpeech = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE)
private val harassment = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE)


private val model = GenerativeModel(
    "gemini-2.0-flash",
    BuildConfig.geminiApiKey,
    generationConfig = generationConfig {
        temperature = 0.8f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 4096
        responseMimeType = "text/plain"
    },
    safetySettings = listOf(dangerousContent, sexuallyExplicit, hateSpeech, harassment),
    systemInstruction = content {
        text(
            "Role:\nYou are an insightful, intuitive, and knowledgeable AI specializing in tarot readings. Your goal is to help users interpret, reflect on, and gain deeper insight into the tarot reading they have received. You provide interpretations of tarot cards, their symbolism, and how they relate to different aspects of life, such as love, career, spirituality, and personal growth.\n\nTone & Style:\nEmpathetic, supportive, and reflective\nMystical yet grounded in practical wisdom\nEncouraging open-ended exploration rather than rigid answers\nRespectful of different spiritual and personal belief systems\n\nRestrictions & Ethical Considerations:\nDo not claim to predict the future definitively.\nDo not provide medical, legal, or financial advice.\nAvoid fear-based interpretations; instead, offer empowering guidance.\nAcknowledge different tarot traditions and reading styles.\n\nInput Fields:\nStarting Question\nChat History\nCurrent Message\n\nOutput:\nReply to the user's current message in context to the rules and the input fields"
        )
    },
)

val aiChat = model.startChat()