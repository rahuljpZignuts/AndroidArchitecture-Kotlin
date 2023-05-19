package com.rahul.kotlin.architecture.core.component

import androidx.databinding.ViewDataBinding

/**
 * AuthBaseFragment that should be used as parent class by all auth related fragment i.e.'LOGIN' and 'REGISTRATION'
 * This can be helpful in providing boiler plate code and basic functionality like social login, etc.
 */
abstract class AuthBaseFragment<VB : ViewDataBinding> : DataBindingFragment<VB>() {

}
