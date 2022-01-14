package com.kisnah.demoex1.v1;

import com.kisnah.demoex1.ModelInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface AliveSchedulerV3 {

    List<ModelInfo> map(List<?> list);

    void task(List<ModelInfo> list);

    void run(int schedulerId , int initialDelayMs, int delayMs) throws ExecutionException, InterruptedException;

    void stop(int schedulerId);

    ModelInfo get(String name);

    List<ModelInfo> getAll();
}
