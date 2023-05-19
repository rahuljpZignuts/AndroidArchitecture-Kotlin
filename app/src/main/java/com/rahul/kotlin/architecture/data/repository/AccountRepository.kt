package com.rahul.kotlin.architecture.data.repository

import com.rahul.kotlin.architecture.data.model.Public
import com.rahul.kotlin.architecture.data.proto.AuthUser
import com.rahul.kotlin.architecture.data.proto.Authentication
import com.rahul.kotlin.architecture.data.source.AccountRemoteSource
import com.rahul.kotlin.architecture.injection.RepositoryDispatcher
import com.rahul.kotlin.architecture.lifecycle.bo.StringResource
import com.rahul.kotlin.architecture.network.meta.Resource
import com.rahul.kotlin.architecture.network.meta.ResourceException
import com.rahul.kotlin.architecture.network.meta.asResource
import com.rahul.kotlin.architecture.persistance.proto.ProtoDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Account repository responsible for handling all user operations e.g. login, registration,
 * password updates, profile updates, etc.
 */
@Singleton
class AccountRepository @Inject constructor(
    @RepositoryDispatcher private val dispatcher: CoroutineDispatcher,
    private val remoteSource: AccountRemoteSource,
    private val dataStore: ProtoDataStore,
) {
    val authenticationData: Flow<Authentication> = dataStore.authenticationData
    val authUserData: Flow<AuthUser> = dataStore.authUserData

    suspend fun login(): Resource<Public> {
        val response = remoteSource.login()
        dataStore.saveAuthenticationData(builder = Authentication.newBuilder()) {
            it.accessToken = response.data?.entries?.firstOrNull()?.api.toString()
        }
        return response.asResource() ?: ResourceException.ResourceNotFound(
            StringResource.create(response.message)
        ).asResource(response.requestState)
    }

    suspend fun getEntries() : Resource<Public>{
        val response = remoteSource.getEntries()
        return response.asResource() ?: ResourceException.ResourceNotFound(
            StringResource.create(response.message)
        ).asResource(response.requestState)
    }
}
