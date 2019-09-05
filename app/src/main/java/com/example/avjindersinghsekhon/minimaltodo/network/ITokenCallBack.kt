package com.example.avjindersinghsekhon.minimaltodo.network

/**
 * Created on 2019-09-05, 14:10.
 * @author Akram Shokri
 */
interface ITokenCallBack {
    fun onSuccess(data: String)
    fun onError(error: String)
}