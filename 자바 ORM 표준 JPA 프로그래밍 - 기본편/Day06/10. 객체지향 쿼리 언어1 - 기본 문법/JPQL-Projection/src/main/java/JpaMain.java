import jpql.*;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();
            
            // 엔티티 프로젝션 : 결과가 몇 개가 나오든 영속성 컨텍스트에서 관리됨
            List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList();

            Member findMember = result.get(0);
            findMember.setAge(20);  // 여기서 update 생김

//            List<Team> resultTeam = em.createQuery("select m.team from Member m", Team.class).getResultList();  // JOIN 쿼리가 나간다
            List<Team> resultTeam = em.createQuery("select t from Member m join m.team t", Team.class).getResultList();
            // 위의 방식으로 조인하면 헷갈리기 때문에, join 한다는 것을 명시적으로 표현하는 아래의 것을 사용하는 것이 좋다

            // 임베디드 프로젝션
            em.createQuery("select o.address from Order o", Address.class).getResultList();
            // address는 임베디드 타입인데 select가 잘 나간다
            // -> 임베디드 타입만으로는 안되고, 엔티티로부터 시작해야 한다(o.address)

            // 스칼라 타입 프로젝션
            em.createQuery("select distinct m.username, m.age from Member m").getResultList();

            // DTO로 여러 값 조회
            List<MemberDTO> DTOselect = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class).getResultList();
            // DTO를 생성하고, 생성자를 만들어야 함
            // new 생성자와 함께 패키지과 전체 클래스 명도 명시해줘야 한다
            MemberDTO memberDTO = DTOselect.get(0);
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());

            tx.commit();
        } catch (Exception e) {
            System.err.println("예외 발생 : " + e);
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}

// 프로젝션
// ※ SELECT 절에 조회할 대상을 지정하는 것
// ※ 프로젝션 대상 : 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본 데이터 타입)
// - SELECT m FROM Member m -> 엔티티 프로젝션
// - SELECT m.team FROM Member m -> 엔티티 프로젝션
// - SELECT m.address FROM Member m -> 임베디드 타입 프로젝션
// - SELECT m.username, m.age FROM Member m -> 스칼라 타입 프로젝션
// ※ DISTINCT로 중복 제거

// 프로젝션 - 여러 값 조회
// Q. 스칼라 타입 프로젝션은 String과 int 처럼 여러 값을 가져오는데 어떻게 가져와야 할까?
// 1. Query 타입으로 조회
// 2. Object[] 타입으로 조회
// 3. new 명령어로 조회
//  - 단순 값을 DTO로 바로 조회(select new jpabook.jpql.UserDTO(m.username, m.age) FROM Member m)
//  - 패키지 명을 포함한 전체 클래스 명 입력
//  - 순서와 타입이 일치하는 생성자 필요