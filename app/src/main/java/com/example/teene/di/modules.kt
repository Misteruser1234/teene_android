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
import com.example.teene.home.data.repositories.ImagesRepository
import com.example.teene.home.data.repositories.ImagesRepositoryImpl
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
import com.example.teene.authentication.register.trainer.RegisterTrainerImagesViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File


val landingModule = module {
    // Shared coroutine scope for DataStore
    single { CoroutineScope(Dispatchers.IO + SupervisorJob()) }

    // Singleton Preferences DataStores
    single<DataStore<Preferences>>(named("userPrefs")) {
        PreferenceDataStoreFactory.create(
            scope = get(),
            produceFile = { File(androidContext().filesDir, "user_prefs.preferences_pb") }
        )
    }
    single<DataStore<Preferences>>(named("appPrefs")) {
        // Keep the original file name to preserve existing data
        PreferenceDataStoreFactory.create(
            scope = get(),
            produceFile = { File(androidContext().filesDir, "application_prefferences.preferences_pb") }
        )
    }

    // Singletons that depend on DataStore singletons
    single { LandingDataStore(get(named("appPrefs"))) }
    single { UsersRepository(get(), get()) }
    // Provide the use case
    single { CreateUserUseCase(get()) }
    single { ForgotPasswordUseCase(get()) }
    single { LoginUseCase(get()) }
    single { TokenManager(get(named("userPrefs"))) }
    // UserDataStore to keep authorized user's local data (e.g., user_id)
    single { com.example.teene.data.UserDataStore(get(named("userPrefs"))) }

    viewModel { LandingViewModel(get(), get(), get()) }
    viewModel { RegisterViewModel(get(), get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get(), get()) }
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
    single { com.example.teene.home.domain.usecases.CreateTrainerUseCase(get()) }

    // Bookings: local + remote data sources, repository, use case
    single { com.example.teene.home.data.datasources.BookingsLocalDataSource(get()) }
    single { com.example.teene.home.data.datasources.BookingsRemoteDataSource(get()) }
    single<com.example.teene.home.data.repositories.TrainingBookingsRepository> {
        com.example.teene.home.data.repositories.TrainingBookingsRepositoryImpl(get(), get())
    }
    single { com.example.teene.home.domain.usecases.BookTrainingUseCase(get()) }
    single { com.example.teene.home.domain.usecases.GetTrainingBookingsUseCase(get()) }

    // Images: repository for upload/delete
    single<ImagesRepository> { ImagesRepositoryImpl(get(), androidContext().contentResolver) }

    single { GetTrainerAvailabilityUseCase(get()) }
    viewModel { ExploreViewModel(get(), get<com.example.teene.home.domain.usecases.GetFeaturesUseCase>()) }
    viewModel { SportViewModel(get(), get()) }
    viewModel { BookingTrainerViewModel(get(), get<com.example.teene.home.domain.usecases.BookTrainingUseCase>()) }
    viewModel { CoachesFilterViewModel(get(), get(), get()) }
    viewModel { com.example.teene.mysessions.presentation.MySessionsViewModel(get()) }

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

    // ViewModels
    viewModel { RegisterTrainerImagesViewModel(get(), get()) }
    viewModel { com.example.teene.authentication.register.trainer.RegisterTrainerViewModel(get(), get(), get(), get()) }
}

val eventsModule = module {
    single { EventsRepositoryImpl(get()) }
    single { GetEventsUseCase(get()) }
    viewModel { EventsViewModel(get()) }
}

val inboxModule = module {
    single { com.example.teene.inbox.data.QuickbloxManager(androidContext(), get(), get()) }
    single { com.example.teene.inbox.data.InboxRepository() }
    viewModel { com.example.teene.inbox.presentation.InboxViewModel(get(), get()) }
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

