package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.client.service.ServerService;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.types.Medallion;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.utils.StringUtils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by gluo on 6/6/2017.
 */
public class TestServer {
    private RestConnection connection = new RestConnection("http://localhost:4567", "");
    @BeforeTest
    public void start() {
        /*new Thread(() -> {
            try {
                Server.main(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();*/
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
    public void stop() throws IOException, ClassNotFoundException {
        //Server.clear();
        //Server.stop();
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

    @Test
    public void testExchange() throws IOException {
        Pet pet = new Pet();
        pet.setType("testExchange");
        pet.setFirstName(FirstName.angry);
        pet.setSecondName(SecondName.blue);
        pet.setHp(100);
        pet.setMaxHp(100);
        pet.setAtk(100);
        pet.setDef(100);
        pet.setIndex(100);
        pet.setId("exchange_pet_test");
        pet.setOwnerName("exchanger");
        pet.setOwnerId("exchanger");
        HttpURLConnection urlConnection = connection.getHttpURLConnection("/submit_exchange", RestConnection.POST);
        urlConnection.addRequestProperty("owner_id", pet.getOwnerId());
        System.out.println(connection.connect(pet, urlConnection));


        urlConnection = connection.getHttpURLConnection("/exchange_pet_list", RestConnection.POST);
        List<ExchangeObject>  o = (List<ExchangeObject>) connection.connect(urlConnection);
        System.out.println(o);

        Accessory accessory = new Accessory();
        accessory.setName("testExchange");
        accessory.setId("exchange_accessory_test");
        accessory.setColor("#12345");

        ExchangeObject eo = o.get(0);

        urlConnection = connection.getHttpURLConnection("/request_exchange", RestConnection.POST);
        urlConnection.addRequestProperty("owner_id", "exchanger");
        urlConnection.addRequestProperty("ex_id", eo.getId());

        System.out.println(connection.connect(accessory,urlConnection));

        urlConnection = connection.getHttpURLConnection("/exchange_pet_list", RestConnection.POST);
        List<ExchangeObject>  oo = (List<ExchangeObject>) connection.connect(urlConnection);
        assertNotEquals(oo.size(), o.size());

        //Acknowledge
        urlConnection = connection.getHttpURLConnection("/query_my_exchange", RestConnection.POST);
        urlConnection.addRequestProperty("owner_id", "exchanger");
        List<ExchangeObject>  mo = (List<ExchangeObject>) connection.connect(urlConnection);
        assertTrue(mo.size() > 0);
        for(ExchangeObject meo : mo){
            if(meo.getChanged()!=null){
                if(meo.getExchange() instanceof Accessory) {
                    assertEquals(meo.getChanged().getId(), pet.getId(), "Exchange should be change to success!");
                }
                if(meo.getExchange() instanceof Pet){
                    assertEquals(meo.getChanged().getId(), accessory.getId(), "Exchange should be change to success!");
                }
                if(!meo.getAcknowledge()) {
                    urlConnection = connection.getHttpURLConnection("/acknowledge_my_exchange", RestConnection.POST);
                    urlConnection.addRequestProperty("ex_id", meo.getId());
                    System.out.println(connection.connect(urlConnection));
                }
            }
        }

        urlConnection = connection.getHttpURLConnection("/query_my_exchange", RestConnection.POST);
        urlConnection.addRequestProperty("owner_id", "exchanger");
        List<ExchangeObject>  moa = (List<ExchangeObject>) connection.connect(urlConnection);
        assertNotEquals(moa.size(), mo.size());
    }

    @Test
    public void testSubmitHero() throws IOException {
        Hero hero = new Hero();
        hero.setName("QA_" + System.currentTimeMillis());
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        hero.setRace(Race.Elyosr.ordinal());
        hero.setId("test");
        Maze maze = new Maze();
        Pet pet = new Pet();
        pet.setType("test");
        pet.setFirstName(FirstName.angry);
        pet.setSecondName(SecondName.blue);
        pet.setHp(100);
        pet.setMaxHp(100);
        pet.setAtk(100);
        pet.setDef(100);
        pet.setIndex(100);
        pet.setId(hero.getId());
        pet.setOwnerName("100");
        pet.setOwnerId("101");
        hero.getPets().add(pet);
        ServerData upload = new ServerData();
        upload.hero = hero;
        upload.maze = maze;
        upload.pets = new ArrayList<>(hero.getPets());
        ServerService serverService = new ServerService("http://localhost:4567","test");
        serverService.uploadHero(upload);
        assertTrue(new File("data/hero/" + hero.getId()).exists());
        assertTrue(serverService.postOnlineData(hero.getId()).contains(hero.getDisplayName()));
    }

    @Test
    public void testBattle(){
        Hero hero = new Hero();
        hero.setRace(Race.Wizardsr.ordinal());
        hero.setName("QA_" + System.currentTimeMillis());
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        hero.setId(UUID.randomUUID().toString());
        Maze maze = new Maze();
        ServerData upload = new ServerData();
        upload.hero = hero;
        upload.maze = maze;
        ServerService serverService = new ServerService("http://localhost:4567","test");
        serverService.uploadHero(upload);
        assertTrue(new File("data/hero/" + hero.getId()).exists());
        try {
            Thread.sleep(360000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String str = serverService.postBattleMsg(hero.getId(), 1);
        System.out.println(str);
        //assertTrue(StringUtils.isNotEmpty(str), "Should has battle message!");
        while (StringUtils.isNotEmpty(str = serverService.postBattleMsg(hero.getId(), 1))){
            System.out.println(str);
        }
    }

    @Test
    public void testPollBattleMessage(){
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ServerService serverService = new ServerService("http://localhost:4567","test");
        String str = serverService.postBattleMsg("test", 1);
        System.out.println(str);
        //assertTrue(StringUtils.isNotEmpty(str), "Should has battle message!");
        while (StringUtils.isNotEmpty(str = serverService.postBattleMsg("test", 1))){
            System.out.println(str);
        }
    }

    public static void main(String...args){
        ServerService serverService = new ServerService("http://localhost:4567","test");
        String str = serverService.postBattleMsg("test", 1);
        System.out.println(str);
    }

}
