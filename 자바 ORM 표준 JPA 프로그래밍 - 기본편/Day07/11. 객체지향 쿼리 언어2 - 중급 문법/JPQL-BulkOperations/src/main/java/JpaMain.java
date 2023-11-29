import jpql.*;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setTeam(teamB);
            em.persist(member3);
            
            // 벌크 연산 예제
            int resultCount = em.createQuery("update Member m set m.age = 20").executeUpdate();
            System.out.println("resultCount = " + resultCount);
            
            // 위의 persist들을 flush 하지 않았는데도 insert 구문이 출력된다
            // -> 벌크 연산을 진행하면서 쿼리문이 사용되기 때문에, 자동으로 flush 되는 것
            // => flush는 됐으니 clear만 잘 해주면 된다

            // clear 하지 않았을 때의 문제점 예시
            System.out.println("member1.getAge() = " + member1.getAge());
            System.out.println("member2.getAge() = " + member2.getAge());
            System.out.println("member3.getAge() = " + member3.getAge());
            // 기본값인 0으로 출력된다
            // 하지만 DB를 확인하면 20으로 설정되어 있는 것을 확인할 수 있다

            // clear 후 다시 출력
            em.clear();

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember = " + findMember.getAge());

            tx.commit();
        } catch (Exception e) {
            System.err.println("예외 발생 : " + e);
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}

// 벌크 연산
// ※ PK를 찍어서 하나만 수정, 삭제하는 경우를 제외한 여러 데이터를 변경하는 경우
//  ex) 재고가 10개 미만인 모든 상품의 가격을 10% 상승하려면?
//  - JPA 변경 감지 기능으로 실행하려면 너무 많은 SQL 실행
//      - 1. 재고가 10개 미만인 상품을 리스트로 조회한다
//      - 2. 상품 엔티티의 가격을 10% 증가한다
//      - 3. 트랜잭션 커밋 시점에 변경감지가 동작한다
//  - 변경된 데이터가 100건이라면 100번의 UPDATE SQL 실행

// 벌크 연산 예제
// ※ 쿼리 한 번으로 여러 테이블 로우 변경(엔티티)
// ※ executeUpdate()의 결과는 영향받은 엔티티 수 반환
// ※ update, delete 지원
// ※ insert(insert into .. select, 하이버네이트 지원)

// 벌크 연산 주의
// ※ 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리
//  - 영속성 컨텍스트와 DB의 데이터가 다를 수 있다 => 잘못하면 꼬인다
//      - 해결책 1. 영속성 컨텍스트에 아무 값도 넣지 않고 벌크 연산을 먼저 실행
//      - 해결책 2. 벌크 연산 수행 후 영속성 컨텍스트 초기화 => 벌크 연산도 SQL문이 나가기 때문에 flush()가 된다. 따라서 clear()만 해주면 된다