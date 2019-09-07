package com.example.avjindersinghsekhon.minimaltodo

import androidx.test.filters.SmallTest
import com.example.avjindersinghsekhon.minimaltodo.network.APICallRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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
       repository.getRreceipt("TC263PYMYA")
    }
}