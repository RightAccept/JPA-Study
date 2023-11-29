import jpql.*;

import javax.persistence.*;
import java.util.Collection;
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

            // 상태 필드
//            String query = "select m.username From Member m";

            // 단일 값 연관 경로
//            String query = "select m.team From Member m";
//            String query = "select m.team.name From Member m";  // m.team => 단일 값 연관 경로, m.team.name => 상태 필드
//            List<String> resultList = em.createQuery("select m.team.name From Member m", String.class).getResultList();
//            System.out.println("resultList = " + resultList);

            // 단일 값 연관 경로 묵시적 내부 조인
//            List<Team> resultList1 = em.createQuery(query, Team.class).getResultList();
            // Team으로 받아온다고 적어 뒀지만, 출력되는 쿼리를 보면 Member를 기준으로 join으로 team을 가져온다
            // 이를 묵시적 내부 조인이라고 한다

            // 컬렉션 값 연관 경로
//            String query = "select t.members From Team t";  // 묵시적 내부 조인 발생, members는 1:N 관계, 뭘 선택해서 어떤걸 꺼내야 할지 난감 => 탐색 불가
//            List<Collection> resultList = em.createQuery(query, Collection.class).getResultList();
//            System.out.println("resultList = " + resultList);

//            Integer singleResult = em.createQuery("select t.members.size From Team t", Integer.class).getSingleResult();    // size 정도는 사용 가능하다
//            System.out.println("singleResult = " + singleResult);

            // 컬렉션에서 값을 가져오고 싶으면 명시적 조인을 해야한다
            em.createQuery("select m From Team t join t.members m", Member.class).getResultList();
            // -> 명시적 조인을 하면 컬렉션도 별칭을 얻기 때문에 추가 탐색이 가능하다
            
            // 영한 왈 : 실무에서 묵시적 조인 쓰지말고 명시적 조인을 사용해라


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            System.err.println("예외 발생 : " + e);
        } finally {
            em.close();
        }
        emf.close();
    }
}

// 경로 표현식
// .(점)을 찍어 객체 그래프를 탐색하는 것
/*
    select m.username -> 상태 필드
        from Member m
         join m.team t -> 단일 값 연관 필드
         join m.orders o -> 컬렉션 값 연관 필드
     where t.name = '팀A'
 */

// 경로 표현식 용어 정리
// ※ 상태 필드(state field)
//  - 단순히 값을 저장하기 위한 필드
//  - ex) m.username
// ※ 연관 필드(association field) : 연관 관계를 위한 필드
//  - 단일 값 연관 필드
//      -   @ManyToOne, @OneToOne, 대상이 엔티티( Ex) m.team)
//  - 컬렉션 값 연관 필드
//      - @OneToMany, @ManyToMany, 대상이 컬렉션 ( Ex) m.orders)

// 경로 표현식의 특징
// ※ 상태 필드(sate field) : 경로 탐색의 끝, 탐색 X
// ※ 단일 값 연관 경로 : 묵시적 내부 조인(inner join) 발생, 탐색 O
// ※ 컬렉션 값 연관 경로 : 묵시적 내부조인 발생, 탐색 X
//  - FROM 절에서 명식적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능

// 상대 필드 경로 탐색
// ※ JPQL: select m.username, m.age from Member m
// ※ SQL: select m.username, m.age from Member m

// 단일 값 연관 경로 탐색
// ※ JPQL: select o.member from Order o
// ※ SQL: select m.* from Orders o inner join Member m on o.member_id = m.id

// 명시적 조인, 묵시적 조인
// ※ 명시적 조인 : join 키워드 직접 사용
//  - select m from Member m join m.team t
// ※ 묵시적 조인 : 경로 표현식에 의해 묵시적으로 SQL 조인 발생(내부 조인만 가능)
//  - select m.team from Member m

// 경로 탐색을 사용한 묵시적 조인 시 주의 사항
// ※ 항상 내부 조인
// ※ 컬렉션은 경로 탐색의 끝, 명시적 조인을 통해 별칭을 얻어야 함
// ※ 경로 탐색은 주로 SELECT, WHERE 절에서 사용하지만 묵시적 조인으로 인해 SQL의 FROM(JOIN) 절에 영향을 줌

// 실무 조언
// ※ 가급적 묵시적 조인 대신에 명시적 조인 사용
// ※ 조인은 SQL 튜닝에 중요 포인트
// ※ 묵시적 조인은 조인이 일어나는 상황을 한눈에 파악하기 어려움