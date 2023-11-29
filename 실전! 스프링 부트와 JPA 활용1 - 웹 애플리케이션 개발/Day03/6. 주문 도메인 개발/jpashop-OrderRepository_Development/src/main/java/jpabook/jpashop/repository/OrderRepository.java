package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 주문 리포지토리에는 주문 엔티티를 저장하고 검색하는 기능이 있다.
    // findAll(OrderSearch orderSearch) 메서드는 조금 뒤에 있는 주문 검색 기능에서 자세히 알아보자
//    public List<Order> findAll(OrderSearch orderSearch) {}
}
