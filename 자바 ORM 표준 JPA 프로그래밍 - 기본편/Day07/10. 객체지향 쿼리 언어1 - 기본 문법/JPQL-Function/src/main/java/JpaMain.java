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

            // CONCAT
//            String query = "select concat('a', 'b') From Member m";
//            String query = "select 'a' || 'b' From Member m";   // CONCAT 대신 ||로도 사용할 수 있다
            
            // SUBSTRING
//            String query = "select SUBSTRING(m.username, 2, 3) From Member m";

            // LOCATION
//            String query = "select locate('de', 'abcdegf') From Member m";  // 얘는 사용할 때 String.calss가 아니라 Integer.class로 넣어야 한다

            // Index
//            String query = "select index(t.members) from Team t";
            
            // 사용자 정의 함수 호출
//            String query = "select function('group_concat', m.username) From Member m";
            String query = "select group_concat(m.username) From Member m";
            // 결과가 여러 줄로 나올 때, 한 줄로 만들어 주는 함수
            // ex) s = member1, s = member2 등으로 나올 때 s = member1, member2 로 만들어 줌
            
            
            // 실행 구문
            List<String> resultList = em.createQuery(query, String.class).getResultList();

            for (String s : resultList) {
                System.out.println("s = " + s); 
            }

//            List<Integer> resultList = em.createQuery(query, Integer.class).getResultList();



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

// JPQL 기본 함수
/*
    ※ CONCAT : 문자열 붙이기
    ※ SUBSTRING : 문자열 추출
    ※ TRIM
    ※ LOWER, UPPER
    ※ LENGTH : 문자의 길이
    ※ LOCATE : 찾는 문자가 찾는 문자열의 몇 번째에 위치했는지 인덱스 반환
    ※ ABS, SQRT, MOD
    ※ SIZE(JPA) : 컬렉션의 크기를 반환
    ※ INDEX(JPA 용도) : 컬렉션의 위치 값을 구할 때 사용
 */

// 사용자 정의 함수 호출
// ※ 하이버네이트는 사용전 방언에 추가해야 한다
//  - 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록한다
//      - select function('froup_concat', i.name) from Item i