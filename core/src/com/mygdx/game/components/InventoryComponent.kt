package com.mygdx.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.mygdx.game.objects.InventoryItem

/**
 * Created by Paha on 12/14/2016.
 */
class InventoryComponent : Component {
    val itemMap = hashMapOf<String, InventoryItem>()

    /** A list of listeners for inventory changes.*/
    private val inventoryChangeListeners:HashMap<String, Array<(itemName:String, amtChanged:Int, amtFinal:Int)->Unit>> = hashMapOf()

    val isEmpty:Boolean
        get() = itemMap.isEmpty()

    /**
     * @param itemName The itemName of the item to add.
     * @param amount The amount to add.
     * @return The amount added.
     */
    fun addItem(itemName:String, amount:Int = 1):Int{
        if(amount < 1)
            return 0

        val item = itemMap.getOrPut(itemName, { InventoryItem(itemName, 0) })
        item.amount += amount

        inventoryChangeListeners[itemName]?.forEach { it(itemName, amount, item.amount) }
        inventoryChangeListeners["all"]?.forEach { it(itemName, amount, item.amount) } //Empty quotes signifies for any item

        return amount
    }

    /**
     * @param name The name of the item to remove
     * @param amount The amount to remove. -1 indicates all of the item.
     * @return The amount that was removed
     */
    fun removeItem(name:String, amount:Int = 1):Int{
        var amount = amount //This makes this mutable
        if(amount < 0)
            amount = getAmount(name)

        val item = itemMap[name]
        if(item != null){
            val amountTaken = if(item.amount - amount < 0) item.amount else amount

            item.amount -= amount
            if(item.amount <= 0) {
                item.amount = 0
                itemMap.remove(name)
            }

            //Call any listeners... The amountTaken needs to be negative here
            inventoryChangeListeners[name]?.forEach { it(name, -amountTaken, item.amount) }
            inventoryChangeListeners["all"]?.forEach { it(name, -amountTaken, item.amount) }

            return amountTaken
        }
        return 0
    }

    /**
     * Reserves an outgoing item. This affects the overall item's amount and therefore should not be used when actually
     * taking an item that is outgoing.
     * @param name The name of the item
     * @param amount The amount to modify. Negative numbers represent taking away from the outgoing item stack. Positive
     * represents adding to it
     * @return An Int representing the total amount the was modified.
     */
    fun reserveOutgoingItem(name:String, amount:Int):Int{
        val item = itemMap.getOrPut(name, {InventoryItem(name, 0)})

        //Clamp this between the negative outgoing and the total amount we have
        //The negative outgoing allows us to accept negative numbers to reduce the outgoing amount
        val amountToReserve = MathUtils.clamp(amount, -item.outgoing, item.amount)
        item.outgoing += amountToReserve //Add to the outgoing
        item.amount -= amountToReserve //Take away from the available amount
        //If the item is completely empty, remove it
        if(item.outgoing == 0 && item.amount == 0 && item.incoming == 0)
            itemMap.remove(name)
        return amountToReserve
    }

    /**
     * Takes a reserved outgoing item.
     * @param name The name of the item
     * @param amount The amount to take
     * @return An Int representing the total amount the was taken.
     */
    fun takeOutgoingItem(name:String, amount:Int) : Int{
        val item = itemMap.getOrPut(name, {InventoryItem(name, 0)})

        //Clamp this between 0 and the max outgoing amount
        val amountToReserve = MathUtils.clamp(amount, 0, item.outgoing)
        item.outgoing -= amountToReserve //Remove the amount from outgoing
        //If the item is completely empty, remove it
        if(item.outgoing == 0 && item.amount == 0 && item.incoming == 0)
            itemMap.remove(name)
        return amountToReserve
    }

    /**
     * Modify an outgoing item
     * @param name The name of the item
     * @param amount The amount to modify. Negative numbers represent taking away from the incoming item stack. Positive
     * represents adding to it
     * @return An Int representing the total amount the was modified.
     */
    fun reserveIncomingItem(name:String, amount:Int):Int{
        val item = itemMap.getOrPut(name, { InventoryItem(name, 0) })

        //We can't take away more the than the item incoming, but we can add MAX_VALUE (infinite basically)
        val amountToReserve = MathUtils.clamp(amount, -item.incoming, Int.MAX_VALUE)

        item.incoming += amountToReserve
        return amountToReserve
    }

    /**
     * @param name The name of the item
     * @return True if the item is in the inventory, false otherwise.
     */
    fun hasItem(name:String):Boolean{
        return itemMap.containsKey(name)
    }

    /**
     * Gets the item amount if available
     * @param name The name of the item
     * @return The amount of the item or 0 if the item doesn't exist.
     */
    fun getAmount(name:String):Int{
        if(hasItem(name))
            return itemMap[name]!!.amount

        return 0
    }

    fun getAmountInclIncoming(name:String):Int{
        if(hasItem(name)) {
            val item = itemMap[name]!!
            return item.amount + item.incoming
        }
        return 0
    }

    fun addInventoryListener(itemName:String, listener:(itemName:String, amountChanged:Int, itemFinalAmount:Int) -> Unit) :
            (itemName:String, amountChanged:Int, itemFinalAmount:Int) -> Unit{
        inventoryChangeListeners.getOrPut(itemName, {Array()}).add(listener)

        return listener
    }

    fun removeInventoryListener(itemName:String, listener: (itemName: String, amountChanged: Int, itemFinalAmount: Int) -> Unit){
        inventoryChangeListeners[itemName]?.removeValue(listener, true)
    }
}