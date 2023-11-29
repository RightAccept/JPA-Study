package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    // Optional : findById에서 결과값이 null일 수 있는데, 요즘은 null을 그대로 반환하는 대신 감싸고 반환하는 방법을 선호
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
