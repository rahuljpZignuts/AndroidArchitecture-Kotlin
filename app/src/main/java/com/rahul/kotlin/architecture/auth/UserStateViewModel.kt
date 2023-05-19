package com.rahul.kotlin.architecture.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rahul.kotlin.architecture.core.component.BaseViewModel
import com.rahul.kotlin.architecture.data.proto.AuthUser
import com.rahul.kotlin.architecture.data.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class UserStateViewModel(
    val accountRepository: AccountRepository,
) : BaseViewModel() {
    val authUser: LiveData<AuthUser> = accountRepository.authUserData.asLiveData()

    /**
     * Should be called one time from root activity to clear user resources when user is logged out
     */
    fun clearUserData(
        clearCacheOnMain: () -> Unit,
        clearCacheOnBackground: () -> Unit,
        callback: () -> Unit,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { clearCacheOnBackground() }
            withContext(Dispatchers.Main) { clearCacheOnMain() }
        }
    }

//    /**
//     * Protected method to be used by derived classes to keep logout behavior consistent
//     */
//    protected suspend fun logoutInternal(): Resource<Unit> {
//        return accountRepository.logoutUser()
//    }

//    /**
//     * Can be called from anywhere to log user of the app and invalidate its session
//     */
//    open fun logout() {
//        viewModelScope.launch {
//            logoutInternal()
//        }
//    }
}
