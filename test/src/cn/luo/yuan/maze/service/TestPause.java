package cn.luo.yuan.maze.service;

/**
 * Created by luoyuan on 2017/6/3.
 */
public class TestPause {
    public static void main(String...args) throws InterruptedException {
        Boolean pause = false;
        new Thread(new R(pause)).start();
        Thread.sleep(1000);
        pause = true;
    }

    static class R implements Runnable{
        Boolean pause;
        R(Boolean pause){
            this.pause = pause;
        }
        @Override
        public void run() {
            while (true){
                System.out.println("R: pause = " + pause);
            }
        }
    }
}
