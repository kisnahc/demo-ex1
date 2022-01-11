package com.kisnah.demoex1;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class AliveSchedulerImpl implements AliveScheduler {

    private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
    private static final Map<Integer, ScheduledFuture<?>> scheduleTasks = new ConcurrentHashMap<>();

    private static final List<HostInfo> result = new ArrayList<>();

    private boolean shutdown = false;
    @Override
    public void run(int scheduleId, List<HostInfo> hosts, int initialDelayMs, int delayMs) throws InterruptedException {

        if (shutdown) throw new InterruptedException("스케줄러 종료");
        ScheduledFuture<?> future = ses.scheduleWithFixedDelay(() -> {
            for (HostInfo host : hosts) {
                boolean alive = isAlive(host.getHostName());
                host.setAlive(alive);
                System.out.println("HOST = " + host);
            }
        }, initialDelayMs, delayMs, TimeUnit.MILLISECONDS);
        scheduleTasks.put(scheduleId, future);
    }

    @Override
    public void stop(int scheduleId) throws ExecutionException, InterruptedException {
        System.out.println(" ================================ 스케줄러 종료. ================================");
        ses.shutdown();
        shutdown = true;

        scheduleTasks.get(scheduleId).cancel(true);

        scheduleTasks.clear();

        System.out.println(" ================================ 스케줄러 종료. ================================");
    }

    @Override
    public void get(int scheduleId, String hostName) {
        scheduleTasks.get(1);
    }

    @Override
    public void getAll(int scheduleId) throws ExecutionException, InterruptedException {
        System.out.println(" =============== getAll =============== ");
        System.out.println(scheduleTasks.get(scheduleId));
        System.out.println(" =============== getAll =============== ");
    }

    @Override
    public void update(int scheduleId, List<HostInfo> hosts) throws ExecutionException, InterruptedException {
//        stop(1);
//        Thread.sleep(2000);
        run(scheduleId, hosts, 1000, 3000);
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


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String hostNameA = "google.com";
        List<HostInfo> list = new ArrayList<>();

        list.add(new HostInfo(hostNameA));
        for (int i = 0; i < 10; i++) {
            list.add(new HostInfo(hostNameA + i));
        }

        AliveSchedulerImpl aliveScheduler = new AliveSchedulerImpl();

        aliveScheduler.run(1, list, 1000, 3000);

        Thread.sleep(8000);

        aliveScheduler.getAll(1);

        Thread.sleep(10000);

        aliveScheduler.stop(1);

        List<HostInfo> updateList = new ArrayList<>();
        String hostNameB = "naver.com";
        updateList.add(new HostInfo(hostNameB));
        for (int i = 0; i < 10; i++) {
            updateList.add(new HostInfo(hostNameB + i));
        }

        aliveScheduler.update(1, updateList);

    }
}
