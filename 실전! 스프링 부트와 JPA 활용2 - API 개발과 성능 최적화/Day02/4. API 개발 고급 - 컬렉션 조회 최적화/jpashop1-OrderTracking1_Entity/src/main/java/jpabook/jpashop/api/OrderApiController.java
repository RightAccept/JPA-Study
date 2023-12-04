package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();    // LAZY 강제 초기화
            order.getDelivery().getAddress();   // LAZY 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());    // LAZY 강제 초기화
        }
        return all;
    }
    // v1 : 엔티티 직접 노출
    //  - 엔티티가 변하면 API 스펙이 변한다
    //  - 트랜잭션 안에서 지연 로딩 필요
    //  - 양방향 연관관계 문제
    //  - Hibernate5Module 모듈 등록, LAZY = null 처리
    //  - 양방향 관계 문제 발생 -> @JsonIgnore
}
