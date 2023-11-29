package jpabook;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {


            tx.commit();    // 현재 트랜잭션을 commit
        } catch (Exception e) {
            tx.rollback();
        } finally {
            // entityManger 닫기
            em.close();
        }
        // entityMangerFactory 닫기
        emf.close();

    }
}

// 일대일 관계는 그 반대도 일대일
// 주 테이블이나 대상 테이블 중 외래키 선택 가능
// - 주 테이블에 외래키
// - 대상 테이블에 외래키
// 외래키에 데이터베이스 유니크 제약조건 추가

// 하나의 멤버가 하나의 Locker만 가질 수 있다고 가정
// Locker가 Member_id를 가지거나, Member가 Locker_id를 가지거나 크게 차이가 없다

// 일대일 : 주 테이블에 외래키 단방향 정리
// - 다대일(@ManyToOne) 단방향 매핑과 유사

// 단방향 관계는 JPA 지원X
// 양방향 관계는 지원

// 외래키를 Member가 가지고 있는게 좋을까? Locker가 가지고 있는게 좋을까?
// - 누가 가지고 있어도 일대일은 성립한다
// - 정답은 없지만, 만약 비즈니스 룰이 바껴서 하나의 회원이 여러 개의 라커를 가질 수 있다고 할 때
// - DB입장에서는 Locker가 가지고 있으면 Unique만 지워주면 하나의 회원이 여러 개의 라커 사용이 가능해진다
// - 개발자 입장에서는 Member가 Locker를 가지고 있는 것이 작업하기 편해진다
// -> Member만 select해도 Locker의 데이터를 가져올 수 있으니까

// 일대일 정리
/*
* 주 테이블에 외래키
* - 주 객체가 대상 객체의 참조를 가지는 것 처럼 주 테이블에 외래 키를 두고 대상 테이블을 찾음
* - 객체지향 개발자 선호
* - JPA 매핑 편리
* - 장점 : 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능
* - 단점 : 값이 없으면 외래 키에 null 허용
* 
* 대상 테이블에 외래키
* - 대상 테이블에 외래키가 존재
* - 전통적인 데이터베이스 개발자가 선호
* - 장점 : 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지
* - 단점 : 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩됨
*/