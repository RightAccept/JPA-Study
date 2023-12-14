package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // QueryDSL 전용 기능인 회원 search를 작성할 수 없다 => 사용자 정의 리포지토리 필요
    
    List<Member> findByUsername(String username);
}
