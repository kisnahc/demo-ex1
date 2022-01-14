package com.kisnah.demoex1;

public class ModelInfo {

    private String name;
    private boolean isAlive;

    public ModelInfo() {
    }

    public ModelInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlive(boolean isAlive) {
        isAlive = isAlive;
    }


    @Override
    public String toString() {
        return "ModelInfo{" +
                "name='" + name + '\'' +
                ", isAlive=" + isAlive +
                '}';
    }
}
