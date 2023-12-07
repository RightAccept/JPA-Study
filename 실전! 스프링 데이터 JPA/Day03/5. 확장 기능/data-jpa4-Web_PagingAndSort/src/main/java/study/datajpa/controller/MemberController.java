package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    
    // 도메인 클래스 컨버터 사용 전
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 클래스 컨버터 사용 후
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }
    // HTTP 요청은 회원 id를 받지만 도메인 클래스 컨버터가 중간에 동작하여 회원 엔티티 객체를 반환
    // 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음
    // 주의 : 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다
    //  - 트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않는다
    
    
    // 스프링 데이터가 제공하는 페이징과 정렬 기능을 스프링 MVC에서 편리하게 사용할 수 있다
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 12) Pageable pageable) {
        // 파라미터로 Pageable을 받을 수 있다
        // Pageable은 인터페이스, 실제는 PageRequest 객체 생성
        // 요청 파라미터
        //  ex) /members?page=0&size=3&sort=id,desc&sort=username,desc
        //  - page : 현재 페이지, 0부터 시작한다
        //  - size : 한 페이지에 노출할 데이터 건수  -> 기본값 20, 전체 적으로 바꾸고 바꾸고 싶으면 application.yml에서 수정, 하나만 바꾸고 싶으면 @PageableDefault 어노테이션 사용
        //  - sort : 정렬 조건을 정의한다
        //      - ex) 정렬 속성, 정렬 속성... (ASC | DESC), 정렬 방향을 변경하고 싶으면 sort 파라미터 추가(asc 생략 가능)
        Page<Member> page = memberRepository.findAll(pageable);

        // Page 내용을 DTO로 변환하기
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        return map;
    }
    // 스프링 데이터는 Page를 0부터 시작한다. 만약 1부터 시작하려면?
    // 1. Pageable, Page를 파라미터와 응답 값으로 사용하지 않고, 직접 클래스를 만들어서 처리한다.
    //  - 직접 PageRequest(Pageable 구현체를) 생성해서 리포지토리에 넘긴다.
    //  - 물론 응답값도 Page 대신에 직접 만들어서 제공해야 한다
    // 2. application.yml에서 spring.data.web.pageable.one-indexed-parameters를 true로 설정한다
    //  - 그런데 이 방법은 web에서 page 파라미터를 -1 처리할 뿐이다
    //  - 따라서 응답값인 Page에 모두 0 페이지 인덱스를 사용하는 한계가 있다

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
