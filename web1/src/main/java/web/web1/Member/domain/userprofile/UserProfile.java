package web.web1.Member.domain.userprofile;

import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import web.web1.Member.domain.token.TokenFactory;


@Service
@RequiredArgsConstructor
public class UserProfile {

    private final ApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> getUserProfile(String tokenValue) {

        TokenFactory tokenFactory = applicationContext.getBean("googleTokenFactory", TokenFactory.class);

        
        // 이 메소드는 user와 admin인 경우 실행 가능함
        List<String> allowedRoles = Arrays.asList("USER", "ADMIN");

        Map<String, Object> decodedToken = tokenFactory.decodeToken(tokenValue);
        String oauth2Id = (String) decodedToken.get("oauth2Id");

        Boolean isChecked = tokenFactory.checkToken(tokenValue, allowedRoles);
        Boolean isExpired = tokenFactory.isExpired(String.valueOf(decodedToken.get("exp")));

        if (isChecked && !isExpired){
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효합니다.");

            Map<String, Object> userProfile = null;
            try{
                // SQL 시작
                String sql1 = "SELECT * FROM member WHERE oauth2id = ?";
                System.out.println("사용자 정보를 찾습니다.");
                userProfile = jdbcTemplate.queryForMap(sql1 ,oauth2Id);
                System.out.println("사용자 정보를 찾았습니다.");
                return userProfile;
            } catch (Exception e) {
                System.out.println("사용자 정보를 찾을 수 없습니다.");
                return null;
            }
        }
        else {
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효하지 않습니다.");
            return null;
        }

    }

    public void updateUserProfile(String tokenValue, MultipartFile photo, String newName) {
        if (tokenValue == null) {
            System.out.println("토큰값이 없습니다. 이 작업을 실행할 수 없습니다.");
            return; // 조기 반환
        }

        TokenFactory tokenFactory = applicationContext.getBean("googleTokenFactory", TokenFactory.class);

        // 이 메소드는 user와 admin인 경우 실행 가능함
        List<String> allowedRoles = Arrays.asList("USER", "ADMIN");
        Map<String, Object> decodedToken = null;

        try{
            decodedToken = tokenFactory.decodeToken(tokenValue);
            if (decodedToken.isEmpty()) {
                System.out.println("토큰 디코드 중 오류가 발생했습니다.");
                return;
            } else {
                System.out.println("토큰 디코드 성공");
            }
        } catch (Exception e) {
            System.out.println("토큰 디코드 중 오류가 발생했습니다.");
            return;
        }

        
        String oauth2Id = (String) decodedToken.get("oauth2Id");

        Boolean isChecked = tokenFactory.checkToken(tokenValue, allowedRoles);
        Boolean isExpired = tokenFactory.isExpired(String.valueOf(decodedToken.get("exp")));
        
        if (isChecked && !isExpired) {
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효합니다.");
    
            try {
                String sql = "UPDATE member SET name = ?, photo_path = ? WHERE oauth2id = ?";
                String photoPath = null;
                if (photo != null && !photo.isEmpty()) {
                    photoPath = savePhotoAndGetPath(photo, oauth2Id); 
                }

                jdbcTemplate.update(sql, newName, photoPath, oauth2Id);
            } catch (Exception e) {
                System.out.println("프로필을 업데이트중 오류발생함 :  " + e.getMessage());
            }
        } else {
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효하지 않습니다.");
        }
    }


    
    private String savePhotoAndGetPath(MultipartFile photo, String oauth2Id) {
        try {
            // 변경된 절대 경로
            String directoryPath = "C:\\projects\\web1\\src\\main\\resources\\static\\user_profile_photos\\";
            String photoFileName = oauth2Id + "_profile_photo" + getExtension(photo.getOriginalFilename());
            Path directory = Paths.get(directoryPath);
    
            System.out.println("directory경로 = " + directory);
    
            // Check if the directory exists, create it if it doesn't
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
    
            Path savePath = directory.resolve(photoFileName);
            System.out.println("save경로 = " + savePath);
            System.out.println("save경로 = " + savePath.toString());
    
            // Save the photo
            Files.copy(photo.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
    
            // 절대 경로 대신 웹 접근 가능한 상대 경로 반환
            String accessiblePath = "/user_profile_photos/" + photoFileName;
            return accessiblePath;
        } catch (Exception e) {
            System.out.println("Failed to save photo: " + e.getMessage());
            return null;
        }
    }
    
    
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
}
