package com.kisnah.demoex1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AliveSchedulerImplTest {

    private final AliveSchedulerImpl aliveScheduler;

    @Autowired
    public AliveSchedulerImplTest(AliveSchedulerImpl aliveScheduler) {
        this.aliveScheduler = aliveScheduler;
    }

    List<HostInfo> list = new ArrayList<>();

    @BeforeEach
    void init(){
        list.add(new HostInfo("google.com"));
        for (int i = 1; i < 2001; i++) {
            list.add(new HostInfo("google.com"+i));
        }
    }

    private final int id = 1;

    @Test
    public void run_test() throws ExecutionException, InterruptedException {
        aliveScheduler.run(id, list, 1000, 3000);
    }

    @Test
    public void stop_test() throws ExecutionException, InterruptedException {
        aliveScheduler.stop(id);
    }

}