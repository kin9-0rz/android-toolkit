package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolExecutorTest {
    public static void main(String[] args) {

        int corePoolSize = 2;
        int maximumPoolSize = 3;
        long keepAliveTime = 3L;

        ThreadPoolExecutor pool =
                new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(1),
                        new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 10; i++) {

            System.out.println("Active Count : " + pool.getActiveCount());
            System.out.println("Core Pool Size : " + pool.getCorePoolSize());
            System.out.println("Maximum Pool Size : " + pool.getMaximumPoolSize());
            System.out.println("Pool Size : " + pool.getPoolSize());
            System.out.println("准备添加任务" + i);

            while (true) {
                if (pool.getActiveCount() < maximumPoolSize) {
                    break;
                }
            }

            pool.execute(new Task(i));
        }
        pool.shutdown();
    }
}

class Task extends Thread {
    int index;

    Task(int i) {
        index = i;
    }

    @Override
    public void run() {
        System.out.println("task[" + index + "]" + "begin");
        try {
            Thread.sleep((long) (Math.random() * 10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("task[" + index + "]" + "end");

    }
}
