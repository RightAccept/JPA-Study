package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
    xToOne(ManyToOne, OneToOne)에서 성능 최적화를 어떻게 하는가
    Order
    Order -> Member => ManyToOne
    Order -> Delivery   => OneToOne
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    // v1 : 엔티티 직접 노출
    //  - Hibernate5Module 모듈 등록, LAZY=null 처리
    //  - 양방향 관계 문제 발생 -> @JsonIgnore
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 1.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // 2. JpashopApplication에서 hibernate5Module.configure 주석 후 실행
        for (Order order : all) {
            order.getMember().getName();    // Lazy 강제 초기화
            order.getDelivery().getAddress();   // Lazy 강제 초기화
        }
        return all;
    }
    // stackOverFlow 발생
    // - Order에 가니 Member가 있고, member에 갔더니 List<Order>가 있고, 그래서 다시 Order 갔더니 Member가 있고 .... 무한 반복
    // 해결책 : 양방향 연관관계에서는 한쪽에 @JsonIgnore를 걸어줘야 한다
    // - Delivery, Member에 @JsonIgnore를 걸었다

    // 그래도 예외 발생
    // com.fasterxml.jackson.databind.exc.InvalidDefinitionException
    // order -> member와 order -> delivery는 지연 로딩이다. 따라서 실제 엔티티 대신 프록시가 존재
    // - jackson 라이브러리는 기본적으로 이 프록시 객체를 json으로 어떻게 생성해야 하는지 모름 -> 예외 발생
    // 해결책 : Hibernate5Module을 스프링 빈으로 등록하면 해결(스프링 부트 사용중)
    //  - build.gradle에 라이브러리 추가
    //  - JpashopApplication에 @Bean 등록

    
    // v2 : 엔티티를 조회해서 DTO로 변환(fetch join 사용 X)
    //  - 단점 : 지연로딩으로 쿼리 N번 호출
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return result;
    }
    // - 엔티티를 DTO로 변환하는 일반적인 방법
    // - 쿼리가 총 1 + N + N번 실행된다(v1과 쿼리수 결과는 같다)
    //  - order 조회 1번(order 조회 결과 수가 N이 된다)
    //  - order -> member 지연 로딩 조회 N번
    //  - order -> delivery 지연 로딩 조회 N번
    //  ex) order의 결과가 4개면 최악의 경우 1 + 4 + 4번 실행 /된다
    //      - 지연 로딩은 영속성 컨텍스트에서 조회하므로, 이미 조회된 경우 쿼리를 생략한다

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
