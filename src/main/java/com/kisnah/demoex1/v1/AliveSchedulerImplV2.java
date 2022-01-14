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
public class AliveSchedulerImplV2 implements AliveScheduler {

    private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    // task 관리.
    private static final Map<Integer, ScheduledFuture<?>> scheduleTasks = new ConcurrentHashMap<>();

    // 스케줄러 데이터 관리.
    private static final Map<String, ModelInfo> cacheManager = new ConcurrentHashMap<>();

    @Override
    public void run(int scheduleId, List<ModelInfo> hosts, int initialDelayMs, int delayMs) {
        ExecutorService service = Executors.newFixedThreadPool(100);

        Runnable command = () -> {
            for (ModelInfo host : hosts) {
                boolean alive = isAlive(host.getName());
                host.setAlive(alive);
                System.out.println("HOST = " + host + Thread.currentThread().getName());
                cacheManager.put(host.getName(), host);
            }
        };

        ScheduledFuture<?> future = ses.scheduleWithFixedDelay(() ->
                service.execute(command),
                1000,
                3000,
                TimeUnit.MILLISECONDS);
        scheduleTasks.put(scheduleId, future);
    }

    @Override
    public void stop(int scheduleId) throws InterruptedException {
        scheduleTasks.get(scheduleId).cancel(true);
        System.out.println(" ====== Task Clear ======");
        scheduleTasks.clear();
//        ses.awaitTermination(3000, TimeUnit.MILLISECONDS);
//        System.out.println(shutdown);
        ses.shutdownNow();
        System.out.println(" ================================ 스케줄러 종료. ================================");
    }

    @Override
    public ModelInfo get(String hostName) {
        return cacheManager.get(hostName);
    }

    @Override
    public List<ModelInfo> getAll() {
        Collection<ModelInfo> values = cacheManager.values();
        return new ArrayList<>(values);
    }

    @Override
    public void update(int scheduleId, List<ModelInfo> hosts) {
        cacheManager.clear();
        run(scheduleId, hosts, 1000, 3000);
    }

    public static void main(String[] args) throws InterruptedException {
        // 모니터링 조회 대상 리스트.
        List<ModelInfo> list = getHosts();

        AliveSchedulerImplV2 aliveScheduler = new AliveSchedulerImplV2();

        aliveScheduler.run(1, list, 1000, 3000);

        System.out.println(" ====== first sleep start ======");
        Thread.sleep(5000);
        System.out.println(" ====== first sleep end ======");

        System.out.println(" ====== second sleep start ======");
        Thread.sleep(5000);
        System.out.println(" ====== second sleep end ======");

        System.out.println(" ====== getAll ====== ");
        List<ModelInfo> getAll = aliveScheduler.getAll();
        System.out.println(getAll);
        System.out.println(" ====== getAll ====== ");

        System.out.println("======== get size =========");
        int size = aliveScheduler.getAll().size();
        System.out.println(size);
        System.out.println("======== get size =========");

        System.out.println(" ====== get ====== ");
        ModelInfo modelInfoA = aliveScheduler.get("google.com5");
        System.out.println(modelInfoA);
        System.out.println(" ====== get ====== ");


        aliveScheduler.stop(1);

        System.out.println(" ====== 새로운 리스트 조회. ======");
        List<ModelInfo> updateList = getUpdateList();


        Thread.sleep(3000);

        System.out.println(" 스케줄러 시작.");
        aliveScheduler.update(1, updateList);

        Thread.sleep(8000);

        System.out.println(" ====== get ====== ");
        ModelInfo modelInfoB = aliveScheduler.get("google.com5");
        System.out.println(modelInfoB);
        System.out.println(" ====== get ====== ");

        aliveScheduler.stop(1);

        System.out.println("메인 스레드 종료.");
        System.out.println(Thread.currentThread().getName());


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
