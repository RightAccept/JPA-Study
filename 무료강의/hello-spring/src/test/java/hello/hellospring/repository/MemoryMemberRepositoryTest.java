package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setName("spring");
        repository.save(member);

        Member result = repository.findById(member.getId()).get();
//        syso 단축 : soutv
        System.out.println("result = " + (result == member));
        assertEquals(member, result);
//      Assertions.assertEquals("검색할 객체 혹은 값", "결과 값");
//      Assertions에서 alt+엔터 누르면 앞의 Assertions를 제거할 수 있다
        assertThat(member).isEqualTo(result);
//        assertThat은 Assertions와는 다른 클래스이기 때문에, 따로 임포트 해줘야한다
//        assertThat() 까지 치고 alt+엔터 누르면 임포트 가능
    }


    @Test
    public void findByNmae() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        Member result = repository.findByName("spring1").get();

        assertThat(result).isEqualTo(member1);
    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        List<Member> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }
}
