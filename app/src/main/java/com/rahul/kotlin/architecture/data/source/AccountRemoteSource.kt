package com.rahul.kotlin.architecture.data.source

import com.rahul.kotlin.architecture.data.model.Public
import com.rahul.kotlin.architecture.injection.DataSourceDispatcher
import com.rahul.kotlin.architecture.network.NetworkExecutor
import com.rahul.kotlin.architecture.network.meta.DataRequest
import com.rahul.kotlin.architecture.network.meta.DataResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source to handle network updates for operations related with account actions.
 */
@Singleton
class AccountRemoteSource @Inject constructor(
    @DataSourceDispatcher private val dispatcher: CoroutineDispatcher,
    private val networkExecutor: NetworkExecutor<AccountService>,
) {
    suspend fun login(): DataResponse<Public> =
        withContext(dispatcher) {
            return@withContext networkExecutor.execute(DataRequest) {
                login()
            }
        }

    suspend fun getEntries() : DataResponse<Public> =
        withContext(dispatcher){
            return@withContext networkExecutor.execute(DataRequest) {
                getEntries()
            }
        }
}
