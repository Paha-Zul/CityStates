package com.mygdx.game.objects

class Transaction(val name:String, val amount:Int, val pricePerUnit:Int) {
    val totalPrice:Int
        get() = amount*pricePerUnit

    override fun toString(): String {
        return "$name, A:$amount, total: $totalPrice"
    }
}