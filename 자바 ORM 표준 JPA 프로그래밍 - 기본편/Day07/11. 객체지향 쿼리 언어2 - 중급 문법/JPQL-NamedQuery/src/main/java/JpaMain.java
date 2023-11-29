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

            // Member에서 NamedQuery 설정
            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class).setParameter("username", member.getUsername()).getResultList();
            System.out.println("resultList = " + resultList);

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

// Named 쿼리 - 정적 쿼리
// ※ 미리 정의해서 이름을 정해두고 사용하는 JPQL
// ※ 정적 쿼리
// ※ 어노테이션, XML에 정의
// ※ 애플리케이션 로딩 시점에 초기화 후 재사용
// ※ 애플리케이션 로딩 시점에 쿼리를 검증
//  -> 정적 쿼리는 변하지 않기 때문에 JPA나 하이버네이트가 SQL로 파싱한다
//  -> 사용 중간중간에 파싱하는 것에 비해 로딩 시점에 한 번만 파싱하기 때문에 코스트가 절약된다

// Named 쿼리 - 어노테이션
// ※ 쿼리를 사용할 엔티티에서 선언

// Named 쿼리 - XML에 정의
// ※ persistence.xml에 매핑 후, META-INF에 새로운 XML 파일을 생성하여 그곳에 작성
// ※ XML이 항상 우선권을 가진다
// ※ 애필리케이션 운영 환경에 따라 다른 XML을 배포할 수 있다