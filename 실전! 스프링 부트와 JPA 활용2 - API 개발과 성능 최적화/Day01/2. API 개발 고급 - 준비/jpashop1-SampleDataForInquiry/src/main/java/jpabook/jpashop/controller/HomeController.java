package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j  // 로그를 남기기 위한 어노테이션, 롬복일 때 사용 가능
public class HomeController {

//    Logger log = LoggerFactory.getLogger(getClass()); // 롬복을 사용하지 않을 때 로거 생성

    @RequestMapping("/")
    public String home() {
        log.info("home controller");    // 최상위(/) 요청이 오면, 로그에 "home controller"라는 기록을 남기라는 의미
        return "home";
    }
}
