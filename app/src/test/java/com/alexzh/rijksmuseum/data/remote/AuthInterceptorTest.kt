package com.alexzh.rijksmuseum.data.remote

import com.google.common.truth.Truth.assertThat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest  {
    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should key be added when interceptor registered`() {
        val interceptor = AuthInterceptor()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success": true}""")
        )
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val request = Request.Builder()
            .url(mockWebServer.url("/test-endpoint"))
            .build()

        val response = client.newCall(request).execute()

        assertThat(response.request.url.queryParameter("key"))
            .isNotEmpty()
    }
}