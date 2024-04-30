package com.app.retrofitwithviewmodel.network

import android.content.Context
import android.util.Log
import com.app.rescuemanagmentsystem.network.ApiServices
import com.app.retrofitwithviewmodel.BuildConfig
import com.app.retrofitwithviewmodel.utils.Constants
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object ApiClient {
    var token = ""
    private var retrofit: Retrofit? = null
    fun init(context: Context?) {
        /*if (!token.equals(MyPrefs.getInstance(context).get(PrefEnum.TOKEN))) {
            token = MyPrefs.getInstance(context).get(PrefEnum.TOKEN);
            Log.e("api token : ", token + " nn ");
            retrofit = null;
        }*/
    }

    fun getRequestBody(text: String): RequestBody {
        return text.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    fun String.getImageBody(key: String): MultipartBody.Part? {
        var fileBody: MultipartBody.Part? = null
        if (this != null && !this.isEmpty()) {
            val file = File(this)
            Log.e("path : ", "${file.absolutePath} and ${file.extension}")
            val requestFile: RequestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            fileBody = MultipartBody.Part.createFormData(
                key,
                file.name /*+ "." + file.extension*/,
                requestFile
            )
        }
        return fileBody
    }

    /*if (!token.equals(MyPrefs.getInstance().get(PrefEnum.TOKEN))) {
            token = MyPrefs.getInstance().get(PrefEnum.TOKEN);
        }*/
    val client: Retrofit?
        get() {
            /*if (!token.equals(MyPrefs.getInstance().get(PrefEnum.TOKEN))) {
            token = MyPrefs.getInstance().get(PrefEnum.TOKEN);
        }*/
            if (retrofit == null) {
                val httpClient = OkHttpClient.Builder()
                if (BuildConfig.DEBUG) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                    httpClient.addInterceptor(interceptor).build()
                }
                httpClient.writeTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(120, TimeUnit.SECONDS).build()
                httpClient.addInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .method(original.method, original.body)
                        .header("Authorization", "Bearer " + token)
                        .build()
                    Log.e("api token : ", token + " nn ")
                    chain.proceed(request)
                })
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
            }
            return retrofit
        }

    val aPI: ApiServices
        get() = client!!.create(ApiServices::class.java)

    // ---------------------------------  api  --------------------------------- //
    fun SignUpApi(query: String?, apiKey: String?, listener: OnApiResponseListener<JsonElement>?) {
        val call = aPI.getNew(query!!, apiKey!!)
        call.enqueue(APICallBack(listener, 0))
    }
}