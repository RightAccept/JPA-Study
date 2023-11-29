package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public class MemberService {
    // JPA를 사용하려면 Transactional을 붙여야한다

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /*회원가입
    같은 이름이 있는 중복 회원은 안된다고 가정*/
    public Long join(Member member) {
//       Optional<Member> result = memberRepository.findByName(member.getName());
//       memberRepository.findByName(member.getName()); 까지만 치고 ctrl + alt + v 누르면 앞에 자동으로 변수가 작성됨
//       result.ifPresent(m -> {
////        result가 null이 아니면 실행
////        Optional이기 때문에 사용 가능
//          throw new IllegalStateException("이미 존재하는 회원입니다.");
//       });
//       result.orElseGet();
//       값이 있으면 꺼내고, 없으면 어떤 메서드를 실행

//     안예쁘다 싶으면
        validateDuplicateMember(member);
        // 작성한 구문을 드래그 후 ctrl + alt + M을 누르면 해당 구문을 메서드로 따로 뺄 수 있다

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                     throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    // 전체 회원 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
