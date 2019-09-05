package com.example.avjindersinghsekhon.minimaltodo.network

import android.content.Context
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

/**
 * Created on 2019-09-05, 13:07.
 * @author Akram Shokri
 */
class AuthenticationRepository(val context: Context, val callBack: ITokenCallBack) {
    private val authUrl = "https://api.sumup.com/token"
    private val params = JSONObject()

    init {
        params.put("grant_type", "client_credentials")
        params.put("client_id", "kOqTv6sBVOUCa96IFytoiT6Ozd4O")
        params.put("client_secret", "adcd5ff804c15347ae77ec1f9e2ea3d8b23659a1db10f11f0f6939cf5b8457c7")
    }

    fun requestToken() {
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val request = object : JsonObjectRequest(
                Method.POST,
                authUrl,
                params,
                Response.Listener<JSONObject> {
                    processResponse(it)

                },
                Response.ErrorListener {
                    processError(it)
                }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }


        queue.add(request)
    }

    private fun processResponse(data: JSONObject){
        callBack.onSuccess(data.get("access_token") as String)

    }

    private fun processError(error: VolleyError){
        callBack.onError(error.message!!)
    }
}