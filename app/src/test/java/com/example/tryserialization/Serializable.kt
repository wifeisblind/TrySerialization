package com.example.tryserialization

import com.google.gson.Gson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.TestScheduler
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.`is`
import org.junit.AfterClass
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

class Serializable {

    companion object {

        private lateinit var networkService: Service

        private lateinit var mockWebServer: MockWebServer

        private var responseStr: String? = null

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            mockWebServer = MockWebServer()
            networkService = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(Service::class.java)

            mockWebServer.dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return when (request.path) {
                        "/api/get_sample" -> {
                            MockResponse().apply {
                                setResponseCode(200)
                                setBody("""{str:$responseStr}""")
                            }
                        }
                        else -> MockResponse().apply {
                            setResponseCode(404)
                            setBody("404 Not Found")
                        }
                    }
                }
            }
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            mockWebServer.shutdown()
        }
    }

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
    fun `test Serialization converter`() {

        responseStr = "aaa"

        val consumer = mockk<Consumer<in Throwable>>(relaxUnitFun = true)
        var sample: Sample? = null
        val testScheduler = TestScheduler()


        networkService.getSample()
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .subscribe(Consumer { response ->
                sample = response
            }, consumer)
        testScheduler.advanceTimeBy(1L, TimeUnit.SECONDS)


        verify(exactly = 0) {
            consumer.accept(any())
        }
        assertThat(sample?.str,  `is`("aaa"))
    }

    @Test
    fun `test Serialization converter when null happens`() {

        responseStr = null

        val consumer = mockk<Consumer<in Throwable>>(relaxUnitFun = true)
        var sample: Sample? = null
        val testScheduler = TestScheduler()


        networkService.getSample()
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .subscribe(Consumer { response ->
                sample = response
            }, consumer)
        testScheduler.advanceTimeBy(1L, TimeUnit.SECONDS)


        verify(exactly = 1) {
            consumer.accept(any())
        }
        assert(sample == null)
    }
}