package jpabook;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    // 양방향 매핑
    @OneToMany(mappedBy = "team")   // mappedBy가 작성되어 있으면 조회만 가능하고 삽입, 수정이 불가능하다는 의미
    private List<Member> members = new ArrayList<>();   // DTO에 list를 만들 때 ArrayList로 초기화 해주는 것이 관례

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
