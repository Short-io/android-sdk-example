package com.github.shortioapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.shortioapp.ui.theme.ShortIOAppTheme
import com.github.shortiosdk.ShortioSdk
import com.github.shortiosdk.ShortIOResult
import com.github.shortiosdk.ShortIOParameters
import kotlin.concurrent.thread
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ShortioSdk.initialize( apiKey, domain) //Replace with your Short.io API KEY and Domain in Constants File
        enableEdgeToEdge()

        setContent {
            ShortIOAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LinkShortnerTitle()


                        Spacer(modifier = Modifier.height(16.dp))
                        LinkShorteningButton()

                        Spacer(modifier = Modifier.height(16.dp))
                        CreateSecureUrlButton()

                        Spacer(modifier = Modifier.height(16.dp))
                        TrackConversionButton()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch {
            val result = ShortioSdk.handleIntent(intent)
            Log.d("New Intent", "Host: ${result?.host}, Path: ${result?.path}, DestinationURL: ${result?.destinationUrl}")
        }
    }
}

@Composable
fun LinkShorteningButton() {
    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                isLoading = true
                resultMessage = null
                thread {
                    try {
                        val params = ShortIOParameters(
                            originalURL = "https://{YOUR_DOMAIN}/",
                        )

                        when (val result = ShortioSdk.shortenUrl(params)) {
                            is ShortIOResult.Success -> {
                                val shortUrl = result.data.shortURL
                                Log.d("ShortIO", "Shortened URL: $shortUrl")
                                (context as ComponentActivity).runOnUiThread {
                                    isLoading = false
                                    isError = false
                                    resultMessage = "Short Url:- $shortUrl"
                                }
                            }
                            is ShortIOResult.Error -> {
                                val error = result.data
                                Log.e("ShortIO", "Error:- ${error.message}")
                                (context as ComponentActivity).runOnUiThread {
                                    isLoading = false
                                    isError = true
                                    resultMessage = "Error:- ${error.message}"
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ShortIO", "Exception: ${e.message}", e)
                        (context as ComponentActivity).runOnUiThread {
                            isLoading = false
                            isError = true
                            resultMessage = "Error: ${e.message}"
                        }
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(text = "Create Short Link")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(4.dp))
            Text("Creating Short Link...", fontSize = 14.sp, color = Color.Gray)
        }

        resultMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            if (isError) {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                val actualUrl = it.substringAfter("Short Url:- ").trim()

                Text(
                    text = actualUrl,
                    fontSize = 16.sp,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("shortURL", actualUrl)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Short URL copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Copy URL")
                }
            }
        }
    }
}

@Composable
fun CreateSecureUrlButton() {
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        val originalUrl = "https://{your_domain}"
                        val result = ShortioSdk.createSecure(originalUrl)

                        Log.d("SecureURL", "RESULT: $result")
                        Log.d("SecureURL", "URL: ${result.securedOriginalURL}")
                        Log.d("SecureURL", "KEY: ${result.securedShortUrl}")

                        (context as ComponentActivity).runOnUiThread {
                            isLoading = false
                            resultMessage = "Secure URL: ${result.securedShortUrl}"
                        }
                    } catch (e: Exception) {
                        Log.e("SecureURL", "Exception: ${e.message}", e)
                        (context as ComponentActivity).runOnUiThread {
                            isLoading = false
                            resultMessage = "Error: ${e.message}"
                        }
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(text = if (isLoading) "Generating..." else "Create Secure Short Link")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        resultMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            if (it.startsWith("Error")) {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                val secureUrl = it.substringAfter("Secure URL:").trim()

                Text(
                    text = secureUrl,
                    fontSize = 16.sp,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("secureURL", secureUrl)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Secure URL copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Copy Secure URL")
                }
            }
        }
    }
}

@Composable
fun TrackConversionButton() {
    var conversionResult by remember { mutableStateOf<Boolean?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val res = ShortioSdk.trackConversion()
                        // You can pass originalUrl, clid and conversionId as parameters to trackConversion()
                        conversionResult = res
                        errorMessage = null
                    } catch (e: Exception) {
                        conversionResult = null
                        errorMessage = e.message
                        Log.e("Handle Conversion Tracking", "Error calling trackConversion", e)
                    }
                }
            }
        ) {
            Text(text = "Conversion Tracking")
        }
        conversionResult?.let { success ->
            Text(
                text = if (success) "Conversion successful" else "Conversion failed",
                color = if (success) Color.Green else Color.Red
            )
        }
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red
            )
        }
    }
}

@Composable
fun LinkShortnerTitle() {
    Text(
        text = "Short Link Generator",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    )
}