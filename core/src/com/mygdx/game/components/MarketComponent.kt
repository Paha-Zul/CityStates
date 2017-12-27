package com.mygdx.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.MathUtils
import com.mygdx.game.objects.InventoryItem
import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.objects.Transaction

/***
 * Probably will be pretty similar to the inventory component but with specialized functions
 */
class MarketComponent : Component {
    private var marketMoney = 50000

    private val itemMap = hashMapOf<String, InventoryItem>()
    private val itemPriceMap = hashMapOf<String, Int>()

    /**
     * Sells an item to the market
     */
    fun sellItemToMarket(itemName:String, itemAmount:Int): Transaction {
        val item = itemMap.getOrPut(itemName, { InventoryItem(itemName, 0) })

        //TODO Implement this!
        //Get current average item price (or calculate it based on how much they are selling)
        //Check if the market has enough money to supply them?
        //Check if the market has enough of the item (get the lowest of this or the above)
        //Exchange the money for goods
        //Be on our merry way. Update average prices?

        //But for now we're just gonna lay down some temp code
        val itemPrice = getAveragePriceForSellingToMarket(itemName, itemAmount) //Get the item price
        //The amount the market can take. Either all of the item or the amount bound by the market money
        val amountAbleToSell = Math.min(itemAmount, marketMoney/itemPrice)
        val totalPrice = amountAbleToSell*itemPrice //Get the total price for the transaction

        marketMoney -= totalPrice //Subtract this from the market money
        item.amount += amountAbleToSell //Add the item amount to the market (we are selling TO the market)

        return Transaction(itemName, amountAbleToSell, itemPrice)
    }

    /**
     * Buys an item from the market
     */
    fun buyItemFromMarket(itemName:String, itemAmount:Int): Transaction {
        val item = itemMap[itemName] ?: return Transaction(itemName, 0, 0)

        //But for now we're just gonna lay down some temp code
        val itemPrice = getAveragePriceForBuyingFromMarket(itemName, itemAmount)
        //The amount the market can give away. Either all of the inventory item or the amount requested by the buyer
        val amountAbleToBuy = Math.min(item.amount, itemAmount)
        val totalPrice = amountAbleToBuy*itemPrice //Get the total price for the transaction

        marketMoney += totalPrice //Add the money to the market
        item.amount -= amountAbleToBuy //Subtract the item from the market

        return Transaction(itemName, amountAbleToBuy, itemPrice)
    }

    /**
     * Calculates the average price when selling items to the market
     * @param itemName The name of the item
     * @param itemAmount The amount of the item
     */
    fun getAveragePriceForSellingToMarket(itemName:String, itemAmount:Int):Int{
        //TODO Actually implement this
        return itemPriceMap.getOrPut(itemName, {DefinitionManager.resourceMap[itemName]!!.marketPrice}) //Get the price of the item
    }

    /**
     * Calculates the average price when buying items from the market
     * @param itemName The name of the item
     * @param itemAmount The amount of the item
     */
    fun getAveragePriceForBuyingFromMarket(itemName:String, itemAmount:Int):Int{
        //TODO Actually implement this
        return itemPriceMap.getOrPut(itemName, {DefinitionManager.resourceMap[itemName]!!.marketPrice}) //Get the price of the item
    }

    /**
     * Reserves an item for pickup in the market (outgoing)
     */
    fun reserveOutgoingItem(itemName:String, itemAmount:Int):Int{
        val item = itemMap.getOrPut(itemName, {InventoryItem(itemName, 0)})

        //Clamp this between the negative outgoing and the total amount we have
        //The negative outgoing allows us to accept negative numbers to reduce the outgoing amount
        val amountToReserve = MathUtils.clamp(itemAmount, -item.outgoing, item.amount)
        item.outgoing += amountToReserve //Add to the outgoing
        item.amount -= amountToReserve //Take away from the available amount
        //If the item is completely empty, remove it
//        if(item.outgoing == 0 && item.amount == 0 && item.incoming == 0)
//            itemMap.remove(itemName)
        return amountToReserve
    }

    /**
     * Reserves an incoming item to the market
     */
    fun reserveIncomingItem(itemName:String, itemAmount:Int):Int{
        val item = itemMap.getOrPut(itemName, { InventoryItem(itemName, 0) })

        //We can't take away more the than the item incoming, but we can add MAX_VALUE (infinite basically)
        val amountToReserve = MathUtils.clamp(itemAmount, -item.incoming, Int.MAX_VALUE)

        item.incoming += amountToReserve
        return amountToReserve
    }

    fun getAmountInclIncoming(name:String):Int{
        if(hasItem(name)) {
            val item = itemMap[name]!!
            return item.amount + item.incoming
        }
        return 0
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

    fun getPriceOfItem(itemName:String):Int{
        return itemPriceMap.getOrPut(itemName, {DefinitionManager.resourceMap[itemName]!!.marketPrice})
    }

    fun getAllItems():List<InventoryItem>{
        return itemMap.values.toList()
    }
}