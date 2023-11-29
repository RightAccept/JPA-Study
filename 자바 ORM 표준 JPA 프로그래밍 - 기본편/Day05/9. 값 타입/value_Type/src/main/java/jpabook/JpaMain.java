package jpabook;

import jpabook.Child;
import jpabook.Member;
import jpabook.Parent;
import jpabook.Team;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);
            // 1번
            em.persist(child1);
            em.persist(child2);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            // 2번
//            findParent.getChildList().remove(0);
            // 컬렉션에서 제거된 엔티티를 DB에서 제거한다
            
            // 3번
            em.remove(findParent);
//            findParent.getChildList().remove(0);

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

    private static void printMember(Member findMember) {
        System.out.println("findMember = " + findMember.getName());
    }

    private static void printMemberAndTeam(Member findMember) {
        String username = findMember.getName();
        System.out.println("username = " + username);

        Team team = findMember.getTeam();
        System.out.println("team = " + team.getName());
    }
}
