package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

//    @PostMapping("/members/new")
//    public String create(@Valid MemberForm form) {    // @Valid : javax.validation 하위의 클래스들을 사용하겠다는 표시
//        // 해당 PostMapping에서는 name에 값이 없으면 예외 페이지로 이동한다
//        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
//
//        Member member = new Member();
//        member.setName(form.getName());
//        member.setAddress(address);
//
//        memberService.join(member);
//        return "redirect:/";
//    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {    // @Valid : javax.validation 하위의 클래스들을 사용하겠다는 표시
        // BindingResult : 보통 예외가 터지면 페이지 자체가 터져버리는데, BindingResult를 사용하면 BindingResult 객체에 예외를 담은 상태로 내부 코드를 계속 실행한다

        if (result.hasErrors()) {   // 만약 result에 예외가 있다면
            return "members/createMemberForm";  // 회원 등록 페이지로 다시 이동한다
            // form의 데이터도 다시 가지고 돌아간다 => 포워딩
        }
        
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
//        model.addAttribute("members", memberService.findMembers());
        // 기능이 복잡해질 수록 Member 엔티티를 직접 전달하는 것 보다는 DTO를 생성해서 전달하자
        return "members/memberList";
    }
}

// 굳이 MemberForm이라는 객체를 만들어야 할까?
// 지금까지 우리가 했던대로 DTO의 역할을 대신하는 Entity를 사용하면 되지 않을까?
// - 요구사항이 정말 단순할 때는 폼 객체(MemberForm) 없이 엔티티(Member)를 직접 등록과 수정 화면에서 사용해도 된다
// - 화면 요구사항이 복잡해지기 시작하면, 엔티티에 화면을 처리하기 위한 기능이 점점 증가하면서, 
//      엔티티는 점점 화면에 종속적으로 변하고, 지저분해진 엔티티는 유지보수하기 어려워진다
// - 실무에서 엔티티는 핵심 비즈니스 로직만 가지고 있고, 화면을 위한 로직은 없어야 한다
// - 화면이나 API에 맞는 폼 객체나 DTO를 사용하자

// API를 만들때는 절대 Entity를 반환하면 안된다
// - 외부로 Entity를 유출하면 안된다
// - Entity가 수정되면 API 스펙이 변경되기 때문
