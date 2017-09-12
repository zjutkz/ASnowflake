package com.github.zjutkz.snowflake;

import com.github.zjutkz.IDCreator;
import com.github.zjutkz.exception.TimeBackwardException;

import java.util.Random;

/**
 * Created by kangzhe on 17/9/12.
 */

public class SnowFlake implements IDCreator {

    private static final long MAX_SEQ_ID = 4095;
    private static final long MAX_MACHINE_ID = 31;

    private static final int DATACENTER_BITS_LENGTH = 5;
    private static final int WORKER_ID_BITS_LENGTH = 5;
    private static final int SEQ_BITS_LENGTH = 12;

    private static final int TIMESTAMP_SHIFT = DATACENTER_BITS_LENGTH
            + WORKER_ID_BITS_LENGTH + SEQ_BITS_LENGTH;
    private static final int DATACENTER_SHIFT = WORKER_ID_BITS_LENGTH + SEQ_BITS_LENGTH;
    private static final int WORKER_ID_SHIFT = SEQ_BITS_LENGTH;

    private static SnowFlake sInstance;

    private long lastTimestamp;
    private long datacenterId;
    private long workerId;
    private long sequence;


    //=============== public function ===============
    public static SnowFlake getInstance(){
        if(sInstance == null){
            synchronized (SnowFlake.class){
                if(sInstance == null){
                    sInstance = new SnowFlake();
                }
            }
        }
        return sInstance;
    }

    public SnowFlake(){
        Random random = new Random();
        datacenterId = random.nextInt((int) MAX_MACHINE_ID);
        workerId = random.nextInt((int) MAX_MACHINE_ID);
    }

    @Override
    public synchronized long nextId() {
        long timestamp = processTimestamp();
        timestamp = processSequence(timestamp);
        replaceLastFrame(timestamp);
        return (timestamp << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    //=============== private function ===============

    /**
     * Getting first 41bits-timestamp
     * @return
     */
    private long processTimestamp() {
        long timestamp = getTimestamp();
        if (checkTimeFlow(timestamp)) {
            throw new TimeBackwardException(String.format("time getting back: %s",String.valueOf(lastTimestamp - timestamp)));
        }
        return timestamp;
    }

    /**
     * Getting last 12bits-sequence
     * @return
     * @param timestamp
     */
    private long processSequence(long timestamp) {
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQ_ID;
            if (sequence == 0) {
                timestamp = waitForNextFrame(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        return timestamp;
    }

    private long waitForNextFrame(long lastTimestamp) {
        long timestamp = getTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getTimestamp();
        }
        return timestamp;
    }

    private long getTimestamp(){
        return System.nanoTime();
    }

    private boolean checkTimeFlow(long timestamp) {
        return timestamp < lastTimestamp;
    }

    private void replaceLastFrame(long timestamp) {
        lastTimestamp = timestamp;
    }
}
