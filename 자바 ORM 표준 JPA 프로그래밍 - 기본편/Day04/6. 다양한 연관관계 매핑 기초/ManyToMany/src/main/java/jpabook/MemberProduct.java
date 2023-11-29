package jpabook;

import javax.persistence.*;
import java.time.LocalDateTime;

// 다대다 한계 극복
@Entity
public class MemberProduct {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int count;
    private int price;
    private LocalDateTime orderDate;
    // 등등 다대다를 사용할 경우 중간 테이블에 별도의 정보가 들어가는 경우가 많기 때문에
    // 중간 테이블을 엔티티로 승격시키고, 여러 값을 넣는 편이 낫다

    // member(PK, FK)와 product(PK, FK)를 PK로 묶을 수도 있지만, 2개의 PK를 묶는 것 보다는 의미없는 값을 하나 만들어서 PK로 두는 것이 낫다
    // 그 순간에는 제약조건 걸 때 장점이 많은데, 운영하다 보면 어디에 종속 되어 있다는 것 때문에 시스템의 유연성이 떨어진다
}
