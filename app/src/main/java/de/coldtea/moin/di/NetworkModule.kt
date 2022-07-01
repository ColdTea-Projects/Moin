package de.coldtea.moin.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import de.coldtea.moin.BuildConfig
import de.coldtea.moin.data.network.forecast.WeatherForecastApi
import de.coldtea.moin.data.network.interceptors.AuthInterceptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val WEATHER_API_RETROFIT = "WeatherApiRetrofit"

@OptIn(ExperimentalSerializationApi::class)
val json = Json{
    ignoreUnknownKeys = true
    explicitNulls = false
}

val networkModule = module {
    factory { AuthInterceptor() }
    factory { HttpLoggingInterceptor() }
    factory { provideOkHttpClient(get(), get()) }
    factory { provideForecastApi(get(named(WEATHER_API_RETROFIT))) }
    single(named(WEATHER_API_RETROFIT)) { provideRetrofitWeatherApi(get()) }
}

@OptIn(ExperimentalSerializationApi::class)
fun provideRetrofitWeatherApi(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.ROOT_URL_WEATHER_API)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}


fun provideOkHttpClient(authInterceptor: AuthInterceptor, httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    val okHttpClient = OkHttpClient().newBuilder().addInterceptor(authInterceptor).apply {
        if(BuildConfig.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(httpLoggingInterceptor)
        }
    }
    return okHttpClient.build()
}

fun provideForecastApi(retrofit: Retrofit): WeatherForecastApi = retrofit.create(WeatherForecastApi::class.java)
