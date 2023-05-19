package com.test.rahul.data.source

import com.test.rahul.data.model.Public
import com.test.rahul.injection.DataSourceDispatcher
import com.test.rahul.network.NetworkExecutor
import com.test.rahul.network.meta.DataRequest
import com.test.rahul.network.meta.DataResponse
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
