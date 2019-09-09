package com.example.avjindersinghsekhon.minimaltodo

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.avjindersinghsekhon.minimaltodo.model.convertToLocalDateTime
import com.example.avjindersinghsekhon.minimaltodo.network.AuthenticationRepository
import com.example.avjindersinghsekhon.minimaltodo.network.ITokenCallBack
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created on 2019-09-05, 14:21.
 * @author Akram Shokri
 */

@RunWith(JUnit4::class)
class TestDateConverter {

    @Test
    fun testDateConversion(){
        val dateStr = "2019-09-09T16:24:41.824Z"
        val result = SimpleDateFormat().convertToLocalDateTime(dateStr)

        assert(result.startsWith("09/09/2019"))
        assert(result.contains("8:54"))
    }
}