package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class GeminiGenerationResult(
    val mainText: String,
    val thoughtProcess: String?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

class GeminiService {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    suspend fun generateFullStackArtifact(
        prompt: String,
        currentFilesSummary: String,
        history: List<Pair<String, String>> = emptyList(),
        model: String = "gemini-3.1-pro-preview",
        enableHighThinking: Boolean = true
    ): GeminiGenerationResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext GeminiGenerationResult(
                mainText = "",
                thoughtProcess = "AI Key Configuration Check: No active Gemini API key found in BuildConfig. Please set your key in AI Studio Secrets panel.",
                isSuccess = false,
                errorMessage = "Gemini API Key missing. Please configure GEMINI_API_KEY in the Secrets panel."
            )
        }

        val systemPrompt = """
            You are Bolt AI, an elite autonomous full-stack software engineer inside Bolt.diy IDE.
            You build production-ready modern web applications, APIs, responsive UI, components, and scripts.
            
            When given a user prompt:
            1. First think carefully about the system architecture, file organization, modern web standards (e.g. React/Vite/Tailwind or Node.js API), responsive design, state management, and edge cases.
            2. Always format your output strictly using Bolt Artifacts syntax:
               <boltArtifact id="project-build" title="Project Name">
                 <boltAction type="shell">npm install [packages]</boltAction>
                 <boltAction type="file" filePath="path/to/file.ext">
                 ...file content...
                 </boltAction>
                 <boltAction type="shell">npm run dev</boltAction>
               </boltArtifact>
            
            Guidelines:
            - Always create a complete, self-contained, working web application.
            - Provide clean index.html with Tailwind CSS or CSS styling, and modern interactive JavaScript/React in src/App.jsx.
            - Ensure interactive buttons, forms, state, and visual styling are rich, functional, and bug-free.
            - Provide a clear project explanation before or after the artifact.
        """.trimIndent()

        val contentsList = mutableListOf<GeminiContent>()

        // Add previous history turns
        for ((role, text) in history) {
            val apiRole = if (role == "user") "user" else "model"
            contentsList.add(GeminiContent(role = apiRole, parts = listOf(GeminiPart(text = text))))
        }

        // Current turn with context
        val fullPrompt = if (currentFilesSummary.isNotBlank()) {
            """
            $prompt
            
            [Current Workspace Files Summary]:
            $currentFilesSummary
            """.trimIndent()
        } else {
            prompt
        }
        contentsList.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = fullPrompt))))

        val generationConfig = if (enableHighThinking && model == "gemini-3.1-pro-preview") {
            GenerationConfig(
                temperature = 0.7f,
                thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH")
            )
        } else {
            GenerationConfig(temperature = 0.7f)
        }

        val geminiRequest = GeminiRequest(
            contents = contentsList,
            generationConfig = generationConfig,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        try {
            val jsonBody = requestAdapter.toJson(geminiRequest)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("GeminiService", "API call failed code ${response.code}: $responseString")
                return@withContext GeminiGenerationResult(
                    mainText = "",
                    thoughtProcess = null,
                    isSuccess = false,
                    errorMessage = "HTTP ${response.code}: $responseString"
                )
            }

            val parsedResponse = responseAdapter.fromJson(responseString)
            val candidate = parsedResponse?.candidates?.firstOrNull()
            val parts = candidate?.content?.parts ?: emptyList()

            var thoughtAccumulator = StringBuilder()
            var mainTextAccumulator = StringBuilder()

            for (part in parts) {
                if (part.thought == true) {
                    thoughtAccumulator.append(part.text ?: "").append("\n")
                } else {
                    mainTextAccumulator.append(part.text ?: "")
                }
            }

            // Also check for embedded <boltThought> or <thought> tags
            val rawMain = mainTextAccumulator.toString()
            val thoughtRegex = Regex("<boltThought>([\\s\\S]*?)</boltThought>", RegexOption.IGNORE_CASE)
            val thoughtMatch = thoughtRegex.find(rawMain)
            val finalThought = if (thoughtMatch != null) {
                val extracted = thoughtMatch.groupValues[1].trim()
                if (thoughtAccumulator.isNotEmpty()) "$thoughtAccumulator\n$extracted" else extracted
            } else if (thoughtAccumulator.isNotEmpty()) {
                thoughtAccumulator.toString().trim()
            } else null

            val cleanedMain = rawMain.replace(thoughtRegex, "").trim()

            GeminiGenerationResult(
                mainText = cleanedMain.ifBlank { rawMain },
                thoughtProcess = finalThought,
                isSuccess = true
            )
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception calling Gemini API", e)
            GeminiGenerationResult(
                mainText = "",
                thoughtProcess = null,
                isSuccess = false,
                errorMessage = e.message ?: "Unknown error"
            )
        }
    }
}
