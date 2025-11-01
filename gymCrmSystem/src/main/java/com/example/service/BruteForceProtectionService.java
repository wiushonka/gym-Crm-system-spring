package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
* count until user is locked, saves timestamp when user was locked, on login if required time is gone then user
* will be unblocked and get access to site + BCrypt is slow hashing algo => bruteforce attack will take looong time
* Blocking (Atomic) data structures must be used since in stateless env, requests wont know about each other.
*/

@Service
public class BruteForceProtectionService {

    private static final Logger logger = LoggerFactory.getLogger(BruteForceProtectionService.class);

    private static final int MAX_ATTEMPTS = 3;
    private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(5);
    private final Map<String,Integer> attemptsCache = new ConcurrentHashMap<String, Integer>();
    private final Map<String,Long> lockCache = new ConcurrentHashMap<>();

    public void goodLogin(String key){
        attemptsCache.remove(key);
        lockCache.remove(key);
    }

    public void loginFailed(String key){
        int attempts = attemptsCache.getOrDefault(key,0);
        ++attempts;
        logger.info("USER FAILED LOGGIN AGAIN, COUNT OF FAILED ATTEMPTS ------------> {}",attempts);
        attemptsCache.put(key,attempts);
        if(attempts > MAX_ATTEMPTS){
            logger.info("BLOCKED FOR 5 MINS: USER WITH USERNAME  ------------> {}",key);
            lockCache.put(key,System.currentTimeMillis());
        }
    }

    public boolean isLocked(String key){
        if(!lockCache.containsKey(key)){
            logger.info("{} IS NOT LOCKED",key);
            return false;
        }
        Long curTime = System.currentTimeMillis();
        Long lockedTime = lockCache.get(key);
        if(curTime - lockedTime > TIMEOUT){
            logger.info("{} IS LOCKED",key);
            lockCache.remove(key);
            attemptsCache.remove(key);
            return false;
        }
        return true;
    }

}
