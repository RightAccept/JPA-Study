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

//            String query = "select m from Member m";
//            List<Member> resultList = em.createQuery(query, Member.class).getResultList();
//
//            for (Member member : resultList) {
//                System.out.println("member = " + member.getUsername() + "," + member.getTeam().getName());
//                // 회원1, 팀A(SQL)
//                // 회원2, 팀A(1차 캐시)
//                // 회원3, 팀B(SQL)
//
//                // 회원 100명 -> N + 1
//                // -> 해결법은 fetch join 말고는 없다
//            }

            // fetch join
//            List<Member> resultList = em.createQuery("select m From Member m join fetch m.team", Member.class).getResultList();

//            for (Member member : resultList) {
//                System.out.println("member = " + member.getUsername() + "," + member.getTeam().getName());
//                // Member와 Team의 모든 데이터를 SQL 한 번으로 모두 불러와서 영속성 컨텍스트에 저장
//            }

            // 컬렉션 페치 조인
//            List<Team> resultList1 = em.createQuery("select t From Team t join fetch t.members", Team.class).getResultList();
//            for (Team team : resultList1) {
//                System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
//                // team = teamA | members = 2
//                // team = teamA | members = 2
//                // team = teamB | members = 1
//                // 출력
//
//                // 같은 구문이 2번 출력됐다
//                // -> teamA는 하나이지만, member가 2명이기 때문에 join하면서 회원의 숫자만큼 가져온 것
//
//                for (Member member : team.getMembers()) {
//                    System.out.println("-> member = " + member);
//                }
//            }
            
            // 컬렉션 페치 조인 중복 제거(DISTINCT)
//            List<Team> resultList1 = em.createQuery("select distinct t From Team t join fetch t.members", Team.class).getResultList();
//            System.out.println("resultList1 = " + resultList1.size());
            // SQL에 DISTINCT를 추가하지만 데이터가 다르므로 SQL 결과에서 중복제거 실패
            // -> TEAM_ID는 똑같아도, MEMBER_ID와 USERNAME이 다르기 때문에 중복 제거가 되지 않는다
            // -> 하지만 JPA에서 중복 엔티티를 제거하기 때문에 해당 구문 실행 시 중복된 상태로 나온다
//            for (Team team : resultList1) {
//                System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
//                for (Member member : team.getMembers()) {
//                    System.out.println("-> member = " + member);
//                }
//            }

            // 페치 조인과 일반 조인의 차이
            List<Team> normalJoin = em.createQuery("select t From Team t join t.members m", Team.class).getResultList();
            System.out.println("normalJoin.size() = " + normalJoin.size());
            for (Team team : normalJoin) {
                System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member.getUsername());
                }
            }
            // 팀 엔티티만 조회하고 멤버 엔티티는 조회하지 않는다
            System.out.println("========================");
            em.clear();

            List<Team> fetchJoin = em.createQuery("select t From Team t join fetch t.members m", Team.class).getResultList();
            System.out.println("fetchJoin = " + fetchJoin.size());
            for (Team team : fetchJoin) {
                System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member.getUsername());
                }
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

// 페치 조인(fetch join)
// ※ SQL 조인 종류 X
// ※ JPQL에서 성능 최적화를 위해 제공하는 기능
// ※ 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
// ※ join fetch 명령어 사용

// 엔티티 페치 조인
// ※ 회원을 조회하면서 연관된 팀도 함께 조회(SQL 한 번에)
// ※ SQL을 보면 회원 뿐만 아니라 팀(T.*)도 함께 SELECT
// ※ JPQL
//  - select m from Member m join fetch m.team
// ※ SQL
//  - SELECT M.*, T.* FROM MEMBER M INNER JOIN TEAM T ON M.TEAM_ID=T.ID

// 페치 조인과 DISTINCT
// ※ SQL의 DISTINCT는 중복된 결과를 제거하는 명렁
// ※ JPQL의 DISTINCT는 2가지 기능 제공
//  - 1. SQL에 DISTINCT를 추가
//  - 2. 애플리케이션에서 엔티티 중복 제거

// 페치 조인과 일반 조인의 차이
// ※ 일반 조인 실행 시 연관된 엔티티를 함께 조회하지 않음
// ※ JPQL : select t from Team t join t.members m where t.name = ‘팀A'
// ※ SQL :SELECT T.* FROM TEAM T INNER JOIN MEMBER M ON T.ID=M.TEAM_ID WHERE T.NAME = '팀A'

// ※ JPQL은 결과를 반환할 때 연관관계 고려 X
// ※ 단지 SELECT 절에 지정한 엔티티만 조회할 뿐
//  - 여기서는 팀 엔티티만 조회하고, 회원 엔티티는 조회 X

// ※ 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩)
// ※ 페치 조인은 객체 그래프를 SQL 한 번에 조회하는 개념