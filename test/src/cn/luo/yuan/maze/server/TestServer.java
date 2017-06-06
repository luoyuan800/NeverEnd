package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.types.Medallion;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.service.RestConnection;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by gluo on 6/6/2017.
 */
public class TestServer {
    private RestConnection connection = new RestConnection("http://localhost:4567");
    @BeforeTest
    public void start() {
        new Thread(() -> {
            try {
                Server.main(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Test
    public void testSubmitExchange() throws IOException {

        Pet pet = new Pet();
        pet.setType("test");
        pet.setFirstName(FirstName.angry);
        pet.setSecondName(SecondName.blue);
        pet.setHp(100);
        pet.setMaxHp(100);
        pet.setAtk(100);
        pet.setDef(100);
        pet.setIndex(100);
        pet.setId("101");
        pet.setOwnerName("100");
        pet.setOwnerId("101");
        HttpURLConnection urlConnection = connection.getHttpURLConnection("/submit_exchange", RestConnection.POST);
        urlConnection.addRequestProperty("owner_id", pet.getOwnerId());
        System.out.println(connection.connect(pet, urlConnection));
        assertTrue(new File("data/cn.luo.yuan.maze.model.ExchangeObject/101").exists());
    }

    @AfterTest
    public void stop() {
        Server.stop();
    }

    @Test
    public void testPostExchange() throws IOException {
        Pet pet = new Pet();
        pet.setType("test");
        pet.setFirstName(FirstName.angry);
        pet.setSecondName(SecondName.blue);
        pet.setHp(100);
        pet.setMaxHp(100);
        pet.setAtk(100);
        pet.setDef(100);
        pet.setIndex(100);
        pet.setId(UUID.randomUUID().toString());
        pet.setOwnerName("110");
        pet.setOwnerId("102");
        HttpURLConnection urlConnection = connection.getHttpURLConnection("/submit_exchange", RestConnection.POST);
        urlConnection.addRequestProperty("owner_id", pet.getOwnerId());
        System.out.println(connection.connect(pet, urlConnection));
        urlConnection = connection.getHttpURLConnection("/exchange_pet_list", RestConnection.POST);
        Object  o = connection.connect(urlConnection);
        assertTrue(o instanceof List);
        assertFalse(((List)o).isEmpty());

        Accessory accessory = new Accessory();
        accessory.setName("test");
        accessory.setId(UUID.randomUUID().toString());
        accessory.setColor("#12345");
        urlConnection = connection.getHttpURLConnection("/submit_exchange", RestConnection.POST);
        urlConnection.addRequestProperty("owner_id", "123");
        System.out.println(connection.connect(accessory, urlConnection));
        urlConnection = connection.getHttpURLConnection("/exchange_accessory_list", RestConnection.POST);
        o = connection.connect(urlConnection);
        assertTrue(o instanceof List);
        assertFalse(((List)o).isEmpty());

        Goods goods = new Medallion();
        goods.setId(UUID.randomUUID().toString());
        urlConnection = connection.getHttpURLConnection("/submit_exchange", RestConnection.POST);
        urlConnection.addRequestProperty("owner_id", "123");
        System.out.println(connection.connect(goods, urlConnection));
        urlConnection = connection.getHttpURLConnection("/exchange_goods_list", RestConnection.POST);
        o = connection.connect(urlConnection);
        assertTrue(o instanceof List);
        assertFalse(((List)o).isEmpty());

    }
}
