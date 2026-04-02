package kuke.board.articleread.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ViewClientTest {
    @Autowired
    ViewClient viewClient;

    @Test
    void readCacheableTest() throws InterruptedException {
        viewClient.count(1L); // log 출력 (캐시가 없을때 내부 로직이 호출된다.)
        viewClient.count(1L); // log 미출력 (캐시가 있을때 로직 호출 없이 데이터 보여준다)
        viewClient.count(1L); // log 미출력

        TimeUnit.SECONDS.sleep(3);
        viewClient.count(1L); // log 출력
    }

    @Test
    void readCacheableMultiThreadTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        viewClient.count(1L); // init cache

        for (int i = 0; i < 5; i++) {
            CountDownLatch latch = new CountDownLatch(5);
            for (int j = 0; j < 5; j++) {
                executorService.submit(() -> {
                    viewClient.count(1L);
                    latch.countDown();
                });
            }
            latch.await();
            TimeUnit.SECONDS.sleep(2);
            System.out.println("=== cache expired ===");

        }
    }

}