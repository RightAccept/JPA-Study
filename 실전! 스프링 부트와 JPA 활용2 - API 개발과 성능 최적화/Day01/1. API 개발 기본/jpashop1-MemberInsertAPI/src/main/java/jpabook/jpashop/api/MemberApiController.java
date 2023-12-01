package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 등록 v1 : 요청 값으로 Member 엔티티를 직접 받는다
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberv1(@RequestBody @Valid Member member) {
        // 매개변수의 @RequestBody
        // - json으로 들어온 데이터를 엔티티로 바꿔준다
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    /*
        ※ 문제점
          - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다
           - 엔티티에 API 검증으 루이한 로직이 들어간다 (@NotEmpty 등등)
            - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다
          - 엔티티가 변경되면 API 스펙이 변한다
          
         ※ 결론
         - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다
     */

    // 등록 v2 : 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberv2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    /*
        ※ CreateMemberRequest를 Member 엔티티 대신에 RequestBody와 매핑한다
        ※ 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다
        ※ 엔티티와 API 스펙을 명확하게 분리할 수 있다
        ※ 엔티티가 변해도 API 스펙이 변하지 않는다
     */

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    // 참고 : 실무에서는 엔티티를 API 스펙에 노출하면 안된다
    @Data
    static class CreateMemberResponse {

        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }

    }
}
