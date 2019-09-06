package com.example.avjindersinghsekhon.minimaltodo

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.avjindersinghsekhon.minimaltodo.network.AuthenticationRepository
import com.example.avjindersinghsekhon.minimaltodo.network.ITokenCallBack
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created on 2019-09-05, 14:21.
 * @author Akram Shokri
 */

@RunWith(JUnit4::class)
class TestAuthenticationRepository {

    private val callback = object : ITokenCallBack {
        override fun onSuccess(data: String) {
            assert(data.length >= 32)
        }

        override fun onError(error: String) {
            assert(error.isNotEmpty())
        }
    }

    private val repository = AuthenticationRepository(getInstrumentation().context, callback)


    @Test
    fun testAuthentication(){
        repository.requestToken()
    }
}