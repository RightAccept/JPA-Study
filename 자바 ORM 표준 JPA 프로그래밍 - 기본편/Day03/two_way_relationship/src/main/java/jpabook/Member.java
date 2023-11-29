package jpabook;

import javax.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

//    @Column(name = "TEAM_ID")
//    private Long teamId;

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

//    public Long getTeamId() {
//        return teamId;
//    }
//
//    public void setTeamId(Long teamId) {
//        this.teamId = teamId;
//    }


    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        
        // 양방향 연관관계를 사용할 때, A 세팅, B 세팅 두 번 작업하지 않기 위해서 setTeam에다가 세팅 구문을 넣어준다
        // 원래 있던거에서 빼는 메서드도 만들어야 하는데, 깊이있게 안들어간다고 안만드네
        // 근데 1차캐시 자주 비워줄거면 상관없긴해
        team.getMembers().add(this);
    }
}
