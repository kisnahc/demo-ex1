package com.kisnah.demoex1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class AliveSchedulerImplTestV3 {

    private final AliveSchedulerImplV3 aliveScheduler;

    @Autowired
    public AliveSchedulerImplTestV3(AliveSchedulerImplV3 aliveScheduler) {
        this.aliveScheduler = aliveScheduler;
    }

    List<SourceModelInfo> list = new ArrayList<>();

    @BeforeEach
    void init(){
        list.add(new SourceModelInfo("google.com"));
        for (int i = 0; i < 100; i++) {
            list.add(new SourceModelInfo("google.com"+i));
        }
    }

    @Test
    public void map_test() {
        List<ModelInfo> map = aliveScheduler.map(list);
        System.out.println(map.get(0));

    }
}