package com.test.rahul.data.repository

import com.test.rahul.data.model.Public
import com.test.rahul.data.proto.AuthUser
import com.test.rahul.data.proto.Authentication
import com.test.rahul.data.source.AccountRemoteSource
import com.test.rahul.injection.RepositoryDispatcher
import com.test.rahul.lifecycle.bo.StringResource
import com.test.rahul.network.meta.Resource
import com.test.rahul.network.meta.ResourceException
import com.test.rahul.network.meta.asResource
import com.test.rahul.persistance.proto.ProtoDataStore
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
