package com.example.progressupdatedemo.di

import com.example.progressupdatedemo.data.AuthenticationRepositoryImpl
import com.example.progressupdatedemo.domain.authentication.AuthenticationRepository
import com.example.progressupdatedemo.domain.use_cases.*
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideFirebaseAuthentication(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): FirestoreRepository = FirestoreRepository(
        firestore, firebaseAuth
    )

    @Singleton
    @Provides
    fun provideAuthenticationRepository(
        firebaseAuth: FirebaseAuth,
    ): AuthenticationRepository = AuthenticationRepositoryImpl(firebaseAuth)

    @Singleton
    @Provides
    fun provideAuthUseCases(authenticationRepositoryImpl: AuthenticationRepositoryImpl) = AuthenticationUseCases(
        isUserAuthenticatedUseCase = IsUserAuthenticatedUseCase(authenticationRepositoryImpl),
        firebaseSignUpUseCase = FirebaseSignUpUseCase(authenticationRepositoryImpl),
        firebaseAuthenticationSignInUseCase = FirebaseAuthenticationSignInUseCase(authenticationRepositoryImpl),
        firebaseAuthenticationSignOutUseCase = FirebaseAuthenticationSignOutUseCase(authenticationRepositoryImpl)
    )
}