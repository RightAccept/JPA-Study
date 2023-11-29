package jpabook;

import javax.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    @ManyToOne  // 여러 명의 Member가(DTO 이름) 하나의 팀(밑에 적힌 클래스)
    @JoinColumn(name = "TEAM_ID")   // 어떤 컬럼과 연결할 것인지 지정
    private Team team;  // 단방향 연관관계

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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
