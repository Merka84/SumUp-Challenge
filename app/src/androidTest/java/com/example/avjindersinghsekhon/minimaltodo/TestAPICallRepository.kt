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
       repository.getReceipt("TC263PYMYA", "MVVLAQ7E",
               object: Callback<SumUpReceiptResponse> {
           override fun onFailure(call: Call<SumUpReceiptResponse>, t: Throwable) {
               assert(t.localizedMessage != null)
           }

           override fun onResponse(call: Call<SumUpReceiptResponse>, response: Response<SumUpReceiptResponse>) {
               val result = response.body()
               assert(result?.transactionData?.transactionCode == "TC263PYMYA")
               assert(result?.transactionData?.amount == "1.22")
               assert(result?.transactionData?.tipAmount == "0.1")
               assert(result?.transactionData?.timestamp == "2019-09-05T14:53:25.135Z")
               assert(result?.merchantData?.merchantProfile?.merchantCode == "MVVLAQ7E")
               assert(result?.merchantData?.merchantProfile?.businessName == "Android candidate")
               assert(result?.transactionData?.card?.last4Digits == "2308")
               assert(result?.transactionData?.card?.type == "MASTERCARD")
               assert(result?.transactionData?.card?.cardholderName == "Akram shokri")
               assert(result?.transactionData?.products?.get(0)?.name == "Taxi Ride")
               assert(result?.transactionData?.products?.get(0)?.price == "1.12")
               assert(result?.transactionData?.products?.get(0)?.quantity == 1)
               assert(result?.transactionData?.products?.get(0)?.totalPrice == "1.12")


               assert(response.body() != null)
           }

       })
    }
}