package com.alexzh.rijksmuseum.data.remote

import com.alexzh.rijksmuseum.domain.ArtObjectsRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val networkModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single<AuthInterceptor> { AuthInterceptor() }
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .build()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://www.rijksmuseum.nl/api/en/")
            .client(get())
            .addConverterFactory(
                get<Json>()
                    .asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
    single<RijksmuseumApi> {
        get<Retrofit>().create(RijksmuseumApi::class.java)
    }
    single<ArtObjectsRepository> {
        RemoteArtObjectsRepository(
            api = get<RijksmuseumApi>()
        )
    }
}