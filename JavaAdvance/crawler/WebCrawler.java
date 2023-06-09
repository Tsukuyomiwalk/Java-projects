package info.kgeorgiy.ja.latanov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {

    private final Downloader downloader;
    private final ExecutorService downloadExecutor;
    private final ExecutorService extractExecutor;
    private final Phaser phaser = new Phaser(1);

    /**
     * constructor for this webCrawler
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        downloadExecutor = Executors.newFixedThreadPool(downloaders);
        extractExecutor = Executors.newFixedThreadPool(extractors);
    }

    /**
     * Downloads web-site up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @return download result.
     */
    @Override
    public Result download(String url, int depth) {
        Queue<String> links = new ConcurrentLinkedQueue<>();
        Map<String, IOException> wrongUrl = new ConcurrentHashMap<>();
        Set<String> visited = ConcurrentHashMap.newKeySet();
        Queue<String> queueForUrl = new ConcurrentLinkedQueue<>();
        queueForUrl.add(url);
        visited.add(url);
        while (depth > 0) {
            int depthSize = queueForUrl.size();
            for (int i = 0; i < depthSize; i++) {
                String currentLink = Objects.requireNonNull(queueForUrl.poll());
                downloadExecutor.submit(() -> {
                    try {
                        Document document = downloader.download(currentLink);
                        links.add(currentLink);
                        extractExecutor.submit(() -> {
                            try {
                                for (String link : document.extractLinks()) {
                                    if (!visited.contains(link)) {
                                        visited.add(link);
                                        queueForUrl.add(link);
                                    }
                                }
                            } catch (IOException ignored) {
                            }
                            phaser.arriveAndDeregister();
                            phaser.notify();
                        });
                    } catch (IOException e) {
                        wrongUrl.put(currentLink, e);
                        phaser.arriveAndDeregister();
                        phaser.notify();
                    }
                });
                phaser.register();
            }
            phaser.arriveAndAwaitAdvance();
            depth--;
        }

        return new Result(new ArrayList<>(links), wrongUrl);
    }

    /**
     * Closes this web-crawler, relinquishing any allocated resources.
     */
    @Override
    public void close() {
        phaser.arriveAndDeregister();
        downloadExecutor.shutdownNow();
        extractExecutor.shutdownNow();
    }

    /**
     * main Class for this web-crawler.
     */
    public static void main(String[] args) {
        if (args == null || args.length < 1 || args.length > 4) {
            System.err.println("Problems with args");
            return;
        }
        int downloaders;
        int extractors;
        int perHost;
        if (args.length == 4) {
            downloaders = Integer.parseInt(args[0]);
            extractors = Integer.parseInt(args[1]);
            perHost = Integer.parseInt(args[2]);
        } else {
            System.err.println("illegal amount of arguments");
            return;
        }
        try {
            WebCrawler webCrawler = new WebCrawler(new CachingDownloader(1), downloaders, extractors, perHost);
        } catch (IOException e) {
            System.err.println("Input arguments are wrong");
        }
    }
}
