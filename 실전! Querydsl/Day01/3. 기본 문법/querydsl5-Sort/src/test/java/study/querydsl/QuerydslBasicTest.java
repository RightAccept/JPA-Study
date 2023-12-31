package study.querydsl;

import com.querydsl.core.QueryResults;
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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static study.querydsl.entity.QMember.*;

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
    
    // Q클래스 사용 기본
    @Test
    public void startQuerydsl3() throws Exception {
        // Q클래스 인스턴스를 사용하는 2가지 방법
        // 별칭 직접 지정, 같은 테이블을 조인해야 하는 경우에만 사용
//        QMember qMember = new QMember("m");
        // 기본 인스턴스 사용
//        QMember qMember = QMember.member;

        // 기본 인스턴스를 static import와 함께 사용
        Member findMember = queryFactory.selectFrom(member).from(member).where(member.username.eq("member1")).fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // 검색 조건 쿼리
    @Test
    public void search() throws Exception {
        Member findMember = queryFactory.selectFrom(member).where(member.username.eq("member1").and(member.age.eq(10))).fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    // JPQL이 제공하는 모든 검색 조건 제공
        /*
        member.username.eq("member1") // username = 'member1'
        member.username.ne("member1") //username != 'member1'
        member.username.eq("member1").not() // username != 'member1'

        member.username.isNotNull() //이름이 is not null

        member.age.in(10, 20) // age in (10,20)
        member.age.notIn(10, 20) // age not in (10, 20)
        member.age.between(10,30) //between 10, 30

        member.age.goe(30) // age >= 30
        member.age.gt(30) // age > 30
        member.age.loe(30) // age <= 30
        member.age.lt(30) // age < 30

        member.username.like("member%") //like 검색
        member.username.contains("member") // like ‘%member%’ 검색
        member.username.startsWith("member") //like ‘member%’ 검색
        */
    }
    
    // AND 조건을 파라미터로 처리
    @Test
    public void searchAndParam() throws Exception {
        Member findMember = queryFactory.selectFrom(member).where(member.username.eq("member1"), member.age.eq(10)).fetchOne();
        // where()에 파라미터로 검색 조건을 추가하면 AND 조건이 추가됨
        // 이 경우 null 값은 무시 -> 메서드 추출을 활용해서 동적 쿼리를 깔끔하게 만들 수 있다
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // 결과 조회
    // fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
    // fetchOne() : 단 건 조회
    //  - 결과가 없으면 null
    //  - 결과가 둘 이상이면 com.querydsl.core.NonUniqueResultException
    // fetchFirst() : limit(1).fetchOne()
    // fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행
    // fetchCount() : count 쿼리로 변경 해서 count 수 조회
    @Test
    public void resultFetch() throws Exception {
        // List
        List<Member> fetch = queryFactory.selectFrom(member).fetch();

        // 단 건
        Member fetchOne = queryFactory.selectFrom(member).fetchOne();

        // 처음 한 건 조회
        Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
        
        // 페이징에서 사용
        QueryResults<Member> results = queryFactory.selectFrom(member).fetchResults();
        results.getTotal();
        List<Member> content = results.getResults();

        // count 쿼리로 변경
        long count = queryFactory.selectFrom(member).fetchCount();
    }

    // 정렬
    /*
        회원 정렬 순서
        1. 회원 나이 내림차순(desc)
        2. 회원 이름 올림차순(asc(
            단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort() throws Exception {
        // given
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        // when
        List<Member> result = queryFactory.selectFrom(member).where(member.age.eq(100)).orderBy(member.age.desc(), member.username.asc().nullsLast()).fetch();

        // then
        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();

    }
}
