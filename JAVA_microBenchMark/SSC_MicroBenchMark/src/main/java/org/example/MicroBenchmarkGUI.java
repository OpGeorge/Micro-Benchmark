package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MicroBenchmarkGUI {
    private JTextField entTests;
    private JTextField entThreads;

    public MicroBenchmarkGUI() {
        JFrame frame = new JFrame("Micro Benchmark");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        mainPanel.setBackground(Color.decode("#CECECE"));

        entTests = new JTextField(20);
        entThreads = new JTextField(20);

        mainPanel.add(new JLabel("Nr. of tests"));
        mainPanel.add(entTests);
        mainPanel.add(new JLabel("Nr. of threads"));
        mainPanel.add(entThreads);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nrTests = Integer.parseInt(entTests.getText());
                int nrThreads = Integer.parseInt(entThreads.getText());
                runBenchmark(nrTests, nrThreads);
            }
        });
        mainPanel.add(startButton);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    private static final int ARRAY_SIZE = 10000;
    private static double frecProc;

    private static int cmmdc(int a, int b) {
        if (b != 0) {
            return cmmdc(b, a % b);
        } else {
            return a;
        }
    }

    private static double processor() {
        int iterations = 1000000;
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int sum = 0;
            for (int j = 0; j < 10000; j++) {
                sum += j;
            }
        }
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        return 1.0e9 * iterations / totalTime;
    }

    private static Lock vectorMutex = new ReentrantLock();

    private static void threadFunction(List<Integer> arr, int start, int end) {
        int container;
        for (int i = start; i < end - 1; i++) {
            vectorMutex.lock();
            container = cmmdc(arr.get(i), arr.get(i + 1));
            vectorMutex.unlock();
        }
    }

    private static void threadFunctionDynamic(int[] arr, int start, int end) {
        int container;
        for (int i = start; i < end - 1; i++) {
            vectorMutex.lock();
            container = cmmdc(arr[i], arr[i + 1]);
            vectorMutex.unlock();
        }
    }

    private static void randArray(int[] arr, int nr) {
        Random rand = new Random();
        for (int i = 0; i < nr; i++) {
            arr[i] = rand.nextInt(1000);
        }
    }

    private void runBenchmark(int numTests, int numThreads) {
        // Implement your benchmark logic here

        frecProc = processor();

        String messageForfile = "JAVA\n";


        System.out.println("FREC:" + frecProc);
        long i1, i2, i3, Big, Bigf;
        Big = System.nanoTime();

        int nr = numTests;
        //int nr = 0;

        int noThreads = numThreads;
        //int noThreads = 0;
        Thread[] threads = new Thread[noThreads];

        int loop = nr;
        double t_to_alloc_static = 0;
        while (loop > 0) {
            i1 = System.nanoTime();
            int[] staticArray = new int[ARRAY_SIZE];
            i2 = System.nanoTime();
            i3 = i2 - i1;
            t_to_alloc_static += (i3 / frecProc) * 1000;
            loop--;
        }

        t_to_alloc_static /= nr;

        messageForfile = messageForfile + "Numar microsecunde pentru alocare statica:\n";
        String s = String.format("%,10f",t_to_alloc_static);
        messageForfile = messageForfile + s + "\n";


        //System.out.println("Numar microsecunde pentru alocare statica: ");
        //System.out.printf("%.10f\n", t_to_alloc_static);

        loop = nr;
        int[] staticArray = new int[ARRAY_SIZE];
        double timeForInitialize = 0;
        while (loop > 0) {
            i1 = System.nanoTime();
            randArray(staticArray, ARRAY_SIZE);
            i2 = System.nanoTime();
            i3 = i2 - i1;
            timeForInitialize += ((i3 / frecProc) * 1000);
            loop--;
        }
        timeForInitialize /= nr;

        messageForfile = messageForfile + "Numar microsecunde pentru initalizarea sirului alocat static:\n";
        s = String.format("%,10f", timeForInitialize );
        messageForfile = messageForfile + s + "\n";

        // System.out.println("Numar microsecunde pentru initalizarea sirului alocat static: ");
        // System.out.printf("%.10f\n", timeForInitialize);

        List<Integer> vecArray = new ArrayList<>();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            vecArray.add(staticArray[i]);
        }

        int endIndex = 0;
        int chunkSize = 0;
        int remaining = 0;
        if (noThreads != 0 && noThreads != 1) {
            chunkSize = ARRAY_SIZE / noThreads;
            remaining = ARRAY_SIZE % noThreads;
        }

        if (noThreads > 1) {
            i1 = System.nanoTime();
            for (int i = 0, start = 0; i < noThreads; i++) {
                int chunk = (i < remaining) ? (chunkSize + 1) : chunkSize;
                endIndex = start + chunk;
                final int finalStart = start;
                final int finalEndIndex = endIndex;
                threads[i] = new Thread(() -> threadFunction(vecArray, finalStart, finalEndIndex));
                threads[i].start();
                start = endIndex;
            }
            i2 = System.nanoTime();
            i3 = i2 - i1;
            double newTimer = (i3 / frecProc) * 1000;

            s = String.format("Numar microsecunde pentru completare task pe sir alocat static cu %d Thread-uri:\n", noThreads);
            messageForfile = messageForfile + s;
            s = String.format("%.10f\n", newTimer);
            messageForfile = messageForfile + s;

            //System.out.printf("Numar microsecunde pentru completare task pe sir alocat static cu %d Thread-uri: \n", noThreads);
            //System.out.printf("%.10f\n", newTimer);


            for (int i = 0; i < noThreads; ++i) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            i1 = System.nanoTime();
            for (int i = 0; i < ARRAY_SIZE - 1; i++) {
                cmmdc(staticArray[i], staticArray[i + 1]);
            }
            i2 = System.nanoTime();
            i3 = i2 - i1;
            double endSingle = (i3 / frecProc) * 1000;
            messageForfile = messageForfile + "Numar microsecunde pentru completare task pe sir alocat static:\n";
            s = String.format("%.10f\n", endSingle);
            messageForfile = messageForfile + s;
            //System.out.println("Numar microsecunde pentru completare task pe sir alocat static: ");
            // System.out.printf("%.10f\n", endSingle);
        }

        int[] dynamicArray;
        loop = nr;
        double dynamicAllocTime = 0;
        while (loop > 0) {
            i1 = System.nanoTime();
            dynamicArray = new int[ARRAY_SIZE];
            i2 = System.nanoTime();
            i3 = i2 - i1;
            dynamicAllocTime += ((i3 / frecProc) * 1000);
            loop--;
        }
        messageForfile = messageForfile + "Numarul de microsecunde pentru alocare dinamica:\n";
        s = String.format("%.10f\n", dynamicAllocTime);
        messageForfile = messageForfile + s;

        //System.out.println("Numarul de microsecunde pentru alocare dinamica: ");
        //System.out.printf("%.10f\n", dynamicAllocTime);

        dynamicArray = new int[ARRAY_SIZE];
        loop = nr;
        timeForInitialize = 0;
        while (loop > 0) {
            i1 = System.nanoTime();
            randArray(dynamicArray, ARRAY_SIZE);
            i2 = System.nanoTime();
            i3 = i2 - i1;
            timeForInitialize += ((i3 / frecProc) * 1000);
            loop--;
        }
        timeForInitialize /= nr;

        messageForfile = messageForfile + "Numar microsecunde pentru initalizarea sirului alocat dinamic:\n";
        s = String.format("%.10f\n", timeForInitialize);
        messageForfile = messageForfile + s;
        // System.out.println("Numar microsecunde pentru initalizarea sirului alocat dinamic: ");
        //System.out.printf("%.10f\n", timeForInitialize);

        if (noThreads > 1) {
            i1 = System.nanoTime();
            for (int i = 0, start = 0; i < noThreads; i++) {
                int chunk = (i < remaining) ? (chunkSize + 1) : chunkSize;
                endIndex = start + chunk;
                int[] finalDynamicArray = dynamicArray;
                int finalStart = start;
                int finalEndIndex1 = endIndex;
                threads[i] = new Thread(() -> threadFunctionDynamic(finalDynamicArray, finalStart, finalEndIndex1));
                threads[i].start();
                start = endIndex;
            }
            i2 = System.nanoTime();
            i3 = i2 - i1;
            double newTimer = (i3 / frecProc) * 1000;
            s = String.format("Numar microsecunde pentru completare task pe sir alocat dinamic cu %d Thread-uri:\n", noThreads);

            messageForfile = messageForfile + s;
            s = String.format("%.10f\n", newTimer);
            messageForfile = messageForfile + s;

            //System.out.printf("Numar microsecunde pentru completare task pe sir alocat dinamic cu %d Thread-uri: \n", noThreads);
            //System.out.printf("%.10f\n", newTimer);

            for (int i = 0; i < noThreads; ++i) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            i1 = System.nanoTime();
            for (int i = 0; i < ARRAY_SIZE - 1; i++) {
                cmmdc(dynamicArray[i], dynamicArray[i + 1]);
            }
            i2 = System.nanoTime();
            i3 = i2 - i1;
            double endSingle = (i3 / frecProc) * 1000;

            messageForfile = messageForfile + "Numar microsecunde pentru completare task pe sir alocat dinamic:\n";
            s = String.format("%.10f\n", endSingle);
            messageForfile = messageForfile + s;
            //System.out.println("Numar microsecunde pentru completare task pe sir alocat dinamic: ");
            //System.out.printf("%.10f\n", endSingle);
        }

        String filePath = "JAVA.txt";

        // Create a FileWriter object to open the file
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write the desired data to the file
            writer.write(messageForfile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bigf = System.nanoTime();
        i3 = Bigf - Big;
        double total = (i3 / frecProc) * 1000;
        System.out.println("Numar microsecunde total " + total + "\n");
        System.out.println(messageForfile);
        // You can add your benchmarking code similar to the Python code
        // You might want to use SwingWorker or another threading mechanism
        // to avoid blocking the UI during benchmarking.
        System.out.println("Running benchmark with " + numTests + " tests and " + numThreads + " threads.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MicroBenchmarkGUI();
            }
        });
    }
}