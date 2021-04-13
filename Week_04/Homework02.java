import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 */
public class Homework02 {
    static int result = 0;

    public static void main(String[] args) {
        final Thread mainThread = Thread.currentThread();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // 1.循环检查结果
        Runnable task1 = () -> {
            new Thread(() -> result = sum()).start();
            try {
                while (result <= 0) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        // 2.循环检查异步线程状态
        Runnable task2 = () -> {
            Thread task = new Thread(() -> result = sum());
            task.start();
            try {
                while (!task.getState().equals(Thread.State.TERMINATED)) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        // 3.主线程阻塞等待异步线程完成join
        Runnable task3 = () -> {
            Thread task = new Thread(() -> result = sum());
            task.start();
            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        // 4.主线程阻塞等待异步线程唤醒wait/notify
        Runnable task4 = () -> {
            new Thread(() -> {
                result = sum();
                synchronized (mainThread) {
                    mainThread.notify();
                }
            }).start();
            try {
                synchronized (mainThread) {
                    mainThread.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        // 5.主线程阻塞等待异步线程唤醒-CountDownLatch模式
        Runnable task5 = () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            new Thread(() -> {
                result = sum();
                countDownLatch.countDown();
            }).start();
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        // 6.主线程阻塞等待异步线程唤醒-Condition模式
        Runnable task6 = () -> {
            new Thread(() -> {
                lock.lock();
                result = sum();
                condition.signal();
                lock.unlock();
            }).start();
            try {
                lock.lock();
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };

        // 7.主线程阻塞等待异步线程唤醒-LockSupport模式
        Runnable task7 = () -> {
            new Thread(() -> {
                result = sum();
                LockSupport.unpark(mainThread);
            }).start();
            LockSupport.park();
        };

        // 8.Thread+FutureTask
        Runnable task8 = () -> {
            FutureTask<Integer> task = new FutureTask<>(() -> sum());
            new Thread(task).start();
            try {
                result = task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        };

        // 9.线程池+Future
        Runnable task9 = () -> {
            Future<Integer> future = executor.submit(() -> sum());
            try {
                result = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        };

        // 10.线程池+FutureTask
        Runnable task10 = () -> {
            FutureTask<Integer> task = new FutureTask<>(() -> sum());
            executor.execute(task);
            try {
                result = task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        };

        // 11.CompletableFuture
        Runnable task11 = () -> result = CompletableFuture.supplyAsync(() -> sum()).join();

        // 确保拿到result并输出
        printResult(task1, 1001);
        printResult(task2, 1002);
        printResult(task3, 1003);
        printResult(task4, 1004);
        printResult(task5, 1005);
        printResult(task6, 1006);
        printResult(task7, 1007);
        printResult(task8, 1008);
        printResult(task9, 1009);
        printResult(task10, 1010);
        printResult(task11, 1011);

        executor.shutdown();
        // 然后退出main线程
    }

    private static void printResult(Runnable task, int index) {
        long start = System.currentTimeMillis();
        result = 0;
        task.run();

        System.out.println(index + "异步计算结果为：" + result);
        System.out.println(index + "使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2)
            return 1;
        return fibo(a - 1) + fibo(a - 2);
    }
}
