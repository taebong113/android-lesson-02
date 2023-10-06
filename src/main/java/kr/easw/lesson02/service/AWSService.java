package kr.easw.lesson02.service;

// AWS 인증에 필요한 클래스
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

// AWS 리전 설정을 위한 클래스
import com.amazonaws.regions.Regions;

// S3 서비스와 관련된 클래스
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

// AWS 키 정보를 위한 DTO
import kr.easw.lesson02.model.dto.AWSKeyDto;

// 예외처리를 위한 Lombok 어노테이션
import lombok.SneakyThrows;

// 스프링에서 서비스 레이어로 사용되기 위한 어노테이션
import org.springframework.stereotype.Service;

// 파일 업로드와 관련된 클래스
import org.springframework.web.multipart.MultipartFile;

// 스트림 처리를 위한 클래스
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;




@Service
public class AWSService {

    // 버킷 이름 생성 - 중복을 피하기 위해 랜덤 UUID를 사용
    private static final String BUCKET_NAME = "easw-random-bucket-" + UUID.randomUUID();

    // S3 클라이언트 객체
    private AmazonS3 s3Client = null;

    // AWS S3 초기화 메서드
    public void initAWSAPI(AWSKeyDto awsKey) {
        // S3 클라이언트 설정 및 생성
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsKey.getApiKey(), awsKey.getApiSecretKey())))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();

        // 기존 "easw-random-bucket-"으로 시작하는 버킷이 있으면 삭제
        for (Bucket bucket : s3Client.listBuckets()) {
            if (bucket.getName().startsWith("easw-random-bucket-")) {
                s3Client.listObjects(bucket.getName())
                        .getObjectSummaries()
                        .forEach(it -> s3Client.deleteObject(bucket.getName(), it.getKey()));
                s3Client.deleteBucket(bucket.getName());
            }
        }

        // 새 버킷 생성
        s3Client.createBucket(BUCKET_NAME);
    }

    // S3 클라이언트 초기화 여부 확인 메서드
    public boolean isInitialized() {
        return s3Client != null;
    }

    // 현재 버킷의 파일 목록을 가져오는 메서드
    public List<String> getFileList() {
        return s3Client.listObjects(BUCKET_NAME).getObjectSummaries().stream().map(S3ObjectSummary::getKey).toList();
    }

    // 파일을 S3 버킷에 업로드하는 메서드
    @SneakyThrows
    public void upload(MultipartFile file) {
        s3Client.putObject(BUCKET_NAME, file.getOriginalFilename(), new ByteArrayInputStream(file.getResource().getContentAsByteArray()), new ObjectMetadata());
    }

    // S3 버킷에서 파일을 다운로드하는 메서드
    @SneakyThrows
    public InputStream download(String fileName) {
        S3Object s3Object = s3Client.getObject(BUCKET_NAME, fileName);
        return s3Object.getObjectContent();
    }
}