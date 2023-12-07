package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    // 사용자 정의 인터페이스를 사용할 때 규칙
    // ※ 클래스 이름은 주 리포지토리 + Imple이다
    //  - 지금은 MemberRepository + Imple
    // ※ 다른 이름을 사용하고 싶다면 XML을 설정하거나, JavaConfig를 설정해야 한다
    //  - 그냥 관례 따라서 써라
    
    // 추가
    // ※ 스프링 데이터 2.x 부터는 사용자 정의 구현 클래스에 리포지토리 인터페이스 이름 + Imple을 적용하는 대신, 사용자 정의 인터페이스 명 + Imple도 지원한다
    //  - MemberRepositoryCustomImpl도 가능하다는 소리
    //  - 기존 방식보다 사용자 정의 인터페이스 이름과 구현 클래스 이름이 비슷하므로 더 직관적이다
    //  - 여러 인터페이스를 분리해서 구현하는 것도 가능하기 때문에 새롭게 변경된 이 방식을 더 권장

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
