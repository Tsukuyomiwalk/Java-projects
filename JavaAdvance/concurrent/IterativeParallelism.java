package info.kgeorgiy.ja.latanov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author created by Daniil Latanov
 */
public class IterativeParallelism implements ScalarIP {

    private final ParallelMapper mapper;

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    public IterativeParallelism() {
        mapper = null;
    }


    /**
     * Returns maximum value.
     *
     * @param threads    number of concurrent threads.
     * @param values     values to get maximum of.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return maximum of given values
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return distributor(threads, values, a -> Collections.max(a, comparator), b ->
                b.stream().max(comparator).orElse(null));
    }

    /**
     * Returns minimum value.
     *
     * @param threads    number of concurrent threads.
     * @param values     values to get minimum of.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return minimum of given values
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return distributor(threads, values, a -> Collections.min(a, comparator), b ->
                b.stream().min(comparator).orElse(null));
    }

    /**
     * Returns whether all values satisfy predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return whether all values satisfy predicate or {@code true}, if no values are given.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return distributor(threads, values, a -> a.stream().allMatch(predicate), b ->
                b.stream().allMatch(Boolean::booleanValue));
    }

    /**
     * Returns whether any of values satisfies predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return whether any value satisfies predicate or {@code false}, if no values are given.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return distributor(threads, values, a -> a.stream().anyMatch(predicate), b ->
                b.stream().anyMatch(Boolean::booleanValue));
    }

    /**
     * Returns number of values satisfying predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return number of values satisfying predicate.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return distributor(threads, values, a -> (int) a.stream().filter(predicate).count(), b ->
                b.stream().mapToInt(Integer::intValue).sum());
    }

    private <T, TT> TT distributor(int threads, List<? extends T> values,
                                   Function<List<? extends T>, TT> f,
                                   Function<List<TT>, TT> g)
            throws InterruptedException {
        int n = Math.min(threads, values.size());

        int howManyForThreads = values.size() / n;
        ArrayList<List<? extends T>> sublistsForThreads = new ArrayList<>(Collections.nCopies(n, null));
        ArrayList<TT> res = new ArrayList<>(Collections.nCopies(n, null));

        int counter = values.size() - (threads * howManyForThreads);
        int sublistStart = 0;
        List<Thread> threadList = new ArrayList<>(Collections.nCopies(n, null));

        int i = 0;
        while (i < n) {
            sublistsForThreads.set(i, sublist(values, sublistStart, howManyForThreads, counter > 0));
            startThread(res, i, f, sublistsForThreads, threadList, i);
            if (counter > 0) {
                sublistStart += howManyForThreads + 1;
                counter -= 1;
            } else {
                sublistStart += howManyForThreads;
            }
            i++;

        }
        return getResult(f, g, sublistsForThreads, res, threadList);
    }


    private <T, TT> TT getResult(Function<List<? extends T>, TT> f, Function<List<TT>, TT> g, ArrayList<List<? extends T>> sublistsForThreads, ArrayList<TT> res, List<Thread> threadList) throws InterruptedException {
        List<TT> results;
        if (mapper != null) {
            results = mapper.map(f, sublistsForThreads);
            return g.apply(results);
        } else {
            joiningThreads(threadList);
            return g.apply(res);
        }
    }

    private void joiningThreads(List<Thread> threadList) throws InterruptedException {
        for (Thread thread : threadList) {
            thread.join();
        }
    }

    private <T, R> void startThread(ArrayList<R> res, int finalI1, Function<List<? extends T>, R> f, ArrayList<List<? extends T>> sublistsForThreads, List<Thread> threadList, int i) {
        Thread thread = new Thread(() -> res.set(finalI1, f.apply(sublistsForThreads.get(finalI1))));
        threadList.set(i, thread);
        thread.start();
    }


    private <T> List<? extends T> sublist(List<? extends T> values, int index, int howManyForThreads, boolean anyOther) {
        if (anyOther) return values.subList(index, howManyForThreads + index + 1);
        return values.subList(index, howManyForThreads + index);
    }
}
