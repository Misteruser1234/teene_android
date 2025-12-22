package com.example.teene.di

import UsersRepository
import com.example.teene.data.LandingDataStore
import com.example.teene.data.TokenManager
import com.example.teene.data.network.AuthorizedApiService
import com.example.teene.data.network.NoAuthApiService
import com.example.teene.data.network.AuthInterceptor
import com.example.teene.domain.usecases.CreateUserUseCase
import com.example.teene.domain.usecases.ForgotPasswordUseCase
import com.example.teene.domain.usecases.LoginUseCase
import com.example.teene.events.data.repositories.EventsRepositoryImpl
import com.example.teene.events.domain.usecases.GetEventsUseCase
import com.example.teene.events.presentation.EventsViewModel
import com.example.teene.home.data.repositories.BookRepositoryImpl
import com.example.teene.home.data.repositories.SportsRepositoryImpl
import com.example.teene.home.data.repositories.MockTrainersRepositoryImpl
import com.example.teene.home.data.repositories.TrainersRepository
import com.example.teene.home.data.repositories.TrainersRepositoryImpl
import com.example.teene.home.domain.usecases.GetSportsUseCase
import com.example.teene.home.domain.usecases.GetTrainerAvailabilityUseCase
import com.example.teene.home.domain.usecases.GetTrainersForSportUseCase
import com.example.teene.home.domain.usecases.GetAllTrainersUseCase
import com.example.teene.home.presentation.viewModels.BookingTrainerViewModel
import com.example.teene.home.presentation.viewModels.ExploreViewModel
import com.example.teene.home.presentation.viewModels.SportViewModel
import com.example.teene.home.presentation.viewModels.CoachesFilterViewModel
import com.example.teene.profile.presentation.presentation.ProfileViewModel
import com.example.teene.ui.viewModel.ForgotPasswordViewModel
import com.example.teene.ui.viewModel.LandingViewModel
import com.example.teene.ui.viewModel.LoginViewModel
import com.example.teene.ui.viewModel.RegisterViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val landingModule = module {
    single { LandingDataStore(androidContext()) }
    single { UsersRepository(get()) }
    // Provide the use case
    single { CreateUserUseCase(get()) }
    single { ForgotPasswordUseCase(get()) }
    single { LoginUseCase(get()) }
    single { TokenManager(androidContext()) }
    // UserDataStore to keep authorized user's local data (e.g., user_id)
    single { com.example.teene.data.UserDataStore(androidContext()) }

    viewModel { LandingViewModel(get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
}

val homeModule = module {
    // Provide the ExploreViewModel
    single { SportsRepositoryImpl(get()) }
    single { BookRepositoryImpl(get()) }
    single { GetSportsUseCase(get()) }

    // Features: repo + use case
    single { com.example.teene.home.data.repositories.FeaturesRepositoryImpl(get()) }
    single { com.example.teene.home.domain.usecases.GetFeaturesUseCase(get()) }

    // Bookings: local + remote data sources, repository, use case
    single { com.example.teene.home.data.datasources.BookingsLocalDataSource(get()) }
    single { com.example.teene.home.data.datasources.BookingsRemoteDataSource(get()) }
    single<com.example.teene.home.data.repositories.TrainingBookingsRepository> {
        com.example.teene.home.data.repositories.TrainingBookingsRepositoryImpl(get(), get())
    }
    single { com.example.teene.home.domain.usecases.BookTrainingUseCase(get()) }

    single { GetTrainerAvailabilityUseCase(get()) }
    viewModel { ExploreViewModel(get(), get<com.example.teene.home.domain.usecases.GetFeaturesUseCase>()) }
    viewModel { SportViewModel(get(), get()) }
    viewModel { BookingTrainerViewModel(get(), get<com.example.teene.home.domain.usecases.BookTrainingUseCase>()) }
    viewModel { CoachesFilterViewModel(get(), get(), get()) }

    // Bind TrainersRepository.
    // Toggle by commenting/uncommenting one of the lines below.
    single<TrainersRepository> {
        // Mock implementation (default):
//        MockTrainersRepositoryImpl()

        // Real implementation (uncomment to use the real network-backed repo):
         TrainersRepositoryImpl(get())
    }

    single { GetTrainersForSportUseCase(get()) }
    single { GetAllTrainersUseCase(get()) }
}

val eventsModule = module {
    single { EventsRepositoryImpl(get()) }
    single { GetEventsUseCase(get()) }
    viewModel { EventsViewModel(get()) }
}

// Define a NetworkModule using Koin
val networkModule = module {

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Logs the full request and response body
    }
    // Provide OkHttpClient with Authorization Interceptor
    single { AuthInterceptor(get()) }
    single(named("AuthClient")) {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(get<AuthInterceptor>())
            .build()
    }

    // Provide OkHttpClient without Authorization Interceptor
    single(named("NoAuthClient")) {
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    }

    //     Provide Retrofit instance with Authorization
    single(named("AuthorizedRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://api.staging.tenee.io/") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(get(named("AuthClient")))
            .build()
    }

    // Provide Retrofit instance without Authorization
    single(named("NoAuthRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://api.staging.tenee.io/") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(get(named("NoAuthClient")))
            .build()
    }

    //    // Provide API services for authorized endpoints
    factory { get<Retrofit>(named("AuthorizedRetrofit")).create(AuthorizedApiService::class.java) }

    // Provide API services for unauthorized endpoints
    factory { get<Retrofit>(named("NoAuthRetrofit")).create(NoAuthApiService::class.java) }
}

