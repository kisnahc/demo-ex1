package com.kisnah.demoex1.v1;

import com.kisnah.demoex1.ModelInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class AliveSchedulerImplV3 implements AliveSchedulerV3 {


    private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    // task 관리.
    private static final Map<Integer, ScheduledFuture<?>> scheduleTasks = new ConcurrentHashMap<>();

    // 스케줄러 데이터 관리.
    private static final Map<Object, ModelInfo> cacheManager = new ConcurrentHashMap<>();

    @Override
    public List<ModelInfo> map(List<?> list) {
    }

    @Override
    public void task(List<ModelInfo> list) {
        ExecutorService es = Executors.newCachedThreadPool();
        es.submit(() -> {
            for (ModelInfo modelInfo : list) {
                boolean alive = isAlive(modelInfo.getName());
                modelInfo.setAlive(alive);
                System.out.println(modelInfo);
                cacheManager.put(modelInfo.getName(), modelInfo);
            }
        });
    }

    @Override
    public void run(int schedulerId, List<ModelInfo> list, int initialDelayMs, int delayMs) throws ExecutionException, InterruptedException {

        ExecutorService es = Executors.newCachedThreadPool();

        ScheduledFuture<?> future = ses.scheduleWithFixedDelay(() -> {
            es.submit( () -> {
                for (ModelInfo modelInfo : list) {
                    boolean alive = isAlive(modelInfo.getName());
                    modelInfo.setAlive(alive);
                    System.out.println(modelInfo);
                    cacheManager.put(modelInfo.getName(), modelInfo);
                }
            });
        }, 1000, 3000, TimeUnit.MILLISECONDS);


        scheduleTasks.put(schedulerId, future);

    }

    @Override
    public void reRun(int schedulerId, List<ModelInfo> list) {

    }

    @Override
    public void stop(int schedulerId) {
        scheduleTasks.get(schedulerId).cancel(true);
        scheduleTasks.clear();
        ses.shutdownNow();
    }

    @Override
    public ModelInfo get(String name) {
        return cacheManager.get(name);
    }

    @Override
    public List<ModelInfo> getAll() {
        Collection<ModelInfo> values = cacheManager.values();
        return new ArrayList<>(values);
    }




    private boolean isAlive(String name) {
        try {
            InetAddress inetAddress = InetAddress.getByName(name);
            return inetAddress.isReachable(2000);
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        List<ModelInfo> list = getHosts();
        AliveSchedulerImplV3 aliveSchedulerImplV3 = new AliveSchedulerImplV3();

        System.out.println("----- run -----");
        aliveSchedulerImplV3.run(1, list, 0, 3000);
        System.out.println("----- run -----");
//        Thread.sleep(10000);
        List<ModelInfo> updateList = getUpdateList();
        //TODO 스레드 종료가 안됨.
        System.out.println("----- stop -----");
        aliveSchedulerImplV3.reRun(2, updateList);
        System.out.println("----- stop -----");


    }

    private static List<ModelInfo> getUpdateList() {
        List<ModelInfo> updateList = new ArrayList<>();
        String hostNameB = "naver.com";
        updateList.add(new ModelInfo(hostNameB));
        for (int i = 0; i < 2000; i++) {
            updateList.add(new ModelInfo(hostNameB + i));
        }
        return updateList;
    }

    private static List<ModelInfo> getHosts() {
        String hostNameA = "google.com";
        List<ModelInfo> list = new ArrayList<>();
        list.add(new ModelInfo(hostNameA));
        for (int i = 0; i < 1000; i++) {
            list.add(new ModelInfo(hostNameA + i));
        }
        return list;


    }
}
