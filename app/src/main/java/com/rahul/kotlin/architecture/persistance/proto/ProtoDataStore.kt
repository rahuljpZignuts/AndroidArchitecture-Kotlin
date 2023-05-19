package com.rahul.kotlin.architecture.persistance.proto

import android.content.Context
import androidx.datastore.core.DataStore
import com.google.protobuf.GeneratedMessageLite
import com.rahul.kotlin.architecture.data.proto.AuthUser
import com.rahul.kotlin.architecture.data.proto.Authentication
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data store to handle locally stored data and their updates. The data store uses protocol buffers
 * to assure type safety. Proto classes schema can be found at src/main/proto.
 */
@Singleton
class ProtoDataStore @Inject constructor(@ApplicationContext context: Context) {

    private val authUserDataStore: DataStore<AuthUser> = context.authUserDataStore
    val authUserData: Flow<AuthUser> = authUserDataStore.data

    val authenticationDataStore: DataStore<Authentication> = context.authenticationDataStore
    val authenticationData: Flow<Authentication> = authenticationDataStore.data

    suspend fun clearStoreData() {
        clearAuthUserData()
    }

    suspend fun saveAuthUserData(
        cachedValue: AuthUser? = null,
        builder: AuthUser.Builder? = cachedValue?.toBuilder(),
        block: (AuthUser.Builder) -> Unit = {},
    ) {
        authUserDataStore.saveData(builder) { apply(block) }
    }

    suspend fun saveAuthenticationData(
        cachedValue: Authentication? = null,
        builder: Authentication.Builder? = cachedValue?.toBuilder(),
        block: (Authentication.Builder) -> Unit = {},
    ) {
        authenticationDataStore.saveData(builder) { apply(block) }
    }

    private suspend fun clearAuthUserData() {
        authUserDataStore.clearData()
    }
}

private typealias ProtoLite<M, B> = GeneratedMessageLite<M, B>
private typealias ProtoBuilder<M, B> = GeneratedMessageLite.Builder<M, B>

private fun <T> DataStore<T>.getData(defaultInstance: T): Flow<T> = data.catch { exception ->
    // An error encountered when reading data
    if (exception is IOException) {
        Timber.e(exception, "Error reading app info data")
        emit(defaultInstance)
    } else throw exception
}

private suspend inline fun <M : ProtoLite<M, B>, B : ProtoBuilder<M, B>> DataStore<M>.saveData(
    builder: B?,
    crossinline block: B.(M) -> Unit,
) = updateData { data ->
    with(builder ?: data.toBuilder()) {
        block(this, data)
        build()
    }
}

private suspend fun <M : ProtoLite<M, B>, B : ProtoBuilder<M, B>> DataStore<M>.clearData() {
    saveData(builder = null) { clear() }
}
