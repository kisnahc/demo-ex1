package com.kisnah.demoex1;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface AliveScheduler {

    void run(int scheduleId, List<HostInfo> hosts, int initialDelayMs, int delayMs) throws ExecutionException, InterruptedException;

    void stop(int scheduleId) throws ExecutionException, InterruptedException;

    HostInfo get(String hostName) throws ExecutionException, InterruptedException;

    List<HostInfo> getAll() throws ExecutionException, InterruptedException;

    void update(int scheduleId, List<HostInfo> hosts) throws ExecutionException, InterruptedException;
}
