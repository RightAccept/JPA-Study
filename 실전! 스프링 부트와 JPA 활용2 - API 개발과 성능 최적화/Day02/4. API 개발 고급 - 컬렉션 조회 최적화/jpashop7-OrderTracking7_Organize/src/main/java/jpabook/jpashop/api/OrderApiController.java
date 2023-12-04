package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
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
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return result;
    }

    // v3 : 쿼리가 한 번에 나간다, DB에서 중복 데이터를 애플리케이션으로 보내기 때문에 메모리 낭비가 크다
    // v3.1 : 지연로딩으로 인해 쿼리가 여러 번 나간다, 데이터 중복 없이 DB에서 애플리케이션으로 보내기 때문에 최적화 되어 있다

    // 페이징과 한계 돌파
    // - 컬렉션을 페치 조인하면 페이징이 불가능하다
    //  - 컬렉션을 페치 조인하면 일대다 조인이 발생하므로 데이터가 예측할 수 없이 증가한다
    //  - 일대다에서 일(1)을 기중으로 페이징을 하는 것이 목적
    //      - 그런데 데이터틑 다(N)을 기준으로 row가 생성된다
    //  - Order를 기준으로 페이징하고 싶은데, 다(N)인 OrderItem을 조인하면 OrderItem이 기준이 되어버린다
    // - 이 경우 하이버네이트는 경고 로그를 남기고 모든 DB 데이터를 읽어서 메모리에서 페이징을 시도한다
    //   - 최악의 경우 장애로 이어질 수 있다
    
    // 한계 돌파 : 페이징 + 컬렉션 엔티티를 함께 조회하려면
    // 1. ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다. ToOne 관계는 row 수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다
    // 2. 컬렉션은 지연 로딩으로 조회한다
    // 3. 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size, @BatchSize를 적용한다
    //  - hibernate.default_batch_size : 글로벌 설정 => application.yml 확인
    //  - @BatchSize : 개별 최적화   => Order 엔티티의 orderItems 확인
    //  - 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size만큼 IN 쿼리로 조회한다
    
    @GetMapping("api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset, @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return result;
        // 장점
        // - 쿼리 호출 수가 1 + N -> 1 + 1로 최적화 된다
        // - 조인보다 DB 데이터 전송량이 최적화 된다
        //  - Order와 OrderItem을 조인하면 Order가 OrderItem만큼 중복해서 조회된다
        //  - 이 방법은 각각 조회하므로 전송해야할 중복 데이터가 없다
        // - 페치 조인 방식과 비교해서 쿼리 호출 수가 약간 증가하지만, DB 데이터 전송량이 감소한다
        // - 컬렉션 페치 조인은 페이징이 불가능하지만 이 방법은 페이징이 가능하다

        // 결론
        // - ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않는다
        //  - 따라서 ToOne 관계는 페치 조인으로 쿼리 수를 줄이고 해결하고, 나머지는 hibernate.default_batch_fetch_size로 최적화 하자

        // 참고
        // - default_batch_fetch_size의 크기는 적당한 사이즈를 골라야 하는데, 100~1000 사이를 선택하는 것을 권장
        //  - SQL IN절을 사용하는데, DB에 따라 IN 절 파라미터를 1000으로 제한하기도 한다
        // - 1000으로 잡으면 한 번에 1000개를 DB에서 애플리케이션에 불러오므로 DB에 순간 부하가 증가할 수 있다
        //  - 하지만 애플리케이션은 100이든 1000이든 결국 전체 데이터를 로딩해야 하므로 메모리 사용량이 같다
        // - 1000으로 설정하는 것이 성능상 가장 좋지만, 결국 DB든 애플리케이션이든 순간 부하를 어디까지 견딜 수 있는지로 결정하면 된다
    }
    
    // Query : 루트 1번, 컬렉션 N번 실행
    // ToOne(N:1, 1:1) 관계들을 먼저 조회하고, ToMany(1:N) 관계는 각각 별도로 처리
    //  - 이런 방식을 선택한 이유
    //      - ToOne 관계는 조인해도 데이터 row 수가 증가하지 않는다
    //      - ToMany(1:N) 관계는 조인하면 row 수가 증가한다
    // row 수가 증가하지 않는 ToOne 관계는 조인으로 최적화 하기 쉬우므로 한번에 조회하고, ToMany 관계는 최적화 하기 어려우므로 findOrderItems() 같은 별도의 메서드로 조회한다
    // 생성
    // repository 패키지 하위에 order - query 패키지 생성
    //  - query 패키지 내부에 Dto 2개와 OrderQueryRepository 생성
    @GetMapping("api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // 반환이 OrderQueryDto이기 때문에, OrderQueryDto에 List에 관한 생성자를 추가
    // Query : 1번
    // 단점
    //  - 쿼리는 한번이지만 조인으로 인해 DB에서 애플리케이션에 전달하는 데이터에 중복 데이터가 추가되므로 상황에 따라 v5보다 더 느릴 수도 있다
    //  - 애플리케이션에서 추가 작업이 크다
    //  - 페이징 불가능
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
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
            orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());
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

// 정리
// ※ 엔티티 조회
//  - 엔티티를 조회해서 그대로 반환 : v1
//  - 엔티티 조회 후 DTO로 변환 : v2
//  - 페치 조인으로 쿼리 수 최적화 : v3
//  - 컬렉션 페이징과 한계 돌파 : v3.1
//      - 컬렉션은 페치 조인 시 페이징이 불가능
//      - ToOne 관계는 페치 조인으로 쿼리 수 최적화
//      - 컬렉션은 페치 조인 대신에 지연 로딩을 유지하고, hibernate.default_batch_fetch_size, @BatchSize 최적화
// ※ DTO 직접 조회
//  - JPA에서 DTO를 직접 조회 : v4
//  - 컬렉션 조회 최적화 - 일대다 관계인 컬렉션은 IN절을 활용해서 메모리에 미리 조회해서 최적화 : v5
//  - 플랫 데이터 최적화 - JOIN 결과를 그대로 조회 후 애플리케이션에서 원하는 모양으로 직접 변환 : v6

// 권장 순서
// 1. 엔티티 조회 방식으로 우선 접근
//  1. 페치 조인으로 쿼리 수를 최적화
//  2. 컬렉션 최적화
//      1. 페이징 필요 hibernate.default_batch_fetch_size, @BatchSize 최적화
//      2. 페이징 필요X -> 페치 조인 사용
// 2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
// 3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JDBC Template

// 참고
// ※ 엔티티 조회 방식은 페치 조인이나, hibernate.default_batch_fetch_size, @BatchSize 같이 코드를 거의 수정하지 않고, 옵션만 약간 병경해서,
//  다양한 성능 최적화를 시도할 수 있다
//  - 반면에 DTO를 직접 조회하는 방식은 성능을 최적화 하거나 성능 최적화 방식을 변경할 때 많은 코드를 수정해야 한다
//  - 엔티티 조회 방식 추천

// ※ 개발자는 성능 최적화와 코드 복잡도 사이에서 줄타기를 해야 한다
//  - 항상 그런 것은 아니지만, 보통 성능 최적화는 단순한 코드를 복잡한 코드로 몰고간다
//  - 엔티티 조회 방식은 JPA가 많은 부분을 최적화 해주기 때문에, 단순한 코드를 유지하면서, 성능을 최적화 할 수 있다
//  - DTO 조회 방식은 SQL을 직접 다루는 것과 유사하기 때문에, 둘 사이에 줄타기를 해야 한다

// DTO 조회 방식의 선택지
// ※ DTO로 조회하는 방법도 각각 장단이 있다. V4, V5, V6에서 단순하게 쿼리가 1번 실행된다고 V6이 항상 좋은 방법은 아니다
//  - V4 : 코드가 단순하다
//      - 특정 주문 한 건만 조회하면 이 방식을 사용해도 성능이 잘 나온다
//      - 예를 들어서 조회한 Order 데이터가 1건이면 OrderItem을 찾기 위한 쿼리도 1번만 실행하면 된다
//  - V5 : 코드가 복잡하다
//      - 여러 주문을 한꺼번에 조회하는 경우에는 V4 대신에 이것을 최적화한 V5 방식을 사용해야 한다
//      - 예를 들어서 조회한 Order 데이터가 1000건인데, V4 방식을 그대로 사용하면, 쿼리가 총 1 + 1000번 실행된다
//          - 여기서 1은 Order를 조회한 쿼리고, 1000은 조회된 Order의 row 수다.
//      - V5 방식으로 최적화 하면 쿼리가 총 1 + 1번만 실행된다
//      - 상황에 따라 다르겠지만 운영 환경에서 100배 이상의 성능 차이가 날 수 있다
//  - V6 : 완전히 다른 접근 방식
//      - 쿼리 한 번으로 최적화 되어서 상당히 좋아보이지만, Order를 기준으로 페이징이 불가능하다
//      - 실무에서는 이 정도 데이터면 수백이나, 수천건 단위로 페이징 처리가 꼭 필요하므로, 이 경우 선택하기 어려운 방법이다
//      - 그리고 데이터가 많으면 중복 전송이 증가해서 V5와 비교해서 성능 차이도 미비하다
