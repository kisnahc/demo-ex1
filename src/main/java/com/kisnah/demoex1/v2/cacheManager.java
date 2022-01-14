package com.kisnah.demoex1.v2;

import com.kisnah.demoex1.ModelInfo;

import java.util.List;

public interface cacheManager {

    void put();

    void clear();

    ModelInfo get();

    List<ModelInfo> getAll();
}
