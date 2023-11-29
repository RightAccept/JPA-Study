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
            // 멤버를 생성
            Member member = saveMember(em);

            // 팀을 생성
            Team team = new Team();
            team.setName("teamA");
            
            // 여기가 애매하다
            // member의 값을 변경하는데, team에서 관리한다는 것이 이상한 부분
            // 실행시 update 쿼리가 추가로 나간다
            // -> member가 생성될 때 팀을 넣으면 insert만 나가면 되지만,
            //    이미 생성된 member에 team에서 관여하려고 하니 update가 추가로 나갈 수 밖에 없다
            // => team을 건드렸는데, member가 변경된다. 실무에서 테이블이 많아질 수록 관리가 어려워지게 된다
            // 결론 : 일대다 쓰지 말고 다대일 쓰자. 객체에서 조금 손해볼 수 있지만, 다대일 쓰는 것이 관리하기 좋다
            team.getMembers().add(member);

            em.persist(team);

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

    private static Member saveMember(EntityManager em) {
        Member member = new Member();
        member.setName("member1");

        em.persist(member);
        return member;
    }
}

// 일대다 단방향 정리
/*
* - 일대다 단방향은 일대다(1:N)에서 일(1)이 연관관계의 주인
* - 테이블 일대다 관계는 항상 다(N) 쪽에 외래키가 있음
* - 객체와 테이블의 차이 때문에 반대편 테이블의 외래키를 관리하는 특이한 구조
* - @JoinColumn을 꼭 사용해야 함. 그렇지 않으면 조인 테이블 방식을 사용함(중간에 테이블을 하나 추가함)
* -> 중간에 테이블이 들어가면 성능이 하향한다
*/

/*
* 일대다 단방향 단점
* - 엔티티가 관리하는 외래키가 다른 테이블에 있음
* -> Member를 주인으로 할 경우 insert만 하면 되지만, Team을 주인으로 할 경우 insert와 update를 사용해야 함
*/

// - 일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용하자
