@file:Suppress("UNCHECKED_CAST")

package com.example.progressupdatedemo.core.utils

import com.example.progressupdatedemo.data.Response
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.gson.Gson
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import java.text.SimpleDateFormat
import java.util.*

fun formatTimestamp(timestamp: Timestamp): String {
    // Feb 16, 2023 09:53
    val simpleDateFormat = SimpleDateFormat("EEE dd, yyyy HH:mm", Locale.ENGLISH)
    val date = timestamp.toDate()
    return simpleDateFormat.format(date).toString()
}

fun <T> Response<T>.dataIsNotNullOrEmpty() = this.data != null && this.data.toString().isNotEmpty()

fun <A> String.fromJsonToObject(type: Class<A>): A = Gson().fromJson(this, type)

fun String.isValid(): Boolean = this.trim().isNotEmpty()

fun <T, C> ProducerScope<Response<T>>.getCollectionSnapshotListener(
    collectionRef: CollectionReference,
    type: Class<C>,
) = collectionRef.addSnapshotListener { snapshot, exception ->
    val response = buildResponseBasedOnCollectionSnapshot<T, C>(snapshot, exception, type)
    trySend(response).isSuccess
}

private fun <T, C> buildResponseBasedOnCollectionSnapshot(
    snapshot: QuerySnapshot?,
    exception: FirebaseFirestoreException?,
    type: Class<C>,
) = if (snapshot != null) {
    buildSuccessResponseWithObjects<T, C>(snapshot, type)
} else {
    buildErrorResponse(exception?.message ?: exception.toString())
}

private fun <T, C> buildSuccessResponseWithObjects(
    snapshot: QuerySnapshot,
    type: Class<C>,
): Response.Success<T> {
    val notes = mapSnapshotToObjects(snapshot, type) as T
    return buildSuccessResponse(notes)
}

private fun <C> mapSnapshotToObjects(snapshot: QuerySnapshot, type: Class<C>): List<C> =
    snapshot.toObjects(type)

fun <T> buildSuccessResponse(data: T) = Response.Success(data)

fun <T> buildErrorResponse(errorMessage: String): Response.Error<T> =
    Response.Error(message = errorMessage)

fun <T, C> ProducerScope<Response<T>>.getDocumentSnapshotListener(
    documentRef: DocumentReference,
    type: Class<C>,
) = documentRef.addSnapshotListener { snapshot, exception ->
    val response = buildResponseBasedOnDocumentSnapshot<T, C>(snapshot, exception, type)
    trySend(response).isSuccess
}

private fun <T, C> buildResponseBasedOnDocumentSnapshot(
    snapshot: DocumentSnapshot?,
    exception: FirebaseFirestoreException?,
    type: Class<C>,
) = if (snapshot != null && snapshot.exists()) {
    buildSuccessResponseWithObject<T, C>(snapshot, type)
} else if (snapshot != null && !snapshot.exists()) {
    buildLoadingResponse()
} else {
    buildErrorResponse(exception?.message ?: exception.toString())
}

private fun <T, C> buildSuccessResponseWithObject(
    snapshot: DocumentSnapshot,
    type: Class<C>,
): Response.Success<T> {
    val notes = mapSnapshotToObject(snapshot, type) as T
    return buildSuccessResponse(notes)
}

private fun <C> mapSnapshotToObject(snapshot: DocumentSnapshot, type: Class<C>): C =
    snapshot.toObject(type)!!

private fun <T> buildLoadingResponse(): Response.Loading<T> = Response.Loading()

suspend fun <T> ProducerScope<Response<T>>.removeSnapshotListenerOnClose(
    snapshotListener: ListenerRegistration,
) = awaitClose { removeSnapshotListener(snapshotListener) }

private fun removeSnapshotListener(snapshotListener: ListenerRegistration) =
    snapshotListener.remove()
