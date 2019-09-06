package com.example.avjindersinghsekhon.minimaltodo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.avjindersinghsekhon.minimaltodo.databinding.ReceiptLayoutBinding

/**
 * Created on 2019-09-06, 18:39.
 * @author Akram Shokri
 */
class ReceiptActivity  : AppCompatActivity(){
    lateinit var binding: ReceiptLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.receipt_layout)

    }
}