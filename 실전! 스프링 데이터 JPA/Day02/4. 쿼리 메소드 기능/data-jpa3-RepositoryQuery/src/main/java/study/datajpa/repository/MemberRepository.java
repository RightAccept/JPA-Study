package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
//    List<Member> findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    // 스프링 데이터 JPA는 메소드 이름을 분석해서 JPQL을 생성하고 실행

    @Query(name = "Member.findByUsername")  // 얘를 주석처리해도 실행이 된다.
    // 스프링 데이터 JPA는 선언한 "도메인 클래스 + .(점) + 메서드 이름"으로 Named 쿼리를 찾아서 실행
    // - 만약 실행할 Named 쿼리가 없으면 메서드 이름으로 쿼리 생성 전략을 사용
    List<Member> findByUsername(@Param("username") String username);
    // @Param : 명확하게 JPQL이 있을 경우. 현재 Member 엔티티에는 NamedQuery로 JPQL이 작성되어 있다

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
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

