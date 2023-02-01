package com.ewida.rickmorti.ui.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.ewida.rickmorti.R
import com.ewida.rickmorti.databinding.ActivityWebBinding
import com.ewida.rickmorti.utils.bundle_utils.BundleKeys.WEB_PAGE_URL
import com.ewida.rickmorti.utils.bundle_utils.BundleUtils.getFromBundle
import javax.inject.Inject

class WebActivity : AppCompatActivity() {

    private lateinit var binding:ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.movieWorldWv.webViewClient= WebViewClient()
        viewWebPage(getFromBundle(WEB_PAGE_URL,"https://www.themoviedb.org/") as String)
    }

    private fun viewWebPage(url:String){
        binding.movieWorldWv.loadUrl(url)
    }
}