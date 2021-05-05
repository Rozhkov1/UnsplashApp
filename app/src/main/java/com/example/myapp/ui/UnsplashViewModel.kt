package com.example.myapp.ui

import  android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.adapter.RowItemType
import com.example.myapp.adapter.TextItem
import com.example.myapp.models.UnsplashModel
import com.example.myapp.usecases.GetPhotosUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.*

class UnsplashViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    val listLiveData = MutableLiveData<List<RowItemType>>()
    val errorLiveData = MutableLiveData<String>()
    val isLoadingLiveData = MutableLiveData<Boolean>()

    private val errorHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { context, throwable ->
            Log.e("UnsplashViewModel", "", throwable)
            throwable.message?.also {
                errorLiveData.value = it
                isLoadingLiveData.value = false
            }
        }

    init {
        loadData()
    }

        fun loadData() {
            viewModelScope.launch(errorHandler) {
                isLoadingLiveData.value = true
                val list = getPhotosUseCase.execute()
                if (list.isNullOrEmpty()) {
                    errorLiveData.value = "List is empty"
                    isLoadingLiveData.value = false
                } else {
                    val listImages = ArrayList<RowItemType>(list).apply {
                        add(0, TextItem("Hello"))
                        add(TextItem("Bye"))
                    }
                    listLiveData.value = listImages
                    isLoadingLiveData.value = false
                }
            }
        }


    //fun from MainActivity
    fun updateValue(model: UnsplashModel) {
        viewModelScope.launch {

            val list = listLiveData.value ?: return@launch
            val rowItemList: List<RowItemType> = list.map {
                when (it) {
                    is UnsplashModel -> if (it.id == model.id) {
                        changeModel(it)
                    } else {
                        it
                    }
                    else -> it
                }
            }
            listLiveData.value = rowItemList
        }
    }

    private fun changeModel(model: UnsplashModel): UnsplashModel {
        return if (model.isLiked) {
            model.copy(likesNumber = model.likesNumber - 1, isLiked = false)
        } else {
            model.copy(likesNumber = model.likesNumber + 1, isLiked = true)
        }
    }
}