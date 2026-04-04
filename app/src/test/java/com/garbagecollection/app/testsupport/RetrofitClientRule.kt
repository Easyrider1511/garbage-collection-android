package com.garbagecollection.app.testsupport

import com.garbagecollection.app.api.ApiService
import com.garbagecollection.app.api.RetrofitClient
import org.junit.rules.ExternalResource

class RetrofitClientRule(
    val fakeApiService: FakeApiService = FakeApiService()
) : ExternalResource() {

    override fun before() {
        replaceApiService(fakeApiService)
    }

    override fun after() {
        replaceApiService(null)
    }

    private fun replaceApiService(apiService: ApiService?) {
        val field = RetrofitClient::class.java.getDeclaredField("apiService")
        field.isAccessible = true
        field.set(RetrofitClient, apiService)
    }
}
