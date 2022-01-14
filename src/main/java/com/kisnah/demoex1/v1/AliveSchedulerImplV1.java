package com.kisnah.demoex1.v1;

import com.kisnah.demoex1.ModelInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class AliveSchedulerImplV1 implements AliveScheduler {

    private static ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);

    // task 관리.
    private static Map<Integer, ScheduledFuture<?>> scheduleTasks = new ConcurrentHashMap<>();

    // 스케줄러 데이터 관리.
    private static List<ModelInfo> cacheManager = new ArrayList<>();

    // trigger 역할.
    private boolean shutdown = false;

    @Override
    public void run(int scheduleId, List<ModelInfo> hosts, int initialDelayMs, int delayMs) throws InterruptedException {

        if (shutdown) {
            return;
        }
        ScheduledFuture<?> future = ses.scheduleWithFixedDelay(() -> {
            for (ModelInfo host : hosts) {
                boolean alive = isAlive(host.getName());
                host.setAlive(alive);
//                System.out.println("HOST = " + host);
                cacheManager.add(host);
            }
        }, initialDelayMs, delayMs, TimeUnit.MILLISECONDS);
        scheduleTasks.put(scheduleId, future);
    }

    @Override
    public void stop(int scheduleId) throws ExecutionException, InterruptedException {
        ses.shutdownNow();
        shutdown = true;
        scheduleTasks.get(scheduleId).cancel(true);

        System.out.println(" ====== Task Clear ======");
        scheduleTasks.clear();

        System.out.println(" ================================ 스케줄러 종료. ================================");
    }

    @Override
    public ModelInfo get(String hostName) {
        return cacheManager.get(3);
    }

    @Override
    public List<ModelInfo> getAll() throws ExecutionException, InterruptedException {
        return cacheManager.stream()
                .parallel()
                .collect(Collectors.toList());
    }

    @Override
    public void update(int scheduleId, List<ModelInfo> hosts) throws ExecutionException, InterruptedException {
        shutdown = false;
        run(scheduleId, hosts, 1000, 3000);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 모니터링 조회 대상 리스트.
        List<ModelInfo> list = getHosts();

        AliveSchedulerImplV1 aliveScheduler = new AliveSchedulerImplV1();

        aliveScheduler.run(1, list, 1000, 3000);

        System.out.println(" ====== first sleep start ======");
        Thread.sleep(8000);
        System.out.println(" ====== first sleep end ======");

        System.out.println(" ====== second sleep start ======");
        Thread.sleep(8000);
        System.out.println(" ====== second sleep end ======");

        System.out.println(" ====== getAll ====== ");
        List<ModelInfo> getAll = aliveScheduler.getAll();
        System.out.println(getAll);
        System.out.println(" ====== getAll ====== ");


        System.out.println(" ================================ 스케줄러 종료. ================================");
        aliveScheduler.stop(1);

        System.out.println(" ====== 새로운 리스트 조회. ======");
        List<ModelInfo> updateList = getUpdateList();

        System.out.println(" 스케줄러 시작.");

        Thread.sleep(8000);

//        aliveScheduler.run(1, updateList, 1000, 3000);

        aliveScheduler.update(1, updateList);

    }

    private static List<ModelInfo> getUpdateList() {
        List<ModelInfo> updateList = new ArrayList<>();
        String hostNameB = "naver.com";
        updateList.add(new ModelInfo(hostNameB));
        for (int i = 0; i < 10; i++) {
            updateList.add(new ModelInfo(hostNameB + i));
        }
        return updateList;
    }

    private static List<ModelInfo> getHosts() {
        String hostNameA = "google.com";
        List<ModelInfo> list = new ArrayList<>();
        list.add(new ModelInfo(hostNameA));
        for (int i = 0; i < 10; i++) {
            list.add(new ModelInfo(hostNameA + i));
        }
        return list;
    }
    private boolean isAlive(String hostName) {
        try {
            InetAddress inetAddress = InetAddress.getByName(hostName);
            return inetAddress.isReachable(2000);
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
    }
}
