package jpabook;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
            // Movie를 추가하면, Item이 먼저 추가된 후, Movie에 추가된다
            Movie movie = new Movie();
            movie.setDirector("aaaa");
            movie.setActor("bbbb");
            movie.setName("바람과 함께 사라지다");
            movie.setPrice(10000);
            em.persist(movie);
            
            em.flush();
            em.clear();
            
            // Movie를 찾을 때, Movie를 중심으로 Item을 join해서 가져온다
//            Movie findMovie = em.find(Movie.class, movie.getId());
//            System.out.println("findMovie = " + findMovie);
            
            // 구현 클래스마다 테이블 전략 조회 예시
            // 부모 클래스로 조회 시, 자식 테이블을 전체 UNION으로 가져온다
            Item item = em.find(Item.class, movie.getId());
            System.out.println("item = " + item);

            tx.commit();    // 현재 트랜잭션을 commit
        } catch (Exception e) {
            tx.rollback();
        } finally {
            // entityManger 닫기
            em.close();
        }
        // entityMangerFactory 닫기
        emf.close();

    }
}

// 상속관계 매핑
// - 관계형 데이터베이스는 상속 관계 X
// - 슈퍼타입 서브타입 관계라는 모델링 기법이 객체 상속과 유사
// - 상속관계 매핑 : 객체의 상속 구조와 DB의 슈퍼타입 서브타입 관계를 매핑
// - ex) 물품
//        -> 음반, 영화, 책

// - DB에서 슈퍼타입 서브타입 논리 모델을 실제 물리 모델로 구현하는 방법
// 1. 각각 테이블로 변환 -> 조인 전략, item이라는 테이블에 공통의 이름, 가격을 두고, ALBUM, MOVIE, BOOK 테이블 각각이 고유 컬럼을 가진다
// 2. 통합 테이블로 변환 -> 단일 테이블 전략, item 테이블에 ALBUM, MOVIE, BOOK 테이블의 컬럼들을 한 번에 넣는다, 해당 타입을 사용할 경우, @DiscriminatorColumn이 "DTYPE"으로 자동으로 생성된다
// 3. 서브타입 테이블로 변환 -> 구현 클래스마다 테이블 전략, item이라는 테이블 없이, ALBUM, MOVIE, BOOK 테이블 각각이 이름, 가격 등을 가진다

// 주요 어노테이션
// - @Inheritance(strategy=InheritanceType.XXX)
// -- JOINED : 조인 전략
// -- SINGLE_TABLE : 단일 테이블 전략
// -- TABLE_PER_CLASS : 구현 클래스마다 테이블 전략

// - @DiscriminatorColumn(name="DTYPE")
// -- 부모 클래스에 작성한다.
// -- 작성할 경우, 어떤 자식 때문에 부모 테이블에 데이터가 들어왔는지 명시해준다
// ex) Movie 테이블에 데이터를 insert할 경우, Item 테이블에 데이터가 insert Movie 때문에 insert 됐다는 것을 명시해준다

// - @DiscriminatorValue("XXX")
// -- 자식 클래스에서 작성한다
// -- 부모 클래스에 명시될 때, 어떤 식으로 명시될 지 작성한다
// ex) Movie의 @DiscriminatorValue를 "M"으로 작성할 경우, Item 클래스의 DTYPE에 "M"으로 작성된다

// 조인 전략
// - 장점
// -- 테이블 정규화
// -- 외래키 참조 무결성 제약조건 활용 가능
// -- 저장공간 효율화
// - 단점
// -- 조회시 조인을 많이 사용, 성능 저하
// -- 조회 쿼리가 복잡합
// -- 데이터 저장시 INSERT SQL 2번 호출

// 단일 테이블 전략
// - 장점
// -- 조인이 필요 없으므로 일반적을 조회 성능이 빠름
// -- 조회 쿼리가 단순함
// - 단점
// -- 자식 엔티티가 매핑한 컬럼은 모두 null 허용
// -- 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다.
// --> 상황에 따라서 조회 성능이 오히려 느려질 수 있다

// 구현 클래스마다 테이블 전략
// - 이 전략은 데이터베이스 설계자와 ORM 전문가 둘 다 추천 X
// - 장점
// -- 서브 타입을 명확하게 구분해서 처리할 때 효과적
// -- not null 제약조건 사용 가능
// - 단점
// -- 여러 자식 테이블을 함께 조회할 때 성능이 느림(UNION SQL 필요)
// -- 자식 테이블을 통합해서 쿼리하기 어려움