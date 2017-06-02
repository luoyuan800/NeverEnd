package cn.luo.yuan.maze.server

import cn.luo.yuan.maze.model.ExchangeObject
import cn.luo.yuan.maze.server.persistence.ExchangeTable
import cn.luo.yuan.maze.server.persistence.GroupTable
import cn.luo.yuan.maze.server.persistence.HeroTable
import spark.Spark.post
import java.io.ByteArrayInputStream
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 *
 * Created by gluo on 6/1/2017.
 */

fun main(args: Array<String>) {
    val root = File("data")
    val groupTable = GroupTable(root)
    val heroTable = HeroTable(root)
    val exchangeTable = ExchangeTable(root)
    post("submit_exchange", { request, response ->
        val ois = ObjectInputStream(ByteArrayInputStream(request.bodyAsBytes()))
        val exchange = ois.readObject()
        if (exchange is ExchangeObject) {
            exchange.submitTime = System.currentTimeMillis()
            exchangeTable.addExchange(exchange)
            response.status(1)
        } else {
            response.status(-1)
            response.body("Could not add exchange, maybe there already an exchange object has the same id existed!")
        }
        response
    })

    post("exchange_pet_list", { request, response ->
        val pets = exchangeTable.loadAll(1)
        val oos = ObjectOutputStream(response.outputStream)
        oos.writeObject(pets)
        oos.flush()
        oos.close()
        response.status(1)
        response
    })

    post("exchange_accessory_list", { request, response ->
        val pets = exchangeTable.loadAll(2)
        val oos = ObjectOutputStream(response.outputStream)
        oos.writeObject(pets)
        oos.flush()
        oos.close()
        response.status(1)
        response
    })

    post("exchange_goods_list", { request, response ->
        val pets = exchangeTable.loadAll(3)
        val oos = ObjectOutputStream(response.outputStream)
        oos.writeObject(pets)
        oos.flush()
        oos.close()
        response.status(1)
        response
    })

    post("query_my_exchange", { request, response ->
        val exs = exchangeTable.loadAll(request.headers("id"))
        val oos = ObjectOutputStream(response.outputStream)
        oos.writeObject(exs)
        oos.flush()
        oos.close()
        response.status(1)
        response
    })



    run(heroTable, groupTable)
}