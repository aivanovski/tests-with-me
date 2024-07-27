package com.github.aivanovski.testswithme.android.presentation.core.decompose

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class ViewModelStoreOwnerImpl : ViewModelStoreOwner {

    override val viewModelStore: ViewModelStore = ViewModelStore()
}