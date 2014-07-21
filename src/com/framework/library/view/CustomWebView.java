package com.framework.library.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebView extends WebView{

	// Esto hace que los links de la pagina no funcionen y por lo tanto el usuario no cambie de pagina web
//	private final static String javascript = "javascript:$('a').each(function(){if($(this).attr('onclick') == null){$(this).removeAttr('href');}});";

	private ProgressDialog progress;
	
	public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public CustomWebView(Context context) {
		super(context);
		initialize(context);
	}
	
	public void initialize(Context context){
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.setWebViewClient(new CustomWebViewClient());
        this.setWebChromeClient(new CustomWebChromeClient());
        this.getSettings().setJavaScriptEnabled(true);
        this.setInitialScale(1);
        this.getSettings().setBuiltInZoomControls(true); 
        this.getSettings().setSupportZoom(true);
        
//        progress = new ProgressDialog(context);
//        progress.setMessage(context.getResources().getString(R.string.loading));
	}
	
	private class CustomWebViewClient extends WebViewClient {

		@Override
		public void onLoadResource(WebView view, String url) {	
//			view.loadUrl(javascript);
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
//			progress.dismiss();
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
//			progress.show();
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}
	
	private class CustomWebChromeClient extends WebChromeClient{}
}
