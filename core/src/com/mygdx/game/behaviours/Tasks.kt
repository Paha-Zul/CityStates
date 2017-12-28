package com.mygdx.game.behaviours

import com.mygdx.game.behaviours.leaf.*

object Tasks {
    fun gatherTask(bb:BlackBoard):Task{
        val seq = Sequence(bb)

        seq.controller.addTasks(
            FindClosestSupply(bb),
            ChangeHidden(bb, false),
            MoveTo(bb),
            HarvestResource(bb),
            SetMyTownAsTarget(bb),
            MoveTo(bb),
            ChangeHidden(bb, true),
            TransferInventoryToTown(bb, true)
        )
        //Find closest supply
        //Walk to closest supply
        //Gather closest supply
        //Set target as my town
        //Walk back to town
        //Deposit resource

        return seq
    }

    fun processorTask(bb:BlackBoard):Task{
        val selector = Selector(bb)

        val craftSeq = Sequence(bb, "Crafting Item")
        val buyItemSeq = Sequence(bb, "Buying Item")

        val buyFromMyOrOtherTownSel = Selector(bb, "Buying Item")

        val buyItemFromMyTownSeq = Sequence(bb, "Buying item from my town")
        val buyItemFromOtherTownSeq = Sequence(bb, "Buying item from other town")

        craftSeq.controller.addTasks(
            CraftItem(bb)
        )

        //If we're buying from our own town, we simply want to transfer the money and take the item
        buyItemFromMyTownSeq.controller.addTasks(
                CheckIfTargetIsMyTown(bb), //This will be the gatekeeper. If this fails we move to buying from another town
                TakeTargetItemFromTargetTown(bb),
                SetMyTownAsTarget(bb),
                TransferMoneyForPurchase(bb, false),
                TransferInventoryToTown(bb)
        )

        //If we're buying from another town
        buyItemFromOtherTownSeq.controller.addTasks(
                ChangeHidden(bb, false),
                MoveTo(bb),
                TransferMoneyForPurchase(bb, false),
                TakeTargetItemFromTargetTown(bb),
                SetMyTownAsTarget(bb),
                MoveTo(bb),
                TransferInventoryToTown(bb, false)
        )

        //A selector to either buy from my own town or another town
        buyFromMyOrOtherTownSel.controller.addTasks(
                buyItemFromMyTownSeq,
                buyItemFromOtherTownSeq
        )

        //This is the buy items sequence
        buyItemSeq.controller.addTasks(
                FindClosestSupply(bb, false),
                SetIncomingAndOutgoingItem(bb),
                TransferMoneyForPurchase(bb, true),
                buyFromMyOrOtherTownSel
        )

        //Try to craft and then try to buy items
        selector.controller.addTasks(craftSeq, buyItemSeq)
        /*
        Selector
            - Sequence
                - Craft an item
            - Sequence
                - Find nearest supply of lowest item
                - Get path to supply
                - Unhide
                - Reserve gold to buy supply
                - Move to closest supply
                - Buy Supply
                - Move back to town
                - Hide
                - Transfer items back to town
            - Idle
         */

        return selector
    }
}