package com.example.kotlinicecreamapp.todo.data.remote

import com.example.kotlinicecreamapp.todo.data.IceCream

data class IceCreamEvent(val type: String, val payload: IceCream)