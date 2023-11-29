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

            em.flush();
            em.clear();
            
            // 엔티티를 파라미터로 전달
//            List<Member> resultList = em.createQuery("select m from Member m where m = :member", Member.class).setParameter("member", member1).getResultList();
//            System.out.println("resultList = " + resultList);
            
            // 식별자를 직접 전달
            List<Member> resultList = em.createQuery("select m from Member m where m.id = :memberId", Member.class).setParameter("memberId", member1.getId()).getResultList();
            System.out.println("resultList = " + resultList);
            // 둘 다 실행 후 실행되는 SQL문 확인

            // 엔티티 직접 사용 - 외래 키 값
            List<Member> resultList1 = em.createQuery("select m from Member m where m.team = :team", Member.class).setParameter("team", teamA).getResultList();
            System.out.println("resultList1 = " + resultList1);

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

// 엔티티 직접 사용 - 기본 키 값
// ※ JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본 키 값을 사용
//  - JPQL :
//      - 1. select count(m.id) from Member m //엔티티의 아이디를 사용
//      - 2. select count(m) from Member m //엔티티를 직접 사용
//  - SQL(JPAL 둘 다 같은 다음 SQL 실행) : select count(m.id) as cnt from Member m

