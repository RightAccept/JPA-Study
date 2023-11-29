package jpabook;

import javax.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID" ,insertable = false, updatable = false)
    private Team team;  // 일대다 양방향
    // 이런 매핑은 공식적으로 존재X
    // @JoinColumn(insertable=false, updatable = false)를 적용하여 읽기 전용으로 설정
    // 읽기 전용 필드를 사용해서 양방향처럼 사용하는 방법

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = username;
    }

}
