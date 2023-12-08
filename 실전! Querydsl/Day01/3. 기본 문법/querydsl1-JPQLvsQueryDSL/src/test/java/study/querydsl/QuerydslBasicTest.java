package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    // JPAQueryFactory를 필드로
    JPAQueryFactory queryFactory;
    // JPAQueryFactory를 필드로 제공하면 동시성 문제
    //  - 동시성 문제는 JPAQueryFactory를 생성할 때 제공하는 EntityManger(em)에 달려있다.
    //  - 스프링 프레임워크는 여러 쓰레드에서 동시에 같은 EntityManger에 접근해도, 트랜잭션 마다 별도의 영속성 컨텍스트를 제공하기 때문에, 동시성 문제는 걱정하지 않아도 된다

    @BeforeEach // 각 테스트 실행 전 데이터 세팅
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
//    JPQL : 문자(실행 시점 오류), QueryDSL : 코드(컴파일 시점 오류)
//    JPQL : 파라미터 바인딩 직접, QueryDSL : 파라미터 바인딩 자동 처리
    @Test
    public void startJPQL() throws Exception {
        // member1을 찾아라
        String qlString = "select m from Member m where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class).setParameter("username", "member1").getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // JPAQueryFactory를 필드로 빼기 전
    @Test
    public void startQuerydsl() throws Exception {
        // member1을 찾아라
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); // EntityManger로 JPAQueryFactory 생성,
        QMember m = new QMember("m");   // 변수에다가 별칭을 줘야 한다

        Member findMember = queryFactory
                .selectFrom(m)
                .from(m)
                .where(m.username.eq("member1"))    // 파라미터 바인딩 처리
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // JPAQueryFactory를 필드로 뺀 후
    @Test
    public void startQuerydsl2() throws Exception {
        // member1을 찾아라
        QMember m = new QMember("m");   // 변수에다가 별칭을 줘야 한다

        Member findMember = queryFactory
                .selectFrom(m)
                .from(m)
                .where(m.username.eq("member1"))    // 파라미터 바인딩 처리
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
}
