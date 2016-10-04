package _java._se._07._waitnotify;

import java.util.Random;

public class UserResourceThread {
    public static void main(String[] args) throws InterruptedException {
        SharedResource res = new SharedResource();
        IntegerSetterGetter t1 = new IntegerSetterGetter("1", res);
        IntegerSetterGetter t2 = new IntegerSetterGetter("2", res);
        IntegerSetterGetter t3 = new IntegerSetterGetter("3", res);
        IntegerSetterGetter t4 = new IntegerSetterGetter("4", res);
        IntegerSetterGetter t5 = new IntegerSetterGetter("5", res);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        Thread.sleep(100);

        t1.stopThread();
        t2.stopThread();
        t3.stopThread();
        t4.stopThread();
        t5.stopThread();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();

        System.out.println("main");
    }
}

@SuppressWarnings("WeakerAccess")
class IntegerSetterGetter extends Thread {
    private final SharedResource resource;
    private boolean run;

    private Random rand = new Random();

    public IntegerSetterGetter(String name, SharedResource resource) {
        super(name);
        this.resource = resource;
        run = true;
    }

    public void stopThread() {
        run = false;
    }

    public void run() {
        synchronized (resource) {
            resource.numberOfActiveThreads++;
        }

        int action;

        try {
            while (run) {
                action = rand.nextInt(1000);
                if (action % 2 == 0) {
                    getIntegersFromResource();
                } else {
                    setIntegersIntoResource();
                }
                sleep(5);               // <-- Так чередование потоков получается нагляднее
            }
            synchronized (resource) {
                if (resource.numberOfActiveThreads == 1 && resource.numberOfWaitingThreads > 0) {
                    System.out.printf("Поток %s дописывает число во избежание блокировки других потоков:\n", getName());
                    setIntegersIntoResource();
                }
                resource.numberOfActiveThreads--;
                resource.notify();
            }
            System.out.printf("Поток %s завершил работу.\n", getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getIntegersFromResource() throws InterruptedException {
        Integer number;

        synchronized (resource) {
            System.out.printf("Поток %s хочет извлечь число.\n", getName());
            number = resource.getElement();
            if (number == null && resource.numberOfActiveThreads <= 1) {
                System.out.printf("Потоку %s отказано из-за опасности блокировки.\n", getName());
            } else {
                resource.numberOfActiveThreads--;
                resource.numberOfWaitingThreads++;
                while (number == null) {
                    System.out.printf("Поток %s ждет пока очередь заполнится. [A:%d, W:%d]\n", getName(), resource.numberOfActiveThreads, resource.numberOfWaitingThreads);
                    resource.wait();
                    System.out
                            .printf("Поток %s возобновил работу.\n", getName());
                    number = resource.getElement();
                }
                resource.numberOfWaitingThreads--;
                resource.numberOfActiveThreads++;
                System.out.printf("Поток %s извлек число %d\n", getName(), number);
            }
        }
    }

    private void setIntegersIntoResource() {
        Integer number = rand.nextInt(500);
        synchronized (resource) {
            resource.setElement(number);
            System.out.printf("Поток %s записал число %d\n", getName(), number);
            resource.notify();
        }
    }
}

