package com.ntunghoi.tutorials.concurrency.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class WordFrequencyCountService {
    private final ConcurrentHashMap<String, LongAdder> counts =
            new ConcurrentHashMap<>();

    public void record(String word) {
        counts.computeIfAbsent(word, _ -> new LongAdder())
                .increment();
    }

    public Map<String, Long> snapshot() {
        ConcurrentHashMap<String, Long> result = new ConcurrentHashMap<>();
        counts.forEach((word, adder) ->
                result.put(word, adder.sum())
        );

        return result;
    }
}
