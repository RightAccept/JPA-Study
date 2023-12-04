package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    // v1 : 엔티티 직접 노출
    //  - 엔티티가 변하면 API 스펙이 변한다
    //  - 트랜잭션 안에서 지연 로딩 필요
    //  - 양방향 연관관계 문제
    //  - Hibernate5Module 모듈 등록, LAZY = null 처리
    //  - 양방향 관계 문제 발생 -> @JsonIgnore
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

    // v2 : Entity를 Dto로 변경
    //  - Dto로 변환할 때 내부 컬렉션에 있는 엔티티도 Dto로 변경해야 함
    //  - 지연 로딩으로 너무 많은 SQL 실행
    //      - SQL 실행 수
    //          - order 1번, member, address N번(order 조회 수 만큼), orderItem N번(order 조회 수 만큼), item N번(orderItem 조회 수 만큼)
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return result;
    }
    
    // v3 : 페치 조인으로 SQL이 1번만 실행됨
    // - DISTINCT를 사용한 이유는 1대다 조인이 있으므로 데이터베이스 row 수가 증가한다
    //  - 그 결과 같은 order 엔티티의 조회 수도 증가하게 된다
    //  - JPA의 distinct는 SQL에 distinct를 추가하고, 더해서 같은 엔티티가 조회되면, 애플리케이션에서 중복을 걸러준다
    //  - 이 예에서 order가 컬렉션 페치 조인 때문에 중복 조회 되는 것을 막아준다
    // 단점
    //  - 페이징 불가능
    //      - 하이버네이트는 경고 로그를 남기면서 모든 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다
    // 참고
    //  - 컬렉션 페치 조인은 1개만 사용할 수 있다.
    //      - 컬렉션 둘 이상에 페치 조인을 사용하면 데이터가 부정합하게 조회될 수 있다
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return result;
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
//        private List<OrderItem> orderItems;
        // Entity인 OrderItem이 Dto로 감싸인 상태로 외부로 노출된다
        // 중요한 것은 감싸는 것이 아닌, Entity 와의 연결을 완전히 끊는 것
        // OrderItem에 해당하는 Dto도 생성해야한다
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); // LAZY 초기화
//            orderItems = order.getOrderItems();
            orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName;    // 상품명
        private int orderPrice; // 주문 가격
        private int count;  // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
