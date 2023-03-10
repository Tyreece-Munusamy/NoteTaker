package com.example.progressupdatedemo.di

import com.example.progressupdatedemo.domain.repository.impl.AuthenticationRepositoryImpl
import com.example.progressupdatedemo.domain.repository.impl.NoteRepositoryImpl
import com.example.progressupdatedemo.domain.repository.impl.UserRepositoryImpl
import com.example.progressupdatedemo.domain.repository.AuthenticationRepository
import com.example.progressupdatedemo.domain.repository.NoteRepository
import com.example.progressupdatedemo.domain.repository.UserRepository
import com.example.progressupdatedemo.domain.use_case.*
import com.example.progressupdatedemo.domain.use_case.authentication.*
import com.example.progressupdatedemo.domain.use_case.note.*
import com.example.progressupdatedemo.domain.use_case.user.*
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
    fun provideAuthenticationRepository(
        firebaseAuth: FirebaseAuth,
    ): AuthenticationRepository = AuthenticationRepositoryImpl(firebaseAuth)

    @Singleton
    @Provides
    fun provideAuthUseCases(authenticationRepositoryImpl: AuthenticationRepositoryImpl) =
        AuthenticationUseCases(
            isUserAuthenticated = IsUserAuthenticated(authenticationRepositoryImpl),
            signUp = SignUp(authenticationRepositoryImpl),
            signIn = SignIn(authenticationRepositoryImpl),
            signOut = SignOut(authenticationRepositoryImpl)
        )

    @Singleton
    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): UserRepository = UserRepositoryImpl(firebaseAuth, firestore)

    @Singleton
    @Provides
    fun provideUserUseCases(userRepositoryImpl: UserRepositoryImpl) =
        UserUseCases(
            createUserUseCase = CreateUserUseCase(userRepositoryImpl),
            getUserUseCase = GetUserUseCase(userRepositoryImpl),
            updateUserUseCase = UpdateUserUseCase(userRepositoryImpl)
        )

    @Singleton
    @Provides
    fun provideNoteRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): NoteRepository = NoteRepositoryImpl(firebaseAuth, firestore)

    @Singleton
    @Provides
    fun provideNoteUseCases(noteRepositoryImpl: NoteRepositoryImpl) =
        NoteUseCases(
            createNoteUseCase = CreateNoteUseCase(noteRepositoryImpl),
            getNotesUseCase = GetNotesUseCase(noteRepositoryImpl),
            getNoteUseCase = GetNoteUseCase(noteRepositoryImpl),
            updateNoteUseCase = UpdateNoteUseCase(noteRepositoryImpl),
            deleteNoteUseCase = DeleteNoteUseCase(noteRepositoryImpl)
        )
}