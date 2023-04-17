package com.example.kotlinstudy.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

//定义DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_info")

object DataStoreUtils {


    //定义key
    val keyName = stringPreferencesKey("name")
    val keyAge = intPreferencesKey("age")

    suspend inline fun< reified T : Any> getData(context: Context): T {
        //dataStore获取数据，collect是一个挂起函数，所以会一直挂起，只要name的值发起变更，collect 就会回调
        val nameFlow = context.dataStore.data.map {
            it[keyName] ?: ""
        }
        nameFlow.collect { name ->
            Log.d("datastore", "name $name")
        }
        return nameFlow as T

    }
    //  ☕️
}