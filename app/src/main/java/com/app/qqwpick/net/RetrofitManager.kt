package com.app.qqwpick.net

import com.app.qqwpick.BuildConfig
import com.app.qqwpick.data.user.UserBean
import com.app.qqwpick.util.BASE_URL
import com.app.qqwpick.util.SpUtils
import com.app.qqwpick.util.USER_BEAN
import com.bbq.net.cookie.HttpsHelper
import com.bbq.net.cookie.SimpleCookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext

//管理retrofit
class RetrofitManager private constructor() {


    private var retrofit: Retrofit

    companion object {
        val instance: RetrofitManager by lazy { RetrofitManager() }
    }

    init {
        retrofit = Retrofit.Builder()
            .client(initClient())
            .validateEagerly(true)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    private var hostNames: Array<String>? = null
    private var clientCertificate: InputStream? = null
    private var serverCertificates: Array<InputStream>? = null
    private var clientCertificatePassword: String? = null

    private fun initClient(): OkHttpClient {

        val keyManagers =
            HttpsHelper.prepareKeyManager(clientCertificate, clientCertificatePassword)
        val trustManager = HttpsHelper.prepareX509TrustManager(serverCertificates)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagers, arrayOf(trustManager), null)

        return OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(false)
//            .cache(Cache(BaseApp.cacheDir, Long.MAX_VALUE))
            .cookieJar(SimpleCookieJar())
//            .hostnameVerifier { hostname, session ->
//                if (hostNames != null) {
//                    listOf(*hostNames!!)
//                        .contains(hostname)
//                } else HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session)
//            }
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.addHeader("app-version", BuildConfig.VERSION_NAME)
                requestBuilder.addHeader("app-platform", "2")
                requestBuilder.addHeader("role-key", "1")
                if (!SpUtils.getParcelable<UserBean>(USER_BEAN)?.token.isNullOrEmpty()) {
                    requestBuilder.addHeader(
                        "e-qqw-token",
                        SpUtils.getParcelable<UserBean>(USER_BEAN)?.token.toString()
                    )
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}