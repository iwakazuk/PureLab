package com.purelab.view.itemlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.purelab.models.Category

class ItemListViewModel(application: Application) : AndroidViewModel(application) {
    private val _category = MutableLiveData<Category>(Category())
    val category: LiveData<Category> = _category

    fun setCategory(newCategory: Category) {
        _category.postValue(newCategory)
    }
}