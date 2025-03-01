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

val tarotInterpretationModel = GenerativeModel(
    "gemini-2.0-flash",
    BuildConfig.geminiApiKey,
    generationConfig = generationConfig {
        temperature = 1f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 8192
        responseMimeType = "application/json"
    },
    systemInstruction = content {
        text(
            """
        Limit your response to 400 words. You are an expert tarot reader with deep knowledge of tarot symbolism, meanings, and interpretations.  

        Given a set of tarot cards, generate a compelling and insightful reading.  

        **Input:**  
        - User's question  
        - Brief user information (optional)  
        - A list of tarot cards (e.g., The Fool, The Magician, The High Priestess)  

        **Output:**  
        - title: A short, captivating title summarizing the overall theme of the reading.  
        - interpretation: A concise explanation of the cards' meanings, their connections, and the guidance they offer. Incorporate emotional, spiritual, and practical insights. A Overall conclusion and insight too. 

        Ensure the response is clear, structured, and provides a concrete overall evaluation that directly addresses the user's question.
        """.trimIndent()
        )
    },
    safetySettings = listOf(dangerousContent, sexuallyExplicit, hateSpeech, harassment),
)