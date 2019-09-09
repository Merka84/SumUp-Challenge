package com.example.avjindersinghsekhon.minimaltodo

import com.example.avjindersinghsekhon.minimaltodo.model.convertToLocalDateTime
import org.junit.Test
import java.text.SimpleDateFormat

/**
 * Created on 2019-09-05, 14:21.
 * @author Akram Shokri
 */


class TestDateConverter {

    @Test
    fun testDateConversion(){
        val dateStr = "2019-09-09T16:24:41.824Z"
        val result = SimpleDateFormat().convertToLocalDateTime(dateStr)

        assert(result.startsWith("09/09/2019"))
        assert(result.contains("8:54"))
    }
}