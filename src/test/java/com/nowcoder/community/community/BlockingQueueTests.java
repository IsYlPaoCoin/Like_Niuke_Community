package com.nowcoder.community.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * ClassName: BlockingQueueTests
 * Package: com.nowcoder.community.community
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/12 11:17
 * @Version 1.0
 */
public class BlockingQueueTests {
    public static void main(String[] args) {

        // 阻塞队列 通过 数组实现，   设置默认 数组长度为 10
        BlockingQueue queue = new ArrayBlockingQueue(10);
        // 有一个生产者线程  不断在生产数据  最多生产100个
        new Thread(new Producer(queue)).start();

        // 三个消费者  同时消费  数据
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

class Producer implements  Runnable {

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i=0; i<100; i++){
                // 每20  毫秒生产  一个数据i
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产：" +queue.size());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try{
            while(true) {
                // 0到1000  中随机取一个数
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                // 查看当前的  消费者线程  是谁
                System.out.println(Thread.currentThread().getName()+"消费了"+queue.size());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}