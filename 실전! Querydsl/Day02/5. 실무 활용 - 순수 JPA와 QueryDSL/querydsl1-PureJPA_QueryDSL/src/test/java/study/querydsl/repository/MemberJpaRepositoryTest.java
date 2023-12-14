package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

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
}