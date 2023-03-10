package com.example.progressupdatedemo.presentation.screen.update

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.domain.model.User
import com.example.progressupdatedemo.domain.use_case.note.NoteUseCases
import com.example.progressupdatedemo.domain.use_case.user.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateScreenViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val noteUseCases: NoteUseCases,
) : ViewModel() {

    private val _isProcessingUpdateRequest = mutableStateOf(false)
    val isProcessingUpdateRequest = _isProcessingUpdateRequest

    private val _getUserUseCaseResponseState = mutableStateOf<Response<User>>(Response.Loading())
    val getUserUseCaseResponseState = _getUserUseCaseResponseState

    fun getUser() = viewModelScope.launch {
        collectResponseFromGetUserUseCase()
    }

    fun isUserUpdate(user: User, firstName: String, lastName: String) =
        firstName != user.firstName || lastName != user.lastName

    private suspend fun collectResponseFromGetUserUseCase() =
        invokeGetUserUseCase().collect { _getUserUseCaseResponseState.value = it }

    private fun invokeGetUserUseCase() = userUseCases.getUserUseCase.invoke()

    fun updateNote(updatedNote: Note) = viewModelScope.launch {
        _isProcessingUpdateRequest.value = true
        invokeUpdateNoteUseCase(updatedNote)
    }

    private suspend fun invokeUpdateNoteUseCase(updatedNote: Note) =
        noteUseCases.updateNoteUseCase.invoke(updatedNote)

    fun updateUser(id: String, firstName: String, lastName: String, email: String) =
        viewModelScope.launch {
            isProcessingUpdateRequest.value = true
            val updatedUser = User(id, firstName, lastName, email)
            invokeUpdateUserUseCase(updatedUser)
        }

    private suspend fun invokeUpdateUserUseCase(updatedUser: User) =
        userUseCases.updateUserUseCase.invoke(updatedUser)

}