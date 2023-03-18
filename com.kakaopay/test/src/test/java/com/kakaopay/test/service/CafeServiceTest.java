package com.kakaopay.test.service;

import com.kakaopay.test.config.exception.*;
import com.kakaopay.test.domain.Coffee;
import com.kakaopay.test.domain.Point;
import com.kakaopay.test.dto.HistoryReceiver;
import com.kakaopay.test.repository.CoffeeRepository;
import com.kakaopay.test.repository.HistoryRepository;
import com.kakaopay.test.repository.PointRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class CafeServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CoffeeService coffeeService;
    @Autowired
    private PointService pointService;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Test
    @Order(1)
    public void 초기_포인트데이터_개수확인() {
        List<Point> allPoint = pointRepository.findAll();
        Assertions.assertThat(allPoint.size()).isEqualTo(3);
    }

    @Test
    public void 포인트충전_범위초과_예외_테스트() {
        assertThrows(OverRangeException.class,
                () -> pointService.chargePoint("X00000",-1L));
    }

    @Test
    public void 구매_회원아이디_예외_테스트() {
        assertThrows(NoMemberException.class,
                () -> orderService.orderCoffee("Y00000", 100000L));
    }

    @Test
    public void 포인트충전_회원아이디_예외_테스트() {
        assertThrows(NoMemberException.class,
                () -> pointService.chargePoint("Y00000",1000L));
    }

    @Test
    public void 구매_메뉴아이디_예외_테스트() {
        assertThrows(NoMenuException.class,
                () -> orderService.orderCoffee("X00000", 300000L));
    }

    @Test
    @Order(2)
    public void 구매_포인트초과_예외_테스트() {
        pointService.chargePoint("X00000",0L);
        assertThrows(NotEnoughPointsException.class,
                () -> orderService.orderCoffee("X00000",100000L));
    }

    @Test
    @Order(3)
    public void 초기_데이터_개수확인() {
        List<Coffee> allCoffee = coffeeRepository.findAll();
        Assertions.assertThat(allCoffee.size()).isEqualTo(4);
    }

    @Test
    @Order(4)
    public void 베스트_커피조회_예외_테스트() {
        assertThrows(NoTopCoffeeContentException.class,
                () -> coffeeService.getTopCoffeeList());
    }

    @Test
    public void 커피조회_테스트() {
        List<Coffee> allCoffee = coffeeService.getCoffeeList();
        Assertions.assertThat(allCoffee.size()).isEqualTo(4);
    }

    @Test
    @Order(5)
    @Transactional
    public void 베스트_커피조회_테스트() {
        pointService.chargePoint("X00000" ,34500L);

        for(int i=0; i<3; i++){
            orderService.orderCoffee("X00000", 100000L);
            orderService.orderCoffee("X00000", 100001L);
            orderService.orderCoffee("X00000", 100003L);
        }
        orderService.orderCoffee("X00000", 100002L);

        List<Coffee> topCoffee = coffeeService.getTopCoffeeList();
        Assertions.assertThat(topCoffee.get(0).getMenuId()).isEqualTo(100000L);
        Assertions.assertThat(topCoffee.get(1).getMenuId()).isEqualTo(100001L);
        Assertions.assertThat(topCoffee.get(2).getMenuId()).isEqualTo(100003L);
    }

    @Test
    @Order(5)
    public void MOCK_이력내력_전송테스트(){
        Mono<HistoryReceiver> resMock = orderServiceImpl.historySender("X00000",100000L,1500L);
        Assertions.assertThat(resMock.block().getMenuId()).isEqualTo("100000");
        Assertions.assertThat(resMock.block().getData()).isEqualTo("success");
        Assertions.assertThat(resMock.block().getMemberId()).isEqualTo("X00000");
        Assertions.assertThat(resMock.block().getPayAmount()).isEqualTo("1500");
    }

    @Test
    @Order(6)
    @Transactional
    public void 주문이력_100회_동시성_테스트() throws InterruptedException {
        int threadCounter = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCounter);

        for (int i = 0; i < threadCounter; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint("X00000" ,10000L);
                    // 바닐라라떼 주문
                    orderService.orderCoffee("X00000", 100003L);
                    // 아메리카노 주문
                    orderService.orderCoffee("X00000",100000L);
                    // 카페라떼 주문
                    orderService.orderCoffee("X00000",100001L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<Long> testList = Arrays.asList(100000L, 100001L, 100003L);
        testList.stream().forEach(e -> {
            Assertions.assertThat(historyRepository.findHistoryAmountCount(e)).isEqualTo(100);
        });
    }

    @Test
    @Order(7)
    @Transactional
    public void 동시에_100번_구매시도() throws InterruptedException {
        int threadCounter = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCounter);
        for (int i = 0; i < threadCounter; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint("X00000" ,1500L);
                    orderService.orderCoffee("X00000",100000L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Optional<Point> point = pointRepository.findByMemberId("X00000");
        Assertions.assertThat(0L).isEqualTo(point.get().getPoints());
    }

    @Test
    @Order(8)
    @Transactional
    public void 목업_100회_동시성_테스트() throws InterruptedException {
        List<HistoryReceiver> hr = Collections.synchronizedList(new ArrayList<>());

        int threadCounter = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCounter);

        for (int i = 0; i < threadCounter; i++) {
            executorService.submit(() -> {
                try {
                    HistoryReceiver resMock = orderServiceImpl.historySender("X00001",100001L,1501L).block();
                    hr.add(resMock);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Assertions.assertThat(hr.size()).isEqualTo(100);
        hr.stream().forEach(e -> {
            Assertions.assertThat(e.getData()).isEqualTo("success");
            Assertions.assertThat(e.getMemberId()).isEqualTo("X00001");
            Assertions.assertThat(e.getMenuId()).isEqualTo("100001");
            Assertions.assertThat(e.getPayAmount()).isEqualTo("1501");
        });
    }

    @Test
    @Order(9)
    @Transactional
    public void 동시에_100번_충전시도() throws InterruptedException {
        int threadCounter = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCounter);

        for (int i = 0; i < threadCounter; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint("X00000", 1000L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Optional<Point> point = pointRepository.findByMemberId("X00000");
        Assertions.assertThat(100000L).isEqualTo(point.get().getPoints());
    }

}
