package com.example.avjindersinghsekhon.minimaltodo.model

import com.google.gson.annotations.SerializedName

/**
 * Created on 2019-09-06, 14:14.
 * @author Akram Shokri
 */

data class TransactionData(@SerializedName("transaction_code") val TransactionCode: String,
                           @SerializedName("amount") val amount: String,
                           @SerializedName("vat_amount") val vatAmount: String,
                           @SerializedName("tip_amount") val tipAmount: String,
                           @SerializedName("fee_amount") val feeAmount: String,
                           @SerializedName("currency") val currency: String,
                           @SerializedName("timestamp") val timestamp: String,
                           @SerializedName("status") val status: String,
                           @SerializedName("entry_mode") val entryMode: String,
                           @SerializedName("card") val card: Card,
                           @SerializedName("products") val products: List<Product>,
                           @SerializedName("merchant_data") val merchantData: MerchantData
)

data class Card(@SerializedName("last_4_digits") val last4Digits: String,
                @SerializedName("type") val type: String,
                @SerializedName("cardholder_name") val cardholderName: String
)

data class Product(@SerializedName("name") val name: String,
                   @SerializedName("price") val price: String,
                   @SerializedName("quantity") val quantity: String,
                   @SerializedName("total_price") val totalPrice: String)

data class MerchantData(@SerializedName("merchant_profile") val merchantProfile: MerchantProfile)

data class MerchantProfile(@SerializedName("business_name") val businessName: String,
                           @SerializedName("address") val address: Address)

data class Address(@SerializedName("address_line1") val addressLine1: String,
                   @SerializedName("city") val city: String,
                   @SerializedName("country_en_name") val country: String)
