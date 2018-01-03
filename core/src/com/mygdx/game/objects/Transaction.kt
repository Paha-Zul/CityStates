package com.mygdx.game.objects

class Transaction(val name:String, val amount:Int, val pricePerUnit:Float) {
    val totalPrice:Float
        get() = amount*pricePerUnit

    override fun toString(): String {
        return "$name, A:$amount, total: $totalPrice"
    }
}