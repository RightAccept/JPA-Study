public class ValueMain {

    public static void main(String[] args) {
//        int a = 10;
//        int b = a;
//        b = 20;

        Integer a = new Integer(10);
        Integer b = a;

        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
}


// JPA의 데이터 타입 분류
// ※ 엔티티 타입
// - @Entity로 정의하는 객체
// - 데이터가 변해도 식별자로 지속해서 추적 가능
// - ex) 회원 에티티의 키나 나이 값을 변경해도 식별자로 인식 가능
// ※ 값 타입
// - int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
// - 식별자가 없고 값만 있으므로 변경시 추적 불가
// - ex) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체

// 값 타입 분류
// ※ 기본값 타입
// - 자바 기본 타입(int, double)
// - 래퍼 클래스(Integer, Long)
// - String
// ※ 임베디드 타입(embedded type, 복합 값 타입)
// -> 위도, 경도를 묶어서 하나의 좌표로 사용하고 싶을 때
// ※ 컬렉션 값 타입(collection value type)

// 기본 값 타입
// ex) String name, int age
// ※ 생명 주기를 엔티티에 의존
// ex) 회원을 삭제하면 이름, 나이 필드도 함께 삭제
// ※ 값 타입은 공유하면 X
// ex) 회원 이름 변경시 다른 회원의 이름도 함께 변경되면 안됨
// 참고 : 자바의 기본 타입은 절대 공유 X
// - int, double 같은 기본 타입(primitive type)은 절대 공유 X
// - 기본 타입은 항상 값을 복사함
// - Integer 같은 래퍼 클래스나 String 같은 특수한 클래스는 공유 가능한 객체이지만 변경 X
