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
}
