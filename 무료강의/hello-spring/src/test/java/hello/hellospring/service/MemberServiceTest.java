package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }


    // 테스트를 할 때는 given, when, then을 생각하자
    // given : 값이 주어지면, when : 이게 실행 됐을 때, then : 이게 나와야 해

    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("spring");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    // 예외를 검증하기 위한 테스트
    @Test
    public void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // when
        memberService.join(member1);
        // 예외를 검증하기 위해 try-catch를 사용할 수 있다
//        try {
//            memberService.join(member2);
//            fail();
//        } catch (IllegalStateException e) {
//            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
//        }

        // 예외 검증하려고 try-catch를 쓰기에는 번거롭다 싶으면 assertThrows를 사용할 수 있다
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        // memberService의 join에 member2를 넣었을 때, IllegalStateException이라는 예외가 터지면 true
        // 메세지를 테스트하기 위해 결과 변수를 받을 수 있다

        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

        // then

    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}