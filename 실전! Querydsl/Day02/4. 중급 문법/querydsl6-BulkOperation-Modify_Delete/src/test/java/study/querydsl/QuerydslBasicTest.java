package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;
import static org.assertj.core.api.Assertions.*;

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
    
    // 페이징
    @Test
    public void paging1() throws Exception {
        List<Member> result = queryFactory.selectFrom(member).orderBy(member.username.desc()).offset(1).limit(2).fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    // 전체 조회수가 필요하면
    @Test
    public void paging2() throws Exception {
        QueryResults<Member> results = queryFactory.selectFrom(member).orderBy(member.username.desc()).offset(1).limit(2).fetchResults();

        assertThat(results.getTotal()).isEqualTo(4);
        assertThat(results.getLimit()).isEqualTo(2);
        assertThat(results.getOffset()).isEqualTo(1);
        assertThat(results.getResults().size()).isEqualTo(2);
        
        // 주의 : count 쿼리가 실행되니 성능상 주의
        // 참고 : 실무에서 페이징 쿼리를 작성할 때, 데이터를 조회하는 쿼리는 여러 테이블을 조인해야 하지만,
        //  count 쿼리는 조인이 필요 없는 경우도 있다
        //  - 그런데 이렇게 자동화된 count 쿼리는 원본 쿼리와 같이 모두 조인을 해버리기 때문에 성능이 안나올 수 있다
        //  - count 쿼리에 조인이 필요없는 성능 최적화가 필요하면, count 전용 쿼리를 별도로 작성해야 한다
    }

    // 집합 함수
    /**
     * JPQL
     * select
     * COUNT(m), //회원수
     * SUM(m.age), //나이 합
     * AVG(m.age), //평균 나이
     * MAX(m.age), //최대 나이
     * MIN(m.age) //최소 나이
     * from Member m
     */
    @Test
    public void aggregation() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }
    
    // GroupBy 사용
    // 팀의 이름과 각 팀의 평균 연령을 구해라
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    // 조인 - 기본 조인
    // 조인의 기본 문법은 첫 번째 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭(alias)으로 사용할 Q 타입을 지정하면 된다
    // join(조인 대상, 별칭으로 사용할 Q타입)
    @Test
    public void join() throws Exception {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result).extracting("username").containsExactly("member1", "member2");

        // join(), innerJoin() : 내부 조인(inner join)
        // leftJoin() : left 외부 조인(left outer join)
        // rightJoin() : right 외부 조인(right outer join)
        // JPQL의 on과 성능 최적화를 위한 fetch 조인 제공
    }
    
    // 세타 조인 : 연관 관계가 없는 필드로 조인
    // 회원의 이름이 팀 이름과 같은 회원 조회
    @Test
    public void theta_join() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result).extracting("username").containsExactly("member1", "member2");

        // from 절에 여러 엔티티를 선택해서 세타 조인
        // 외부 조인 불가능
    }

    // 조인 - on절
    // ON절을 활용한 조인(JPA 2.1부터 지원)
    //  - 조인 대상 필터링
    //  - 연관관계 없는 엔티티 외부 조인

    // 1. 조인 대상 필터링
    // ex) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
    // JPQL : select m, t from Member m left join m.team t on t.name = 'teamA'
    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
//                .leftJoin(member.team, team).on(team.name.eq("teamA"))
//                .leftJoin(member.team, team).on(team.name.eq("teamA")).where(team.name.eq("teamA"))
                .join(member.team, team).where(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
        
        // 참고
        // on 절을 활용해 조인 대상을 필터링 할 때, 외부조인이 아니라 내부조인을 사용하면, where 절에서 필터링 하는 것과 기능이 동일하다
        //  - 따라서 on 절을 활용한 조인 대상 필터링을 사용할 때, 내부조인이면 익숙한 where절로 해결하고, 정말 외부조인이 필요한 경우에만 이 기능을 사용하자
    }

    // 2. 연관관계 없는 엔티티 외부 조인
    // ex) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
    // JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

        // 하이버네이트 5.1부터 on을 사용해서 서로 관계가 없는 필드로 외부 조인하는 기능이 추가되었다
        //  - 물론 내부 조인도 가능하다
        // 주의! 문법을 잘 봐야 한다.
        //  - leftJoin() 부분에 일반 조인과 다르게 엔티티 하나만 들어간다
        //      - 일반 조인 : leftJoin(member.team, team)
        //      - on조인 : from(member).leftJoin(team).on(xxx)
    }
    
    // 조인 - 페치 조인
    // 페치 조인은 SQL에서 제공하는 기능은 아니다
    // SQL 조인을 활용해서 연관된 엔티티를 SQL 한 번에 조회하는 기능이다
    // 주로 성능 최적화에 사용하는 방법
    
    // 페치 조인 미적용
    // 지연 로딩으로 Member, TEam SQL 쿼리 각각 실행
    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();    // Memberdml team은 LAZY 이기 때문에 team은 불러와지지 않음

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());   // Team이 불러와 졌는지 확인
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    // 페치 조인 적용
    // 즉시 로딩으로 Member, Team SQL 쿼리 조인으로 한 번에 조회
    // 사용 방법 : join(), leftJoin() 등 조인 기능 뒤에 fetchJoin() 이라고 추가하면 된다
    @Test
    public void fetchJoin() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());   // Team이 불러와 졌는지 확인
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }
    
    // 서브 쿼리
    // com.querydsl.jpa.JPAExpressions 사용
    //  - JPAExpressions는 static import 할 수 있다
    // 1. 서브 쿼리 eq 사용
    // 나이가 가장 많은 회원 조회
    @Test
    public void subQuery1() throws Exception {
        QMember memberSub = new QMember("memberSub");
        // 서브쿼리를 사용할 때, 외부와 겹치면 안되기 때문에 선언

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions.select(memberSub.age.max()).from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(40);
    }

    // 2. 서브 쿼리 goe(greter or equal) 사용
    // 나이가 평균 나이 이상인 회원
    @Test
    public void subQuery2() throws Exception {
        QMember memberSub = new QMember("memberSub");
        // 서브쿼리를 사용할 때, 외부와 겹치면 안되기 때문에 선언

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions.select(memberSub.age.avg()).from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(30, 40);
    }

    // 3. 서브쿼리 여러 건 처리, in 사용
    @Test
    public void subQuery3() throws Exception {
        QMember memberSub = new QMember("memberSub");
        // 서브쿼리를 사용할 때, 외부와 겹치면 안되기 때문에 선언

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions.select(memberSub.age).from(memberSub).where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(20, 30, 40);
    }
    
    // 4. select 절에 서브 쿼리
    @Test
    public void selectSubQuery() throws Exception {
        QMember memberSub = new QMember("memberSub");
        // 서브쿼리를 사용할 때, 외부와 겹치면 안되기 때문에 선언

        List<Tuple> result = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ).from(member).fetch();

        for (Tuple tuple : result) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " + tuple.get(JPAExpressions.select(memberSub.age.avg()).from(memberSub)));
        }
    }
    // ※ from 절의 서브쿼리 한계
    //  - JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다
    //  - 당연히 QueryDSL도 지원하지 않는다
    //  - 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다
    //  - QueryDSL도 하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다
    // ※ from 절의 서브쿼리 해결방안
    //  1. 서브쿼리를 join으로 변경한다(가능한 상황도 있고, 불가능한 상황도 있다)
    //  2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다
    //  3. nativeSQL을 사용한다

    // Case 문
    // select, 조건절(wehre), order by에서 사용 가능

    // 단순한 조건
    @Test
    public void basicCase() throws Exception {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    
    // 복잡한 조건
    @Test
    public void complexCase() throws Exception {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // 상수, 문자 더하기
    // 상수가 필요하면 Expressions.constant(xxx) 사용
    
    // 상수
    // 최적화가 가능하면 SQL에 constant 값을 넘기지 않는다.
    // 상수를 더하는 것 처럼 최적화가 어려우면 SQL에 constant 값을 넘긴다
    @Test
    public void constant() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    
    // 문자
    @Test
    public void concat() throws Exception {
        // {username}_{age}
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
        // member.age.stringValue() 부분이 중요한데, 문자가 아닌 다른 타입들은 stringValue()로 문자로 변환할 수 있다
        //  - 이 방법은 ENUM을 처리할 때도 자주 사용한다
    }
    
    // 프로젝션과 결과 반환 - 기본
    // 프로젝션 : select 대상 지정
    
    // ※ 프로젝션 대상이 하나
    //  - 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
    //  - 프로젝션 대상이 둘 이상이면 튜플이나 DTO로 조회
    @Test
    public void simpleProjection() throws Exception {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
        // member.username이 아닌 member를 그대로 넣어도 프로젝션 대상이 하나인 것
        //  - List<Member>도 프로젝션 대상이 하나라는 말
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // ※ 프로젝션 대상이 둘 이상
    // 튜플 또는 DTO 사용
    //  - 튜플 : QueryDSL이 여러 개를 조회할 때를 대비해서 만들어놓은 타입
    @Test
    public void tupleProjection() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
        // username(String)과 age(Int)로 결과 값이 두 개이기 때문에 tuple 혹은 직접 생성한 DTO를 사용

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
        // 튜플을 리포지토리에서 사용하는 건 괜찮지만, 서비스나 콘트롤러까지 넘어가는 것은 좋은 설계가 아니다
        //  - 튜플은 querydsl 패키지 내부에 존재하기 때문에, querydsl에 종속적이다.
        //  - 외부로 전달해야 할 때는 DTO로 변환해서 전달
        
    }

    // 프로젝션과 결과 반환 - DTO 조회
    // 1. 순수 JPA에서 DTO 조회
    @Test
    public void findDtoByJPQL() {
        List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class).getResultList();
        // 순수 JPA에서는 DTO를 조회할 때는 new 명령어를 사용해야 함
        // DTO의 package 이름을 다 적어줘야 해서 지저분함
        // 생성자 방식만 지원함
    }

    // 2. QueyryDSL 빈 생성(Bean Population)
    // 결과를 DTO 반환할 때 사용
    // 다음 3가지 방법 지원
    //  - 프로퍼티 접근
    //  - 필드 직접 접근
    //  - 생성자 사용

    // a. 프로퍼티 접근
    //  - setter가 필요함
    @Test
    public void findDtoBySetter() {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class, member.username, member.age))
                .from(member).fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // b. 필드 직접 접근
    //  - setter 없이 바로 필드에 값을 넣는다
    @Test
    public void findDtoByField() {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // 추가 : 별칭이 다를 때
    // as를 사용하여 필드 이름을 바꿔준다
    @Test
    public void findUserDtoByField() {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub), "age")
                        ))
                .from(member)
                .fetch();
        for (UserDto userDto : result) {
            System.out.println("memberDto = " + userDto);
        }
        // 프로퍼티나, 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
        // ExpressionUtils.as(source, alias) : 필드나 서브 쿼리에 별칭 적용
        // username.as("name") : 필드에 별칭 적용
    }

    // c. 생성자 접근 방법
    //  - 생성자의 타입을 보고 값을 넣는다
    @Test
    public void findDtoByConstructor() {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
        List<UserDto> UserResult = queryFactory
                .select(Projections.constructor(UserDto.class, member.username, member.age))
                .from(member)
                .fetch();
        for (UserDto userDto : UserResult) {
            System.out.println("userDto = " + userDto);
        }
    }

    // 프로젝션과 결과 반환 - @QueryProjection
    // 생성자 + @QueryProjection
    // 1. MemberDto의 생성자에 @QueryProjection을 달아준다
    // 2. gradle(코끼리)에서 compileQuerydsl(Tasks -> other -> compileQuerydsl)
    //  - Dto가 Q 파일로 생성 -> QMemberDto가 생성된다
    @Test
    public void findDtoByQueryProjection() throws Exception {
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();
        // 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법
        //  - 다만, DTO에 QueryDSL 어노테이션을 유지해야 하는 점과 DTO까지 Q 파일을 생성해야 하는 단점이 있다
        //      - DTO가 QueryDSL을 의존한다
    }

    // 동적 쿼리 - BooleanBuilder
    @Test
    public void BooleanBuilder() throws Exception {
        // 검색 조건
        String usernameParam = "member1";
        Integer ageParam = 10;

        // when
        List<Member> result = searchMember1(usernameParam, ageParam);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        // usernameCond가 null이면, age로만 검색하고, ageCond가 null이면 username으로만 검색
        // 둘 다 null이면 where문이 안들어 가고, 둘 다 있으면 둘 다 들어감
        BooleanBuilder builder = new BooleanBuilder();
        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }
    
    // 동적 쿼리 - Where 다중 파라미터 사용
    @Test
    public void WhereParam() throws Exception {
        // 검색 조건
        String usernameParam = "member1";
        Integer ageParam = 10;

        // when
        List<Member> result = searchmember2(usernameParam, ageParam);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchmember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameCond), ageEq(ageCond))    // where 조건에 null 값은 무시된다
                .fetch();
        // 메서드를 다른 쿼리에서도 재활용 할 수 있다
        // 쿼리 자체의 가독성이 높아진다
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    // BooleanExpression을 사용하여 조건문 조합
    //  - 단, 조합하려면 조합에 사용되는 메서드의 반환값이 Predicate가 아닌, BooleanExpression 이어야 한다
    //  - null 체크는 주의해서 처리햐아 함
    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

    // 수정, 삭제 벌크 연산
    //  - 쿼리 한 번으로 대량의 데이터를 바꿀 때 사용
    //  - 주의 : JPQL 배치와 마찬가지로,
    //  영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에
    //  배치 쿼리를 실행하고 나면 영속성 컨텍스트를 초기화 하는 것이 안전하다

    // 1. 쿼리 한 번으로 대량 데이터 수정
    @Test
    public void bulkUpdate() throws Exception {
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();
    }

    // 2. 기존 숫자에 1 더하기
    @Test
    public void bulkAdd() throws Exception {
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .execute();
    }

    // 3. 곱하기 : multiply(x)
    @Test
    public void bulkMultiply() throws Exception {
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.multiply(2))
                .execute();
    }

    // 3. 쿼리 한 번으로 대량 데이터 삭제
    @Test
    public void bulkDelete() throws Exception {
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }
}
