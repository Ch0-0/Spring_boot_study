package com.kakaopay.test.controller;

import com.kakaopay.test.dto.Message;
import com.kakaopay.test.service.CoffeeService;
import com.kakaopay.test.service.OrderService;
import com.kakaopay.test.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kakaopay.test.config.Constants.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CafeController {
    private final CoffeeService coffeeService;
    private final PointService pointService;
    private final OrderService orderService;

    /**
     * [ 요구사항 1 ] 커피 메뉴 목록 조회 API
     * @return ResponseEntity
     */
    @GetMapping("/coffee")
    public ResponseEntity<?> getCoffeeList() {
        return new ResponseEntity<>(new Message<>(HttpStatus.OK.value(), COFFEE_LIST_SUCCESS, coffeeService.getCoffeeList()), HttpStatus.OK);
    }

    /**
     * [ 요구사항 2 ] 포인트 충전하기 API
     * @return ResponseEntity
     */
    @PutMapping("/point")
    public ResponseEntity<?> chargePoint(@RequestParam String memberId, @RequestParam Long points){
        return new ResponseEntity<>(new Message<>(HttpStatus.OK.value(), CHARGE_POINT_SUCCESS, pointService.chargePoint(memberId, points)), HttpStatus.OK);
    }

    /**
     * [ 요구사항 3 ] 커피 주문, 결제하기 API
     * @return ResponseEntity
     */
    @PostMapping("/coffee")
    public ResponseEntity<?> orderCoffee(@RequestParam String memberId, @RequestParam Long menuId){
        return new ResponseEntity<>(new Message<>(HttpStatus.CREATED.value(), COFFEE_ORDER_SUCCESS, orderService.orderCoffee(memberId, menuId)), HttpStatus.CREATED);
    }

    /**
     * [ 요구사항 4 ] 인기메뉴 목록 조회 API
     * @return ResponseEntity
     */
    @GetMapping("/top/coffee")
    public ResponseEntity<?> getTopCoffeeList() {
        return new ResponseEntity<>(new Message<>(HttpStatus.OK.value(), TOP_COFFEE_LIST_SUCCESS, coffeeService.getTopCoffeeList()), HttpStatus.OK);
    }

}
