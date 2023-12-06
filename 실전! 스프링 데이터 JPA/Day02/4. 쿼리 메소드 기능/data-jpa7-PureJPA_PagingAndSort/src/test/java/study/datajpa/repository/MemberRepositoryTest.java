package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByUsername("AAA");

        // then
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void testQuery() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findUser("AAA", 10);

        // then
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<String> result = memberRepository.findUsernameList();

        // then
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() throws Exception {
        // given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        // when
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        // then
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        // then
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> aaa = memberRepository.findListByUsername("AAA");

        Member bbb = memberRepository.findMemberByUsername("AAA");
        System.out.println("bbb = " + bbb);

        Optional<Member> ccc = memberRepository.findOptionalByUsername("AAA");

        List<Member> result = memberRepository.findListByUsername("ADFAFDAFA");
        System.out.println("result = " + result);
        // 반환값이 컬렉션일 때, 결과가 없을 때는 빈 컬렉션을 반환받기 때문에 null을 신경쓰지 않아도 된다

        Member findMember = memberRepository.findMemberByUsername("ADFADFAF");
        System.out.println("findMember = " + findMember);
        // 단건일 경우, 결과가 없으면 null이 반환된다

        Optional<Member> findOptional = memberRepository.findOptionalByUsername("ADFAFDAFA");
        System.out.println("findOptional = " + findOptional);
        // 단건을 조회할 경우에는 객체를 바로 받는 것 보다는 Optional을 사용하자
        // 조회된 데이터가 2건 이상일 경우 예외가 발생한다

    }

}
