import jpabook.*;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            // TypeQuery와 Query
//            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);// 여기서 변수를 만들면 TypeQuery가 나온다(ctrl + alt + v)
//            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class); // 여기서는 값이 String이라고 명확하기 때문에 TypeQuery
//            Query query3 = em.createQuery("select m.username, m.age from Member m");    // 여기는 String과 int가 합쳐져 있어서 Query가 나온다

            // 단일 객체 반환 예시
//            TypedQuery<Member> query = em.createQuery("select m from Member m where m.id = 10L", Member.class);
//
//            Member result = query.getSingleResult();    // 단일 객체 반환
//            System.out.println("result = " + result);
            
            // 파라미터 바인딩 예시
//            TypedQuery<Member> query = em.createQuery("select m from Member m where m.username = :username", Member.class);
//            query.setParameter("username", "member1");
//            Member singleResult = query.getSingleResult();

            Member singleResult = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1").getSingleResult();

            System.out.println("singleResult = " + singleResult);

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

// JPQL - 기본 문법과 성능
// 소개
/*
    ※ JPQL은 객체지향 쿼리 언어
    -> 테이블을 대상으로 쿼리하는 것이 아니라 엔티티 객체를 대상으로 쿼리한다
    ※ JPQL은 SQL을 추상화해서 특정데이터베이스 SQL에 의존하지 않는다
    ※ JPQL은 결국 SQL로 변환된다
 */

// JPQL 문법
/*
    select_문 :: =
        select_절
        from_절
        [where_절]
        [groupby_절]
        [having_절]
        [orderby_절]
    update_문 :: = update_절 [where_절]
    delete_문 :: = delete_절 [where_절]
 */
/*
    ※ select m from Member as m where m.age > 18
    ※ 엔티티와 속성은 대소문자 구분O (Member, age)
    ※ JPQL 키워드는 대소문자 구분X (select, from, where)
    ※ 엔티티 이름 사용, 테이블 이름이 아님(Member)
    ※ 별칭은 필수(m) (as는 생략 가능)
 */

// 집합과 정렬
/*
    select
        COUNT(m), //회원수
        SUM(m.age), //나이 합
        AVG(m.age), //평균 나이
        MAX(m.age), //최대 나이
        MIN(m.age) //최소 나이
    from Member m
 */
/*
    - GROUP BY, HAVING
    - ORDER BY
 */

// TypeQuery, Query
/*
    ※ TypeQuery : 반환 타입이 명확할 때 사용
    - ex) String, int, Member 등등
    ※ Query : 반환 타입이 명화갛지 않을 때 사용
    - ex) String과 int가 같이 나올 경우 등
 */

// 결과 조회 API
/*
    ※ query.getResultList() : 결과가 하나 이상일 때, 리스트 반환
    - 결과가 없으면 빈 리스트 반환
    ※ query.getSingleResult() : 결과가 정확히 하나, 단일 객체 반환
    - 결과가 없으면 : javax.persistence.NoResultException
    - 둘 이상이면 : javax.persistence.NonUniqueResultException
 */
