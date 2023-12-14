package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;

import javax.persistence.EntityManager;
import java.util.List;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        QMember.member.id,
                        QMember.member.username,
                        QMember.member.age,
                        QTeam.team.id,
                        QTeam.team.name))
                .from(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username) ? QMember.member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName) ? QTeam.team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? QMember.member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? QMember.member.age.loe(ageLoe) : null;
    }

    // 스프링 데이터 페이징 활용1 - QueryDSL 페이징 연동
    // 1. 전체 카운트를 한 번에 조회하는 단순한 방법
    //  - fetchResults() 사용
    // ※ QueryDSL이 제공하는 fetchResults()를 사용하면 내용과 전체 카운트를 한 번에 조회할 수 있다(실제 쿼리는 2번 호출)
    // ※ fetchResult()는 카운트 쿼리 실행 시 필요없는 order by는 제거한다
    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
                .select(new QMemberTeamDto(
                        QMember.member.id,
                        QMember.member.username,
                        QMember.member.age,
                        QTeam.team.id,
                        QTeam.team.name))
                .from(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
    
    // 2. 데이터 내용과 전체 카운트를 별도로 조회하는 방법
    //  - 복잡한 페이징
    //  - 데이터 조회 쿼리와, 전체 카운트 쿼리를 분리
    // ※ 전체 카운트를 조회 하는 방법을 최적화 할 수 있으면 이렇게 분리하면 된다.
    //  - 예를 들어서 전체 카운트를 조회할 때 조인 쿼리를 줄일 수 있다면 상당한 효과가 있다
    // ※ 코드를 리펙토링해서 내용 쿼리와 전체 카운트 쿼리를 읽기 좋게 분리하면 좋다
//    @Override
//    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
//        List<MemberTeamDto> content = queryFactory
//                .select(new QMemberTeamDto(
//                        QMember.member.id,
//                        QMember.member.username,
//                        QMember.member.age,
//                        QTeam.team.id,
//                        QTeam.team.name))
//                .from(QMember.member)
//                .leftJoin(QMember.member.team, QTeam.team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long total = queryFactory
//                .selectFrom(QMember.member)
//                .from(QMember.member)
//                .leftJoin(QMember.member.team, QTeam.team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .fetchCount();
//
//        return new PageImpl<>(content, pageable, total);
//    }
    
    // 스프링 데이터 페이징 활용2 - CountQuery 최적화
    // PageableExecutionUtils.getPage() 사용
    // ※ 스프링 데이터 라이브러리가 제공
    // ※ count 쿼리 생략 가능한 경우 생략해서 처리
    //  - 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
    //  - 마지막 페이지 일 때(offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함)
    //      - 더 정확히는 마지막 페이지이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        QMember.member.id,
                        QMember.member.username,
                        QMember.member.age,
                        QTeam.team.id,
                        QTeam.team.name))
                .from(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

//        JPAQuery<Member> countQuery = queryFactory
//                .selectFrom(QMember.member)
//                .from(QMember.member)
//                .leftJoin(QMember.member.team, QTeam.team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                );

//        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchCount());
//        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);

        // 추가 : fetchCount()와 fetchResult()는 select 쿼리를 단순히 count 처리하는 용도로 바꾸는 정도
        //  - 따라서 단순한 쿼리에서는 잘 동작하지만, 복잡한 쿼리에서는 제대로 동작하지 않는다
        //  - 향후 fetchCount()와 fetchResult()를 지원하지 않기로 결정
        //  그에 따라 실행되는 메서드도 변경되어야 한다
        JPAQuery<Long> countQuery = queryFactory
                .select(QMember.member.count())
                .from(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 아래 쿼리는 count를 구하는 예제
    public Long count() {
        return queryFactory
//                .select(Wildcard.count) // select count(*)    // count(*)을 사용하고 싶으면 Wildcard.count를 사용
                .select(QMember.member.count()) // select count(member.id)  // member.count()를 사용하면 count(member.id)로 처리
                .from(QMember.member)
                .fetchOne();    // 응답 결과는 숫자 하나이므로 fetchOne()을 사용
    }
}
