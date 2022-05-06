package com.example;

public class FirstTask {
    private static final Object object = new Object();

    private static int threadNumber = 1;

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            synchronized (object) {
                try {
                    for (int i = 0; i < 5; i++) {
                        if (threadNumber != 1){
                            object.wait();
                        }
                        System.out.print("A");
                        threadNumber++;
                        object.notifyAll();
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            synchronized (object) {
                try {
                    for (int i = 0; i < 5; i++) {
                        if (threadNumber != 2){
                            object.wait();
                        }
                        System.out.print("B");
                        threadNumber++;
                        object.notifyAll();
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread3 = new Thread(() -> {
            synchronized (object) {
                try {
                    for (int i = 0; i < 5; i++) {
                        if (threadNumber != 3){
                            object.wait();
                        }
                        System.out.print("C");
                        threadNumber = 1;
                        object.notifyAll();
                        if (i == 4 ){
                            break;
                        }
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
    }
}
