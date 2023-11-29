package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext // EntityManagerFactory의 역할을 어노테이션이 대신한다 => build에서 spring-data-jpa와 application.yml을 통해서 팩토리 설정이 완료된다
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}

// 테스트 생성 : ctrl + shift + t
