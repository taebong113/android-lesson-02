package kr.easw.lesson02.controller;

// AWS 키 관련 DTO
import kr.easw.lesson02.model.dto.AWSKeyDto;
// AWS 관련 서비스
import kr.easw.lesson02.service.AWSService;

// Lombok의 RequiredArgsConstructor 어노테이션 (의존성 주입을 위함)
import lombok.RequiredArgsConstructor;

// Spring의 리소스 (다운로드할 때 사용되는 스트림 리소스)
import org.springframework.core.io.InputStreamResource;
// Spring의 HTTP 상태 및 헤더
import org.springframework.http.*;
// Spring의 REST 컨트롤러 관련 어노테이션들
import org.springframework.web.bind.annotation.*;
// MultipartFile (파일 업로드에 사용)
import org.springframework.web.multipart.MultipartFile;
// Spring의 Model 및 View (화면 이동 및 데이터 전송에 사용)
import org.springframework.web.servlet.ModelAndView;

// 입력 스트림 (파일 다운로드에 사용)
import java.io.InputStream;
// URL 인코딩을 위한 클래스
import java.net.URLEncoder;
// 문자열 인코딩 (UTF-8 등)
import java.nio.charset.StandardCharsets;
// Java의 리스트 데이터 타입
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rest/aws")
class AWSController {

    // AWS 서비스
    private final AWSService awsService;

    // AWS 키를 사용해 서비스를 초기화
    @PostMapping("/auth")
    private ModelAndView onAuth(AWSKeyDto awsKey) {
        try {
            awsService.initAWSAPI(awsKey);
            return new ModelAndView("redirect:/");
        } catch (Exception ex) {
            return new ModelAndView("redirect:/server-error?errorStatus=" + ex.getMessage());
        }
    }

    // S3에서 파일 목록 조회
    @GetMapping("/list")
    private List<String> onFileList() {
        return awsService.getFileList();
    }

    // S3에 파일 업로드
    @PostMapping("/upload")
    private ModelAndView onUpload(@RequestParam MultipartFile file) {
        try {
            awsService.upload(file);
            return new ModelAndView("redirect:/?success=true");
        } catch (Exception ex) {
            return new ModelAndView("redirect:/server-error?errorStatus=" + ex.getMessage());
        }
    }

    // S3에서 파일 다운로드
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> onDownload(@RequestParam String fileName) {
        try {
            InputStream s3ObjectContent = awsService.download(fileName);
            InputStreamResource resource = new InputStreamResource(s3ObjectContent);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}