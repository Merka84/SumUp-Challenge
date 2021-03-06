package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.avjindersinghsekhon.minimaltodo.databinding.ReceiptLayoutBinding
import com.example.avjindersinghsekhon.minimaltodo.model.SumUpReceiptResponse
import com.example.avjindersinghsekhon.minimaltodo.network.APICallRepository
import com.google.android.material.snackbar.Snackbar
import com.sumup.merchant.api.SumUpAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created on 2019-09-06, 18:39.
 * @author Akram Shokri
 */
class ReceiptActivity  : AppCompatActivity(){
    private lateinit var binding: ReceiptLayoutBinding
    private val repository = APICallRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.receipt_layout)
        addClickListener()
        prepareData()
    }

    private fun initTheme() {
        val theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME)
        if (theme == MainActivity.DARKTHEME) {
            setTheme(R.style.CustomStyle_DarkTheme)
        } else {
            setTheme(R.style.CustomStyle_LightTheme)
        }
    }

    private fun addClickListener(){
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun prepareData() {
        binding.progressBar.visibility = View.VISIBLE
        val transactionCode = intent.getStringExtra(PaymentActivity.TRANSACTION_CODE)
        val merchantCode = intent.getStringExtra(PaymentActivity.MERCHANT_CODE)
        repository.getReceipt(transactionCode, merchantCode, callBack)
    }

    private val callBack = object : Callback<SumUpReceiptResponse> {
        override fun onResponse(call: Call<SumUpReceiptResponse>,
                                response: Response<SumUpReceiptResponse>) {
            binding.progressBar.visibility = View.GONE
            binding.receipt = response.body()
            binding.executePendingBindings()
        }

        override fun onFailure(call: Call<SumUpReceiptResponse>, t: Throwable) {
            binding.progressBar.visibility = View.GONE
            if(t.message != null) {
                Snackbar.make(binding.root, t.message!!, Snackbar.LENGTH_LONG).show()
            }
        }

    }
}