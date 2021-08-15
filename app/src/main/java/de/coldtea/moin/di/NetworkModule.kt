package de.coldtea.moin.di

import de.coldtea.moin.BuildConfig
import de.coldtea.moin.data.network.forecast.WeatherForecastApi
import de.coldtea.moin.data.network.interceptors.AuthInterceptor
import de.coldtea.moin.data.network.spotify.SpotifyApi
import de.coldtea.moin.data.network.spotify.SpotifyAuthApi
import de.coldtea.moin.services.AuthenticationService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    factory { AuthInterceptor() }
    factory { HttpLoggingInterceptor() }
    factory { provideOkHttpClient(get(), get()) }
    factory { provideForecastApi(get(named("WeatherApiRetrofit"))) }
    factory { provideSpotifyAuthApi(get(named("SpotifyAuthRetrofit"))) }
    factory { provideSpotifyApi(get(named("SpotifyRetrofit"))) }
    single(named("WeatherApiRetrofit")) { provideRetrofitWeatherApi(get()) }
    single(named("SpotifyAuthRetrofit")) { provideRetrofitSpotifyAuth(get()) }
    single(named("SpotifyRetrofit")) { provideRetrofitSpotify(get()) }
    single {AuthenticationService()}
}

fun provideRetrofitWeatherApi(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BuildConfig.ROOT_URL_WEATHER_API).client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create()).build()
}

fun provideRetrofitSpotifyAuth(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BuildConfig.ROOT_URL_SPOTIFY_AUTH).client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create()).build()
}

fun provideRetrofitSpotify(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BuildConfig.ROOT_URL_SPOTIFY).client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create()).build()
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