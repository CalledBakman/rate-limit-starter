package com.github.calledbakman.rate_limit_starter;

import com.github.calledbakman.rate_limit_starter.enums.RateLimitScope;
import com.github.calledbakman.rate_limit_starter.exception.RateLimitExceededForClientException;
import com.github.calledbakman.rate_limit_starter.exception.ServiceRateLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class RateLimitingService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);
    private ConcurrentHashMap<String, RequestLogs> logs = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledThread;

    public static class RequestLogs {
        private long lastUseTimeStamp;
        private final ArrayDeque<Long> timeStamps = new ArrayDeque<>();
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

        public void removeExpiredRequests(Long windowStartTime){
            int before = this.timeStamps.size();

            while (!this.timeStamps.isEmpty() && this.timeStamps.peek() < windowStartTime)
                this.timeStamps.poll();

            int after = this.timeStamps.size();

            logger.info("There was {} expired requests!", before - after);
        }

        public int getCurrentSize(){
            return this.timeStamps.size();
        }

        public boolean isExpired(long windowTime){
            return this.lastUseTimeStamp < System.currentTimeMillis() - windowTime;
        }

        public void addRequest(Long timeStamp){
            try {
                this.timeStamps.add(timeStamp);
                lastUseTimeStamp = System.currentTimeMillis();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void clear(){
            this.timeStamps.clear();
        }

        public boolean isEmpty(){
            return this.timeStamps.isEmpty();
        }

        public Long peek(){
            return this.timeStamps.peek();
        }
    }

    public void checkRateLimit(RateLimitScope limitScope,
                               String key,
                               int limit,
                               Long timeWindowMillis) throws InterruptedException {

        RequestLogs currentLogs = logs.computeIfAbsent(key, k -> new RequestLogs());
        Lock lock = currentLogs.rwLock.writeLock();
        try {
            lock.lock();

            if (!currentLogs.isEmpty() && currentLogs.isExpired(timeWindowMillis)) {
                currentLogs.clear();
                logs.remove(key);
                currentLogs = new RequestLogs();
                currentLogs.addRequest(System.currentTimeMillis());
                logs.put(key, currentLogs);
            } else {
                if (currentLogs.isEmpty() && currentLogs.lastUseTimeStamp != 0L)
                    currentLogs = logs.computeIfAbsent(key, k -> new RequestLogs());

                final Long currentTime = System.currentTimeMillis();
                final Long windowStartTime = currentTime - timeWindowMillis;

                currentLogs.removeExpiredRequests(windowStartTime);

                if (currentLogs.getCurrentSize() < limit) {
                    currentLogs.addRequest(currentTime);
                } else if (limitScope == RateLimitScope.CLIENT)
                    throw new RateLimitExceededForClientException((currentLogs.peek() + timeWindowMillis - System.currentTimeMillis()) / 1000);
                else
                    throw new ServiceRateLimitExceededException((currentLogs.peek() + timeWindowMillis - System.currentTimeMillis()) / 1000);
            }
        } finally {
            lock.unlock();
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("The current thread interrupted!");
        }

    }

    public void schedule(CleanUpConfig configuration){
        long interval = configuration.getCleanUpInterval();

        scheduledThread = scheduledExecutor.scheduleWithFixedDelay(
                () -> cleanUpMemory(configuration.getWindowTime()),
                interval,
                interval,
                TimeUnit.MILLISECONDS
                );

        logger.info("Started periodic cleanup every {} milliseconds", interval);
    }

    public void cleanUpMemory(long windowTime){
        for (Map.Entry<String, RequestLogs> object : logs.entrySet()) {
            RequestLogs current = object.getValue();
            Lock lock = current.rwLock.writeLock();
            try {
                lock.lock();
                if (current.isExpired(windowTime))
                    logs.remove(object.getKey());
            } finally {
                lock.unlock();
            }
        }
    }
}