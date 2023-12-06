package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    // 스프링 데이터 JPA는 메소드 이름을 분석해서 JPQL을 생성하고 실행
}

// 스프링 데이터 JPA가 제공하는 쿼리 메소드 기능
// - 조회 : find...By, read...By, query...By, get...By
//  - findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다
//  - By 뒤에 아무것도 넣지 않으면 모든 데이터를 가져온다
// - COUNT : count...By 반환타입 long
// - EXISTS : exists...By 반환타입 boolean
// - 삭제 : delete...By, remove...By 반환타입 long
// - DISTINCT : findDistinct, findMemberDistinctBy
// - LIMIT : findFirst3, findFirst, findTop, findTop3

// 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 꼭 함께 변경해야 한다
// - 그렇지 않으면 애플리케이션을 시작하는 시점에 오류가 발생
// - 이렇게 애플리케이션 로딩 시점에 오류를 인지할 수 있는 것이 스프링 데이터 JPA의 매우 큰 장점
