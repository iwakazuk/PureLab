package com.purelab.view.itemlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ItemListViewModel(application: Application) : AndroidViewModel(application) {
    private val _category = MutableLiveData<String>("")
    val category: LiveData<String> = _category

    fun setCategory(newCategory: String) {
        _category.postValue(newCategory)
    }
}