package com.codingwithmitch.kotlinrecyclerviewexample.protocols


interface UIUpdaterInterface {

    fun resetUIWithConnection(status: Boolean)
    fun updateStatusViewWith(status: String)
    fun update(message: String)
}