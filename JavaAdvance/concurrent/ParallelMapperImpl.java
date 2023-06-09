package info.kgeorgiy.ja.latanov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ParallelMapperImpl implements ParallelMapper {

    private final Queue<Runnable> tasks = new LinkedList<>();
    private final List<Thread> threads;

    public ParallelMapperImpl(int thread) {
        threads = new ArrayList<>(Collections.nCopies(thread, null));
        IntStream.range(0, thread).forEach(i -> {
            threads.set(i, new Thread(() -> {
                while (!Thread.interrupted()) {
                    if (taskManager()) {
                        return;
                    }
                }
            }));
            threads.get(i).start();
        });
    }

    private boolean taskManager() {
        Runnable task = getTask();
        if (task != null) {
            task.run();
        } else {
            return true;
        }
        return false;
    }

    private void addTask(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    @Override
    public <T, TT> List<TT> map(Function<? super T, ? extends TT> f, List<? extends T> args) throws InterruptedException {
        final List<TT> result = new ArrayList<>(Collections.nCopies(args.size(), null));
        final int[] counter = {0};
        IntStream.range(0, args.size()).forEach(i -> addTask(() -> {
            try {
                result.set(i, f.apply(args.get(i)));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            synchronized (counter) {
                if (++counter[0] == args.size()) {
                    counter.notifyAll();
                }
            }
        }));

        synchronized (counter) {
            while (counter[0] != args.size()) {
                counter.wait();
            }
        }
        return result;
    }

    @Override
    public void close() {
        threads.forEach(Thread::interrupt);
        joiningThread();
    }

    private void joiningThread() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private Runnable getTask() {
        try {
            synchronized (tasks) {
                while (tasks.isEmpty()) {
                    tasks.wait();
                }
                tasks.notifyAll();
                return tasks.poll();
            }
        } catch (InterruptedException e) {
            return null;
        }
    }
}
