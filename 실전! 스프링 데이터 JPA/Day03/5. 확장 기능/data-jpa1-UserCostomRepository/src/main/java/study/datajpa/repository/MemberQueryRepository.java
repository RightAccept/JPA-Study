package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    // 비즈니스 로직과 화면에 뿌려주는 코드를 분리하기 위해 리포지토리를 분리하는 것이 좋다
    //  - API 만들때

    private final EntityManager em;

}
