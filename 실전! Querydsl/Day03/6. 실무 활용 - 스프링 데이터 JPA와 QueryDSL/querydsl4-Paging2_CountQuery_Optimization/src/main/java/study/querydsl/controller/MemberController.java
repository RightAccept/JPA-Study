package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository memberJpaRepository;

    // 조회 API 컨트롤러 개발
    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMembeV1(MemberSearchCondition condition) {
        return memberJpaRepository.searchByWhereParameter(condition);
    }
}
