package com.kisnah.demoex1;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface AliveScheduler {

    void run(int scheduleId, List<HostInfo> hosts, int initialDelayMs, int delayMs) throws ExecutionException, InterruptedException;

    void stop(int scheduleId) throws ExecutionException, InterruptedException;

    void get(int scheduleId, String hostName) throws ExecutionException, InterruptedException;

    void getAll(int scheduleId) throws ExecutionException, InterruptedException;

    void update(int scheduleId, List<HostInfo> hosts) throws ExecutionException, InterruptedException;
}
