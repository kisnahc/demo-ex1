package com.kisnah.demoex1.v1;

import com.kisnah.demoex1.ModelInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface AliveScheduler {

    void run(int scheduleId, List<ModelInfo> hosts, int initialDelayMs, int delayMs) throws ExecutionException, InterruptedException;

    void stop(int scheduleId) throws ExecutionException, InterruptedException;

    ModelInfo get(String hostName) throws ExecutionException, InterruptedException;

    List<ModelInfo> getAll() throws ExecutionException, InterruptedException;

    void update(int scheduleId, List<ModelInfo> hosts) throws ExecutionException, InterruptedException;
}
