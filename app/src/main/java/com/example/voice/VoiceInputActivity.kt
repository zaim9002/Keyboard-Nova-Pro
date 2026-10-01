package com.example.voice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.KeyboardInputMethodService
import com.example.KeyboardProApp

class VoiceInputActivity : ComponentActivity() {

    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenMatches?.firstOrNull()?.trim()
            if (!spokenText.isNullOrEmpty()) {
                KeyboardInputMethodService.commitVoiceText(spokenText)
            }
        }
        finish()
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSpeechRecognitionIntent()
        } else {
            Toast.makeText(this, "يرجى منح إذن الميكروفون للتحدث", Toast.LENGTH_SHORT).show()
            KeyboardInputMethodService.onVoiceError("لم يتم منح إذن الميكروفون")
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startSpeechRecognitionIntent()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startSpeechRecognitionIntent() {
        try {
            val prefs = try {
                (applicationContext as? KeyboardProApp)?.preferences
            } catch (e: Throwable) { null }

            val currentLang = prefs?.currentLanguage ?: "ar"
            val voiceLocale = when (currentLang) {
                "ar" -> "ar-SA"
                "en" -> "en-US"
                "fr" -> "fr-FR"
                "es" -> "es-ES"
                "de" -> "de-DE"
                "tr" -> "tr-TR"
                "ru" -> "ru-RU"
                "ur" -> "ur-PK"
                "fa" -> "fa-IR"
                else -> currentLang
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, voiceLocale)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, voiceLocale)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, voiceLocale)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث الآن... كيبورد محمد v.1")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechLauncher.launch(intent)
        } catch (e: Throwable) {
            Toast.makeText(this, "خطأ في تشغيل الصوت: ${e.message}", Toast.LENGTH_SHORT).show()
            KeyboardInputMethodService.onVoiceError("خطأ في الصوت: ${e.message}")
            finish()
        }
    }
}
