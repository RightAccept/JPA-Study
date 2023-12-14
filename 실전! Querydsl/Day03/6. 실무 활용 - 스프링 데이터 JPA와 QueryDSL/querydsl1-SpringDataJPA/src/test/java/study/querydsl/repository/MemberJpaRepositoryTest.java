package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired MemberJpaRepository memberJpaRepository;

    // 순수 JPA 리포지토리와 QueryDSL
    @Test
    public void basicTest() throws Exception {
        // given
        Member member = new Member("member1", 10);

        // when
        memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.findById(member.getId()).get();

        List<Member> JPA_result1 = memberJpaRepository.findAll();
        List<Member> JPA_result2 = memberJpaRepository.findByUsername("member1");

        List<Member> querydsl_result1 = memberJpaRepository.findAll_Querydsl();
        List<Member> querydsl_result2 = memberJpaRepository.findByUsername_Querydsl("member1");

        // then
        assertThat(findMember).isEqualTo(member);

        assertThat(JPA_result1).containsExactly(member);
        assertThat(JPA_result2).containsExactly(member);

        assertThat(querydsl_result1).containsExactly(member);
        assertThat(querydsl_result2).containsExactly(member);
    }

    // 동적 쿼리와 성능 최적화 조회 - Builder 사용
    @Test
    public void searchTest_Builder() throws Exception {
        // given
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

        // when
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);

        // then
        assertThat(result).extracting("username").containsExactly("member4");
    }

    // 동적 쿼리와 성능 최적화 조회 - Where절 파라미터 사용
    @Test
    public void searchTest_WhereParameter() throws Exception {
        // given
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

        // when
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.searchByWhereParameter(condition);

        // then
        assertThat(result).extracting("username").containsExactly("member4");
    }
}