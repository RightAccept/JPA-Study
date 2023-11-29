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
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

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

// 다형성 쿼리
// TYPE
// ※ 조회 대상을 특정 자식으로 한정
// ex) Item 중에 Book, Movie를 조회해라
//  - JPQL :s elect i from Item i where type(i) IN (Book, Movie)
//  - SQL : select i from i where i.DTYPE in (‘B’, ‘M’)

// TREAT(JPA 2.1)
// ※ 자바의 타입 캐스팅과 유사
// ※ 상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용
// ※ FROM, WHERE, SELECT(하이버네이트 지원) 사용
// ex) 부모인 Item과 자식 Book이 있다
//  - JPQL : select i from Item i where treat(i as Book).author = ‘kim’
//  - SQL : select i.* from Item i where i.DTYPE = ‘B’ and i.author = ‘kim’