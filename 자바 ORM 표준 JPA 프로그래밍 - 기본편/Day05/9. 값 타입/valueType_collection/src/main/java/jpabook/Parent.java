package jpabook;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Parent {

    @Id @GeneratedValue
    private Long id;

    private String name;

//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)  // 얘를 저장할 때 연관된 애들도 같이 저장할거야 
    // 2번
//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)  // 고아 객체 자동 제거
    // 3번
    @OneToMany(mappedBy = "parent", orphanRemoval = true)  // 고아 객체 자동 제거
    private List<Child> childList = new ArrayList<>();
    // 얘는 Entity를 컬렉션에 넣은 것

    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }

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
}
