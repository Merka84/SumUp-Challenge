package com.example.avjindersinghsekhon.minimaltodo.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.sumup.merchant.api.SumUpAPI
import com.sumup.merchant.api.SumUpLogin
import java.lang.reflect.Method


/**
 * Created on 2019-09-05, 13:07.
 * @author Akram Shokri
 */
class AuthenticationRepository(private val context: Context, private val callBack: ITokenCallBack) {
    private val tokenUrl = "https://api.sumup.com/token"
    private val authUrl = "https://api.sumup.com/authorize?response_type=code&client_id=kOqTv6sBVOUCa96IFytoiT6Ozd4O&redirect_uri=merka://sumup/token"
    private val params = JSONObject()
    val queue = Volley.newRequestQueue(context)

    init {
        params.put("client_id", "kOqTv6sBVOUCa96IFytoiT6Ozd4O")
//        params.put("response_type", "code")
//        params.put("redirect_uri", "merka://sumup/token")
//        params.put("grant_type", "client_credentials")
        params.put("grant_type", "authorization_code")
        params.put("username", "dev_mvvlaq7e@sumup.com")
        params.put("password", "extdev")
//        params.put("client_secret", "adcd5ff804c15347ae77ec1f9e2ea3d8b23659a1db10f11f0f6939cf5b8457c7")
    }

    fun authenticate(){
        val request = StringRequest(Request.Method.POST, authUrl,
                Response.Listener<String> {
                    Log.d("", it)
                },
                Response.ErrorListener {
                    Log.d("", it.message)
                })

        queue.add(request)
    }

    fun requestToken() {


        // Request a string response from the provided URL.
        val request = object : JsonObjectRequest(
                Method.POST,
                tokenUrl,
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
//        callBack.onSuccess("${data.get("token_type")} ${data.get("access_token")}")
        callBack.onSuccess(data.get("access_token") as String)

    }

    private fun processError(error: VolleyError){
        callBack.onError(error.message!!)
    }

}