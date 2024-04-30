package com.app.retrofitwithviewmodel.network

import android.content.Context
import android.net.Uri
import android.util.Log
import com.app.retrofitwithviewmodel.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class APICallBack<T>(val listener: OnApiResponseListener<T>?, val requestCode: Int) : Callback<T> {
    private val strMessage = "message"
    private val strError = "error"
    var context: Context? = null

    /*init {
        this.listener = listener!!
        this.requestCode = requestCode
    }*/

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (isSuccess(response)) {
            try {
                if (response.body() == null) {
                    Log.d("Tag", "posting-> " + response.code() + response.headers())
                    listener?.onResponseComplete(Integer.valueOf(response.code()) as T, requestCode)
                } else {
                    Log.e("Tag", "posting-> " + response.body()!!.javaClass.simpleName + " ")
                    if (listener != null) {
                        if (response.body() is JsonElement && response.body() is JsonObject) {
                            val json = response.body() as JsonElement?
                            try {
                                if (json!!.asJsonObject["status"].asInt == 1) {
                                    listener.onResponseComplete(response.body()!!, requestCode)
                                } else listener.onResponseError(
                                    json.asJsonObject[strMessage].asString,
                                    requestCode,
                                    response.code()
                                )
                            } catch (e: NumberFormatException) {
                                listener.onResponseComplete(response.body()!!, requestCode)
                            }
                        } else {
                            listener.onResponseComplete(response.body()!!, requestCode)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Tag", "posting-> try catch")
                try {
                    listener?.onResponseError(e.localizedMessage, requestCode, response.code())
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
                e.printStackTrace()
            }
        } else {
            Log.e("Tag", "isSuccess false")
        }
    }

    private fun isHostOffline(msg: String?): Boolean {
        val url = Uri.parse(Constants.BASE_URL)
        return msg!!.contains("Failed to connect to " + url.host)
    }

    /*override fun onFailure(call: Call<*>, throwable: Throwable) {
        Log.e("Tag", "onFailure-> (" + requestCode + ") " + throwable.message)
        try {
            if (throwable is JsonSyntaxException) {
                listener?.onResponseError(
                    "Server Response Changed : " + throwable.message,
                    requestCode,
                    0
                )
                return
            }
            if (throwable is MalformedJsonException) {
                listener?.onResponseError(
                    "some character are malformed in JSON : " + throwable.message,
                    requestCode,
                    0
                )
                return
            }
            if (throwable is IllegalStateException) {
                listener?.onResponseError("" + throwable.message, requestCode, 0)
                return
            }
            if (throwable is SocketTimeoutException) {
                listener?.onResponseError("Server Time out. Please try again.", requestCode, 0)
                return
            }
            if (throwable is UnknownHostException || throwable is ConnectException) {
                listener?.onResponseError("Internet Connection seems to be offline", requestCode, 0)
                return
            }
            if (throwable.message != null && throwable.message!!.contains("No address associated with hostname")) {
                listener?.onResponseError("Internet Connection seems to be offline", requestCode, 0)
                return
            }
            if (throwable is ConnectException && throwable.message != null && isHostOffline(
                    throwable.message
                )
            ) {
                listener?.onResponseError("Internet Connection seems to be offline", requestCode, 0)
                return
            }
            listener?.onResponseError("Exception : " + throwable.message, requestCode, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private fun isSuccess(response: Response<T>): Boolean {
        if (!response.isSuccessful) {
            try {
                try {
                    val i = response.errorBody()!!.byteStream()
                    val r = BufferedReader(InputStreamReader(i))
                    var errorResult = StringBuilder()
                    var line: String?
                    try {
                        while (r.readLine().also { line = it } != null) {
                            errorResult.append(line).append('\n')
                        }
                        val obj = Gson().fromJson(errorResult.toString(), JsonObject::class.java)
                        if (obj[strError] != null) {
                            if (obj[strError] is JsonObject) {
                                val error = obj[strError].asJsonObject["status"].asString
                                errorResult = StringBuilder()
                                errorResult.append(error)
                            } else {
                                val error = obj[strError].asString
                                errorResult = StringBuilder()
                                errorResult.append(error)
                            }
                        } else if (obj[strMessage] != null) {
                            val error = obj[strMessage].asString
                            errorResult = StringBuilder()
                            errorResult.append(error)
                        }
                        val error = """
                            ${obj[strError].asString}
                            ${obj["message"].asString}
                            """.trimIndent()
                        errorResult.append(error)
                        errorResult = StringBuilder()
                        errorResult.append(error)
                    } catch (e: Exception) {
                    }
                    val errormsg = response.code().toString() + " "
                    Log.e("Tag 12", "response ($requestCode) $errormsg")
                    if (listener != null) {
                        if (response.code() == 403) listener.onResponseError(
                            "Account temporary suspended by Superadmin.",
                            requestCode,
                            response.code()
                        ) else listener.onResponseError(errormsg, requestCode, response.code())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
        return true
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
        Log.e("Tag", "onFailure-> (" + requestCode + ") " + throwable.message)
        try {
            if (throwable is JsonSyntaxException) {
                listener?.onResponseError(
                    "Server Response Changed : " + throwable.message,
                    requestCode,
                    0
                )
                return
            }
            if (throwable is MalformedJsonException) {
                listener?.onResponseError(
                    "some character are malformed in JSON : " + throwable.message,
                    requestCode,
                    0
                )
                return
            }
            if (throwable is IllegalStateException) {
                listener?.onResponseError("" + throwable.message, requestCode, 0)
                return
            }
            if (throwable is SocketTimeoutException) {
                listener?.onResponseError("Server Time out. Please try again.", requestCode, 0)
                return
            }
            if (throwable is UnknownHostException || throwable is ConnectException) {
                listener?.onResponseError("Internet Connection seems to be offline", requestCode, 0)
                return
            }
            if (throwable.message != null && throwable.message!!.contains("No address associated with hostname")) {
                listener?.onResponseError("Internet Connection seems to be offline", requestCode, 0)
                return
            }
            if (throwable is ConnectException && throwable.message != null && isHostOffline(
                    throwable.message
                )
            ) {
                listener?.onResponseError("Internet Connection seems to be offline", requestCode, 0)
                return
            }
            listener?.onResponseError("Exception : " + throwable.message, requestCode, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}