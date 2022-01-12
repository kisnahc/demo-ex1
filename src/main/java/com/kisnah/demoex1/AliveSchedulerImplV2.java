package com.kisnah.demoex1;

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
    private static final Map<String, HostInfo> cacheManager = new ConcurrentHashMap<>();

    @Override
    public void run(int scheduleId, List<HostInfo> hosts, int initialDelayMs, int delayMs) {

        ExecutorService service = Executors.newFixedThreadPool(100);

        Runnable command = () -> {
            for (HostInfo host : hosts) {
                boolean alive = isAlive(host.getHostName());
                host.setAlive(alive);
                System.out.println("HOST = " + host + Thread.currentThread().getName());
                cacheManager.put(host.getHostName(), host);
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
    public HostInfo get(String hostName) {
        return cacheManager.get(hostName);
    }

    @Override
    public List<HostInfo> getAll() {
        Collection<HostInfo> values = cacheManager.values();
        return new ArrayList<>(values);
    }

    @Override
    public void update(int scheduleId, List<HostInfo> hosts) {
        cacheManager.clear();
        run(scheduleId, hosts, 1000, 3000);
    }

    public static void main(String[] args) throws InterruptedException {
        // 모니터링 조회 대상 리스트.
        List<HostInfo> list = getHosts();

        AliveSchedulerImplV2 aliveScheduler = new AliveSchedulerImplV2();

        aliveScheduler.run(1, list, 1000, 3000);

        System.out.println(" ====== first sleep start ======");
        Thread.sleep(5000);
        System.out.println(" ====== first sleep end ======");

        System.out.println(" ====== second sleep start ======");
        Thread.sleep(5000);
        System.out.println(" ====== second sleep end ======");

        System.out.println(" ====== getAll ====== ");
        List<HostInfo> getAll = aliveScheduler.getAll();
        System.out.println(getAll);
        System.out.println(" ====== getAll ====== ");

        System.out.println("======== get size =========");
        int size = aliveScheduler.getAll().size();
        System.out.println(size);
        System.out.println("======== get size =========");

        System.out.println(" ====== get ====== ");
        HostInfo hostInfoA = aliveScheduler.get("google.com5");
        System.out.println(hostInfoA);
        System.out.println(" ====== get ====== ");


        aliveScheduler.stop(1);

        System.out.println(" ====== 새로운 리스트 조회. ======");
        List<HostInfo> updateList = getUpdateList();


        Thread.sleep(3000);

        System.out.println(" 스케줄러 시작.");
        aliveScheduler.update(1, updateList);

        Thread.sleep(8000);

        System.out.println(" ====== get ====== ");
        HostInfo hostInfoB = aliveScheduler.get("google.com5");
        System.out.println(hostInfoB);
        System.out.println(" ====== get ====== ");

        aliveScheduler.stop(1);

        System.out.println("메인 스레드 종료.");
        System.out.println(Thread.currentThread().getName());


    }

    private static List<HostInfo> getUpdateList() {
        List<HostInfo> updateList = new ArrayList<>();
        String hostNameB = "naver.com";
        updateList.add(new HostInfo(hostNameB));
        for (int i = 0; i < 1000; i++) {
            updateList.add(new HostInfo(hostNameB + i));
        }
        return updateList;
    }

    private static List<HostInfo> getHosts() {
        String hostNameA = "google.com";
        List<HostInfo> list = new ArrayList<>();
        list.add(new HostInfo(hostNameA));
        for (int i = 0; i < 1000; i++) {
            list.add(new HostInfo(hostNameA + i));
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
