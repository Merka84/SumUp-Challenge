package com.example.avjindersinghsekhon.minimaltodo

import androidx.test.filters.SmallTest
import com.example.avjindersinghsekhon.minimaltodo.model.SumUpReceiptResponse
import com.example.avjindersinghsekhon.minimaltodo.network.APICallRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created on 2019-09-05, 14:21.
 * @author Akram Shokri
 */

@RunWith(JUnit4::class)
@SmallTest
class TestAPICallRepository {
    private lateinit var repository: APICallRepository

    @Before
    fun prepare() {
        repository = APICallRepository()
    }

    @Test
    fun testReceipt() {
       repository.getReceipt("TC263PYMYA", object: Callback<SumUpReceiptResponse> {
           override fun onFailure(call: Call<SumUpReceiptResponse>, t: Throwable) {
               assert(t.localizedMessage != null)
           }

           override fun onResponse(call: Call<SumUpReceiptResponse>, response: Response<SumUpReceiptResponse>) {
               assert(response.body() != null)
           }

       })
    }
}