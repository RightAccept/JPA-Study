package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class MemberSpec {
    // 명세를 정의하려면 Specification 인터페이스를 구현
    // 명세를 정의할 때는 toPredicate(...) 메서드만 구현하면 되는데, JPA Criteria의 Root, CriteriaQuery, CriteriaBuilder 클래스를 파라미터 제공
    //  - 예제에서는 편의상 람다를 사용
    // 결론 : 실무에서는 JPA Criteria를 거의 안쓴다! 대신에 QueryDSL을 사용하자

    public static Specification<Member> teamName(final String teamName) {
        return (Specification<Member>) (root, query, builder) -> {
            if (StringUtils.isEmpty(teamName)) {
                return null;
            }
            Join<Member, Team> t = root.join("team", JoinType.INNER); //회원과 조인
            return builder.equal(t.get("name"), teamName);
        };
    }
    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, builder) ->
                builder.equal(root.get("username"), username);
    }
}
