package jpabook;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class Period {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Period() {
    }

    public Period(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    // setter를 지워서 불변 객체로 만든다
//    public void setStartDate(LocalDateTime startDate) {
//        this.startDate = startDate;
//    }

//    public void setEndDate(LocalDateTime endDate) {
//        this.endDate = endDate;
//    }
}
