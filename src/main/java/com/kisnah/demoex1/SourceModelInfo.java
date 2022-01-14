package com.kisnah.demoex1;

public class SourceModelInfo {

    public SourceModelInfo() {
    }

    public SourceModelInfo(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SourceModelInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
