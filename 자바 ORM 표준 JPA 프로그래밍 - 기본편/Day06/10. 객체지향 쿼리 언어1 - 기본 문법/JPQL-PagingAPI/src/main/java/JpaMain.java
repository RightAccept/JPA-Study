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

            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(11) // 몇 번째부터
                    .setMaxResults(10)  // 몇 개를 가져올건지
                    .getResultList();

            System.out.println("resultList.size() = " + resultList.size());
            for (Member member1 : resultList) {
                System.out.print("member1.getUsername() = " + member1.getUsername() + ", ");
                System.out.println("member1.getAge() = " + member1.getAge());
            }

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

// 페이징 API
// ※ JPA는 페이징을 다음 두 API로 추상화
// - setFirstResult(int startPosition) : 조회 시작 위치(0부터 시작)
// - setMaxResults(int maxResult) : 조회할 데이터 수
