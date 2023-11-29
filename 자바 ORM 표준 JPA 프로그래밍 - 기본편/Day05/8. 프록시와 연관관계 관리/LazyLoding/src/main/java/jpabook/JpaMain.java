package jpabook;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setName("member1");
            member1.setTeam(team);
            em.persist(member1);

            Member member2 = new Member();
            member2.setName("member2");
            member2.setTeam(teamB);
            em.persist(member2);


            em.flush();
            em.clear();

            // LAZY
            // 이 때는 Member만 가져온다
//            Member member = em.find(Member.class, member1.getId());
//            
//            // Team의 클래스가 Proxy로 설정되어 있다
//            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
//
//            System.out.println("==========================");
//            // 여기서 team의 정보를 select로 불러온다
//            System.out.println("member.getTeam().getName()" + member.getTeam().getName()); // lazy 일 때 초기화
//            System.out.println("==========================");

            // EAGER
            // JPA 구현체는 가능하면 조인을 사용해서 SQL 한번에 함께 조회
            // 이 때 Member와 Team을 모두 가져온다
//            Member member = em.find(Member.class, member1.getId());
            
//            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
//
//            System.out.println("==========================");
//            System.out.println("member.getTeam().getName()" + member.getTeam().getName()); // EAGER일 때는 단순 출력
//            System.out.println("==========================");

            // EAGER에서 N+1 문제 예시
            List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
            // SQL : select * from Member
            // EAGER일 때는 Member를 조회하면 무조건 Team도 가지고 있어야 한다
            // 따라서 추가 쿼리문이 나가게 됨
            // SQL : select * from Team where TEAM_ID = xxx
            // 멤버와 팀이 많아진다면?

            // 실무에서는 LAZY로 바르고, ABC를 한번에 조인할 때 fetch 조인을 사용한다
//            em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();

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

// 프록시와 즉시로딩 주의
// ※ 가급적 지연 로딩만 사용(특히 실무에서)
// ※ 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생
// ※ 즉시 로딩은 JPQL에서 N+1 문제를 일으킨다
// ※ @ManyToOne, @OneToOne은 기본이 즉시 로딩
// -> LAZY로 설정
// ※ @OneToMany, @ManyToMany는 기본이 지연 로딩

// 지연 로딩 활용(지금 배우는 건 이론적인 거고, 실무에서는 다 지연로딩 사용한다고 생각하면 됨)
// - Member와 Team은 자주 함께 사용 -> 즉시 로딩
// - Member와 Order는 가끔 사용 -> 지연 로딩
// - Order와 Product는 자주 함께 사용 -> 즉시 로딩

// 지연 로딩 활용 - 실무
// ※ 모든 연관관계에 지연 로딩을 사용해라!!! ※
// -> 실무에서 즉시 로딩을 사용하지 마라!
// - JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라!
// - 즉시 로딩은 상상하지 못한 쿼리가 나간다