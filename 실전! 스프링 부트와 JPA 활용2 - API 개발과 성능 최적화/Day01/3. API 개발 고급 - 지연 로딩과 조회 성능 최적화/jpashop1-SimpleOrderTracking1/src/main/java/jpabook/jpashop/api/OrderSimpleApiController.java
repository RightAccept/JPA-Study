package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @GetMapping("/api/v1/simple-drders")
    public List<Order> ordersV1() {
        // 1.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        
        // 2.
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
}
