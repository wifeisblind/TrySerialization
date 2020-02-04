package com.example.tryserialization

import com.google.gson.Gson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Test

class Serializable {

    @Test
    fun `test Gson`() {
        val gson = Gson()
        val sample = gson.fromJson("""{str:"aaa"}""", Sample::class.java)
        assertThat(sample.str, `is`("aaa"))
    }

    @Test
    fun `test serializable`() {
        val json = Json(JsonConfiguration.Stable)
        val sample = json.parse(Sample.serializer(), """{str:"aaa"}""")
        assertThat(sample.str, `is`("aaa"))
    }

    @Test
    fun `if null happens on Gson`() {
        val gson = Gson()
        val sample = gson.fromJson("""{str:null}""", Sample::class.java)
        assert(sample.str == null)
    }

    @Test
    fun `if null happens on serialization`() {
        val json = Json(JsonConfiguration.Stable)
        val sample = json.parse(Sample.serializer(), """{str:null}""")
        assertThat(sample.str, `is`("default"))
    }

    @Test
    fun name() {
        ServiceFactory.getService().getSample()
    }
}