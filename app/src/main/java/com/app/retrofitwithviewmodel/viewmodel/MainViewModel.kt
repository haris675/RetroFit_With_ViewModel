package com.app.retrofitwithviewmodel.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.retrofitwithviewmodel.network.ApiClient
import com.app.retrofitwithviewmodel.network.OnApiResponseListener
import com.app.retrofitwithviewmodel.utils.AppState
import com.app.retrofitwithviewmodel.utils.Constants
import com.google.gson.JsonElement

class MainViewModel : ViewModel() {

    val newsList = MutableLiveData<AppState<JsonElement>>()

    fun getNews() {
        newsList.value = AppState.Loading()
        ApiClient.SignUpApi(
            Constants.QUERY,
            Constants.API_KEY,
            object : OnApiResponseListener<JsonElement> {
                override fun onResponseComplete(clsGson: JsonElement, requestCode: Int) {

                }

                override fun onResponseError(
                    errorMessage: String?,
                    requestCode: Int,
                    responseCode: Int
                ) {

                }

            })
    }

}