package kr.easw.lesson02.controller;

// AWS 관련 서비스 로직을 포함하고 있는 패키지
import kr.easw.lesson02.service.AWSService;

// Lombok 라이브러리의 어노테이션으로,
// 클래스에 선언된 final 필드를 모두 매개변수로 갖는 생성자를 생성
import lombok.RequiredArgsConstructor;

// 스프링의 컴포넌트 스캔에서 이 클래스를 웹 컨트롤러로 인식하도록 하는 어노테이션
import org.springframework.stereotype.Controller;

// 요청 경로를 지정하는 스프링의 어노테이션
import org.springframework.web.bind.annotation.RequestMapping;

// 클라이언트의 요청을 처리한 결과를 담아서 반환하는 객체
import org.springframework.web.servlet.ModelAndView;


@Controller  // Spring MVC의 컨트롤러 어노테이션
@RequiredArgsConstructor  // Lombok으로 필수 의존성 주입을 위한 생성자 자동 생성
public class BaseWebController {

    private final AWSService awsController;  // AWS 관련 서비스

    // AWS API 초기화 상태에 따라 다른 뷰를 반환하는 메소드
    @RequestMapping("/")
    public ModelAndView onIndex() {
        return new ModelAndView(awsController.isInitialized() ? "upload.html" : "request_aws_key.html");
    }

    // 서버 에러 페이지 테스트를 위한 메소드
    @RequestMapping("/server-error")
    public ModelAndView onErrorTest() {
        return new ModelAndView("error.html");
    }

    // 파일 업로드 페이지를 반환하거나 AWS API 키 요청 페이지를 반환하는 메소드
    @RequestMapping("/upload")
    public ModelAndView onUpload() {
        return new ModelAndView(awsController.isInitialized() ? "upload.html" : "request_aws_key.html");
    }

    // 파일 다운로드 페이지를 반환하거나 AWS API 키 요청 페이지를 반환하는 메소드
    @RequestMapping("/download")
    public ModelAndView onDownload() {
        return new ModelAndView(awsController.isInitialized() ? "download.html" : "request_aws_key.html");
    }
}