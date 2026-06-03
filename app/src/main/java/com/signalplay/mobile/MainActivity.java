package com.signalplay.mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    private WebView webView;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Trava o aplicativo em pé no catálogo e ativa a Tela Cheia Absoluta
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ocultarBarrasDoSistema();

        webView = new WebView(this);
        webView.setBackgroundColor(Color.parseColor("#111114"));
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // Libera acesso total para o player ler os links do painel
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webView.setWebViewClient(new WebViewClient());

        // 2. O VERDADEIRO MOTOR DO PLAYER DE VÍDEO E ROTAÇÃO
        webView.setWebChromeClient(new WebChromeClient() {
            
            // SOME com aquele botão gigante de Play nativo do Android
            @Override
            public Bitmap getDefaultVideoPoster() {
                return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            }

            // Quando o site pedir Tela Cheia (Vídeo)
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                customView = view;
                customViewCallback = callback;
                
                // MÁGICA: Joga o vídeo na camada superior da tela (Resolve a Tela Preta)
                FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
                decorView.addView(customView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                
                // Vira o celular deitado automaticamente
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                ocultarBarrasDoSistema();
            }

            // Quando o usuário sair do Vídeo
            @Override
            public void onHideCustomView() {
                if (customView == null) return;
                
                FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
                decorView.removeView(customView);
                customView = null;
                customViewCallback.onCustomViewHidden();
                
                // Volta o celular para a posição em pé
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                ocultarBarrasDoSistema();
            }
        });

        webView.loadUrl("http://signalplay.pro");
    }

    // 3. Função blindada para esmagar qualquer barra de navegação/notificação
    private void ocultarBarrasDoSistema() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    // Garante que se o celular bloquear/desbloquear as barras continuam sumidas
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ocultarBarrasDoSistema();
        }
    }

    // Sistema inteligente do Botão "Voltar" do celular
    @Override
    public void onBackPressed() {
        if (customView != null) {
            // Se estiver vendo um filme, o botão Voltar apenas sai da tela cheia
            webView.getWebChromeClient().onHideCustomView();
        } else if (webView.canGoBack()) {
            // Se não, volta nas páginas do site
            webView.goBack();
        } else {
            // Fecha o aplicativo
            super.onBackPressed();
        }
    }
}
