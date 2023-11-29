package jpabook;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Address {

    private String city;
    private String street;
    private String zipcode;

    public Address() {}

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }
    public String getZipcode() {
        return zipcode;
    }

    // setter를 지워서 불변 객체로 만든다
//    public void setCity(String city) {

//        this.city = city;

//    }
//    public void setStreet(String street) {
//        this.street = street;

//    }

//    public void setZipcode(String zipcode) {
//        this.zipcode = zipcode;
//    }
    
    // alt + insert 해서 equals() 및 hashCode() 선택해서 만들면 된다
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(city, address.city) && Objects.equals(street, address.street) && Objects.equals(zipcode, address.zipcode);
    }
}
