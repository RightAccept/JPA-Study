package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired MemberRepository memberRepository;

    // 스프링 데이터 JPA
    @Test
    public void basicTest() throws Exception {
        // given
        Member member = new Member("member1", 10);

        // when
        memberRepository.save(member);
        Member findMember = memberRepository.findById(member.getId()).get();

        List<Member> result1 = memberRepository.findAll();
        List<Member> result2 = memberRepository.findByUsername("member1");

        // then
        assertThat(findMember).isEqualTo(member);

        assertThat(result1).containsExactly(member);
        assertThat(result2).containsExactly(member);
    }

    // 사용자 정의 리포지토리
    @Test
    public void searchTest() throws Exception {
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

        List<MemberTeamDto> result = memberRepository.search(condition);

        // then
        assertThat(result).extracting("username").containsExactly("member4");
    }

    // 스프링 데이터 페이징 활용1 - QueryDSL 페이징 연동
    // 1. 전체 카운트를 한 번에 조회하는 단순한 방법
    @Test
    public void searchPageSimple() throws Exception {
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
//        condition.setAgeGoe(35);
//        condition.setAgeLoe(40);
//        condition.setTeamName("teamB");
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

        // then
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username").containsExactly("member1", "member2", "member3");
    }

    // 인터페이스 지원 - QuerydslPredicateExecutor
    //  - MemberRepository에서 QuerydslPredicateExecutor<Member>를 상속받아서 사용
    // 한계점
    //  - 조인X (묵시적 조인은 가능하지만 left join이 불가능하다)
    //  - 클라이언트가 QueryDSL에 의존해야 한다
    //      - 서비스 클래스가 QeuryDSL이라는 구현 기술에 의존해야 한다
    //  - 복잡한 실무 환경에서 사용하기에는 한계가 명확하다
    // 참고 : QuerydslPredicateExecutor는 Pageable, Sort를 모두 지원하고 정상 동작한다
    @Test
    public void querydslPredicateExecutorTest() {
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

        QMember member = QMember.member;
        Iterable<Member> result = memberRepository.findAll(member.age.between(10, 40).and(member.username.eq("member1")));
        for (Member findMember : result) {
            System.out.println("findMember = " + findMember);
        }
    }
}