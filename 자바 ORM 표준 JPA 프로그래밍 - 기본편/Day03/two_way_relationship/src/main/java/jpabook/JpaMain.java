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
            // 팀 저장
//            Team team = new Team();
//            team.setName("TeamA");
//            em.persist(team);

            // 회원 저장
//            Member member = new Member();
//            member.setName("member1");
//            member.setTeamId(team.getId());
//            member.setTeam(team);
//            em.persist(member);

            // 조회
//            Member findMember = em.find(Member.class, member.getId());

            // 연관관계가 없음
//            Long findTeamId = findMember.getTeamId();
//            Team findTeam = em.find(Team.class, team.getId());

            // 연관관계가 있음 (ManyToOne)
//            Team findTeam = findMember.getTeam();
//            System.out.println("findTeam.getName() = " + findTeam.getName());

            // 수정
//            Team newTeam = em.find(Team.class, 100L);   // 새로운 팀을 select로 가져온 다음
//            findMember.setTeam(newTeam);    // DB에서 가져온 Member의 팀을 새로운 팀으로 변경해준다

            // 연관관계가 있음 (OneToMany)
//            List<Member> members = findMember.getTeam().getMembers();
//            for (Member m : members) {
//                System.out.println("m.getName() = " + m.getName());
//            }

            // 양방향 매핑시 가장 많이 하는 실수(연관관계의 주인에 값을 입력하지 않음)
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