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
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setName("member1");

            // 역방향(주인이 아닌 방향)만 연관관계 설정
            team.getMembers().add(member);

            // 순수한 객체 관계를 고려하면 항상 양쪽 다 값을 입력해야 한다
            member.setTeam(team);

            em.persist(member);

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