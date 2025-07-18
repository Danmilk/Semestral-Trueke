package com.tuempresa.truekeapp.data.model

import androidx.lifecycle.ViewModel

class ItemSharedViewModel : ViewModel() {
    private val itemMap = mutableMapOf<String, Item>()

    fun setItem(item: Item) {
        itemMap[item.id] = item
    }

    fun getItem(itemId: String): Item? = itemMap[itemId]
}