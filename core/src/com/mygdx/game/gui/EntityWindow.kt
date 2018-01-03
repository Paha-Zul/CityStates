package com.mygdx.game.gui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.mygdx.game.util.DefinitionManager
import com.mygdx.game.util.Mappers
import com.mygdx.game.MyGame
import com.mygdx.game.components.MarketComponent
import com.mygdx.game.util.Util
import com.mygdx.game.objects.InventoryItem
import java.math.BigDecimal
import java.text.DecimalFormat

class EntityWindow(val entity: Entity) : GUIWindow() {
    val mainTable = Table()
    val formatter = DecimalFormat("#.00")

    init {
        mainTable.setSize(400f, 600f)
        mainTable.setPosition(200f, 300f)
        mainTable.background = TextureRegionDrawable(TextureRegion(Util.createPixel(Color(Color.GRAY).apply { a = 0.5f })))

        val town = Mappers.town[entity]
        val market = Mappers.market[entity]

        val labelStyle = Label.LabelStyle(MyGame.defaultFont12, Color.WHITE)

        val nameLabel = Label(Mappers.identity[entity].name, labelStyle)

        mainTable.add(nameLabel).left().pad(10f, 10f, 0f, 0f).row()

        val sortedWorkerList = town.workers.values.toList().toList().sortedBy { DefinitionManager.workerMap[it.type]!!.initialCost }

        sortedWorkerList.forEach { group ->
            val groupTable = Table()

            val groupNameLabel = Label("Group: ${group.type}, Amount: ${group.workers.size}, Gold: ${group.gold}, DA: 0", labelStyle)

            val dividerLabel = Label("----------------------", labelStyle)

            val itemTable = makeInventoryTable(group.resources.itemMap.values.toList(), labelStyle)

            groupTable.add(groupNameLabel).row()
            groupTable.add(dividerLabel).row()
            groupTable.add(itemTable).row()

            mainTable.add(groupTable).grow().row()

            //This update function will update the inventory table constantly
            updateFuncs.add {
                makeInventoryTable(group.resources.itemMap.values.toList(), labelStyle, itemTable)
                val dailyAverage = group.dailyTransactionAverages.sumBy { it } / Math.max(group.dailyTransactionAverages.size, 1) //max is to make sure that we don't have 0
                groupNameLabel.setText("Group: ${group.type}, Amount: ${group.workers.size}, Gold: ${group.gold}, DA: $dailyAverage")
            }
        }

        val marketTable = Table()
        val titleLabel = Label("Market", labelStyle)
        marketTable.add(titleLabel).row()
        marketTable.add(Label("--------------", labelStyle)).row()
        val marketItemsTable = Table()
        updateFuncs.add {
            val itemList = market.getAllItems()
            makeInventoryTableForMarketItem(market, itemList, labelStyle, marketItemsTable)
        }

        marketTable.add(marketItemsTable)

        mainTable.add(marketTable)

        MyGame.stage.addActor(mainTable)
    }

    private fun makeInventoryTable(items:List<InventoryItem>, labelStyle: Label.LabelStyle, table:Table = Table()):Table{
        table.clear()

        items.forEach { item ->
            val text = "${item.name}: Amount: ${item.amount}, Incoming: ${item.incoming}, Outgoing: ${item.outgoing}"
            val itemLabel = Label("${item.name}: Amount: ${item.amount}, Incoming: ${item.incoming}, Outgoing: ${item.outgoing}", labelStyle)
            table.add(itemLabel)
            table.row()
        }

        return table
    }

    private fun makeInventoryTableForMarketItem(market:MarketComponent, items:List<InventoryItem>,
                                                labelStyle: Label.LabelStyle, table:Table = Table()):Table{
        table.clear()

        items.forEach { item ->
            val text = "${item.name}: Amount: ${item.amount}, Incoming: ${item.incoming}, Outgoing: ${item.outgoing}, " +
                    "Price: ${formatter.format(market.getAveragePriceForBuyingFromMarket(item.name, 1))}"

            val itemLabel = Label(text, labelStyle)
            table.add(itemLabel)
            table.row()
        }
        return table
    }

    override fun close() {
        super.close()

        mainTable.remove()
    }
}