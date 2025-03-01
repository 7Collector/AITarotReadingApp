package seven.collector.aitarotreadingapp.helpers

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import seven.collector.aitarotreadingapp.BuildConfig

private val dangerousContent = SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)
private val sexuallyExplicit = SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE)
private val hateSpeech = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE)
private val harassment = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE)

val tarotReadingModel = GenerativeModel(
    "gemini-2.0-flash",
// Retrieve API key as an environmental variable defined in a Build Configuration
// see https://github.com/google/secrets-gradle-plugin for further instructions
    BuildConfig.geminiApiKey,
    generationConfig = generationConfig {
        temperature = 0.7f
        topK = 40
        topP = 0.85f
        maxOutputTokens = 4096
        responseMimeType = "application/json"
    },
    systemInstruction = content { text("Limit your response to 400 words in a structured way. You are an expert tarot reader with deep knowledge of tarot symbolism, meanings, and interpretations.  \n\n        Given a set of tarot cards, generate a compelling and insightful reading.  \n\n        **Input:**  \n        - User's question  \n        - Brief user information (optional)  \n        - A list of tarot cards (e.g., The Fool, The Magician, The High Priestess)  \n\n        **Output:**  \n        - title: A short, captivating title summarizing the overall theme of the reading.  \n        - interpretation: A concise explanation of the cards' meanings, their connections, and the guidance they offer. Incorporate emotional, spiritual, and practical insights. A Overall conclusion and insight too. \n\n        Ensure the response is clear, structured, and provides a concrete overall evaluation that directly addresses the user's question.") },
    safetySettings = listOf(dangerousContent, sexuallyExplicit, hateSpeech, harassment),
)