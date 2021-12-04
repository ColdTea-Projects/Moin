package de.coldtea.moin.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import de.coldtea.moin.BuildConfig
import de.coldtea.moin.data.network.forecast.WeatherForecastApi
import de.coldtea.moin.data.network.interceptors.AuthInterceptor
import de.coldtea.moin.data.network.spotify.SpotifyApi
import de.coldtea.moin.data.network.spotify.SpotifyAuthApi
import de.coldtea.moin.domain.services.AuthenticationService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val WEATHER_API_RETROFIT = "WeatherApiRetrofit"
private const val SPOTIFY_AUTH_API_RETROFIT = "SpotifyAuthRetrofit"
private const val SPOTIFY_API_RETROFIT = "SpotifyRetrofit"

@OptIn(ExperimentalSerializationApi::class)
val json = Json{
    ignoreUnknownKeys = true
    explicitNulls = false
}

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
    factory { AuthInterceptor() }
    factory { HttpLoggingInterceptor() }
    factory { provideOkHttpClient(get(), get()) }
    factory { provideForecastApi(get(named(WEATHER_API_RETROFIT))) }
    factory { provideSpotifyAuthApi(get(named(SPOTIFY_AUTH_API_RETROFIT))) }
    factory { provideSpotifyApi(get(named(SPOTIFY_API_RETROFIT))) }
    single(named(WEATHER_API_RETROFIT)) { provideRetrofitWeatherApi(get()) }
    single(named(SPOTIFY_AUTH_API_RETROFIT)) { provideRetrofitSpotifyAuth(get()) }
    single(named(SPOTIFY_API_RETROFIT)) { provideRetrofitSpotify(get()) }
    single { AuthenticationService() }
}

@OptIn(ExperimentalSerializationApi::class)
fun provideRetrofitWeatherApi(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.ROOT_URL_WEATHER_API)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}

@OptIn(ExperimentalSerializationApi::class)
fun provideRetrofitSpotifyAuth(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.ROOT_URL_SPOTIFY_AUTH)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}

@OptIn(ExperimentalSerializationApi::class)
fun provideRetrofitSpotify(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.ROOT_URL_SPOTIFY)
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

fun provideSpotifyAuthApi(retrofit: Retrofit): SpotifyAuthApi = retrofit.create(SpotifyAuthApi::class.java)

fun provideSpotifyApi(retrofit: Retrofit): SpotifyApi = retrofit.create(SpotifyApi::class.java)