package com.signalplay.mobile

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Esconde a barra superior
        supportActionBar?.hide()

        // Cria o WebView em tela cheia direto pelo código
        webView = WebView(this)
        webView.setBackgroundColor(Color.parseColor("#111114"))
        setContentView(webView)

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true 
        settings.databaseEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false 
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Puxando o seu site Hospedado!
        webView.loadUrl("http://signalplay.pro")

        // Botão de voltar inteligente (Navega no site em vez de fechar o app)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }
}
