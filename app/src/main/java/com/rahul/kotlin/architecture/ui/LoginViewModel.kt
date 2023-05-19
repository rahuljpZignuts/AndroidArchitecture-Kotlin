package com.rahul.kotlin.architecture.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rahul.kotlin.architecture.core.component.BaseViewModel
import com.rahul.kotlin.architecture.data.enums.RequestState
import com.rahul.kotlin.architecture.data.model.Public
import com.rahul.kotlin.architecture.data.repository.AccountRepository
import com.rahul.kotlin.architecture.extension.dispatchError
import com.rahul.kotlin.architecture.network.meta.Resource
import com.rahul.kotlin.architecture.network.meta.value
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : BaseViewModel() {
    val userToken = accountRepository.authenticationData.asLiveData()

    private val _entries: MutableLiveData<Public> = MutableLiveData()
    val entries: LiveData<Public> = _entries

    private val _requestState: MutableLiveData<RequestState> = MutableLiveData()
    val requestState: LiveData<RequestState> = _requestState

    fun login() {
        viewModelScope.launch {
            accountRepository.login()
        }
    }

    fun getEntries() {
        viewModelScope.launch {
            _requestState.value = RequestState.IN_PROGRESS
            val resource = accountRepository.getEntries()
            when (resource) {
                is Resource.Success -> _entries.value = resource.value
                is Resource.Error -> dispatchError(resource)
            }
            _requestState.value = resource.state
        }
    }
}
