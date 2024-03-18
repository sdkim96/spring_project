package web.web1.Member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Jdbc;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Random;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;

import web.web1.Config.AdminConfig;
import web.web1.Map.domain.models.Recommend;
import web.web1.Map.domain.searchhistory.CafeRecommendSystem;
import web.web1.Map.domain.searchhistory.SearchHistory;
import web.web1.Member.domain.models.Member;
import web.web1.Member.domain.repository.MemberRepository;
import web.web1.Member.domain.repository.TokenRepository;
import web.web1.Member.domain.token.AbstractTokenFactory;
import web.web1.Member.domain.token.GoogleTokenFactory;
import web.web1.Member.domain.token.NormalTokenFactory;
import web.web1.Member.domain.token.TokenFactory;
import web.web1.Member.domain.userprofile.UserProfile;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequiredArgsConstructor
public class MemberPageController {

    private final TokenFactory tokenFactory;
    private final ApplicationContext applicationContext;
    private final SearchHistory searchHistory;
    private final UserProfile userProfile;
    private final CafeRecommendSystem cafeRecommendSystem;
    private final JdbcTemplate jdbcTemplate;

    private Recommend recommend;

    @GetMapping("/memberpage/queryhistory")
    public ResponseEntity<?> queryHistory(HttpServletRequest request, @RequestParam(required = false) String provider) {
        System.out.println("--------쿼리 get요청 시작--------");

        // Authorization 헤더에서 토큰 값 추출
        String authorizationHeader = request.getHeader("Authorization");
        String tokenValue = authorizationHeader != null ? authorizationHeader.replace("Bearer ", "") : null;
        System.out.println("토큰값 = " + tokenValue);

        // Here you can use the provider if necessary for your logic
        System.out.println("Provider = " + provider);

        List<Map<String, Object>> historyList = null;

        if (tokenValue != null){
            historyList = searchHistory.getHistory(tokenValue);
        } else {
            System.out.println("토큰값이 없습니다.");
        }

        if (historyList != null){
            return ResponseEntity.ok(historyList);
        } else {
            return ResponseEntity.badRequest().body("히스토리를 불러오는데 실패했습니다.");
        }
    }

    @GetMapping("/memberpage/userprofile")
    public ResponseEntity<?> userprofile(HttpServletRequest request, @RequestParam(required = false) String provider) {
        System.out.println("--------유저프로필 get요청 시작--------");

        // Authorization 헤더에서 토큰 값 추출
        String authorizationHeader = request.getHeader("Authorization");
        String tokenValue = authorizationHeader != null ? authorizationHeader.replace("Bearer ", "") : null;
        System.out.println("토큰값 = " + tokenValue);

        // Here you can use the provider if necessary for your logic
        System.out.println("Provider = " + provider);

        Map<String, Object> output = null;

        if (tokenValue != null){
            output = userProfile.getUserProfile(tokenValue);
        } else {
            System.out.println("토큰값이 없습니다.");
        }

        if (output != null){
            return ResponseEntity.ok(output);
        } else {
            return ResponseEntity.badRequest().body("유저프로필를 불러오는데 실패했습니다.");
        }
    }

    
    @PostMapping("/memberpage/userprofile/update")
    public ResponseEntity<?> updateUserProfile(
            HttpServletRequest request,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("name") String name) {
        String authorizationHeader = request.getHeader("Authorization");
        String tokenValue = authorizationHeader != null ? authorizationHeader.replace("Bearer ", "") : null;

        if (tokenValue == null) {
            return ResponseEntity.badRequest().body("Token is missing.");
        }

        userProfile.updateUserProfile(tokenValue, photo, name);

        return ResponseEntity.ok().body("User profile updated successfully.");
    }

    @GetMapping("/memberpage/recommend")
    public ResponseEntity<?> recommend(HttpServletRequest request, @RequestParam(required = false) String provider) {
        System.out.println("--------유저 추천목록 get요청 시작--------");

        // Authorization 헤더에서 토큰 값 추출
        String authorizationHeader = request.getHeader("Authorization");
        String tokenValue = authorizationHeader != null ? authorizationHeader.replace("Bearer ", "") : null;
        System.out.println("토큰값 = " + tokenValue);

        List<String> allowedRoles = Arrays.asList("USER", "ADMIN");

        TokenFactory tokenFactory = applicationContext.getBean("googleTokenFactory", TokenFactory.class);
        Map<String, Object> decodedToken = tokenFactory.decodeToken(tokenValue);
        String oauth2ID = String.valueOf(decodedToken.get("oauth2Id"));

        Boolean isChecked = tokenFactory.checkToken(tokenValue, allowedRoles);
        Boolean isExpired = tokenFactory.isExpired(String.valueOf(decodedToken.get("exp")));

        System.out.println("토큰이 유효합니까? " + isChecked);
        System.out.println("토큰이 만료되었습니까? " + isExpired);

        /// Step2. 해당 유저의 좌표값 불러오기

        String sql1 = "SELECT email FROM member WHERE oauth2id = ?";
        String email = jdbcTemplate.queryForObject(sql1, String.class, oauth2ID);
        String sql2 = "SELECT * FROM recommend WHERE email = ?";
        List<Map<String, Object>> recommendList = jdbcTemplate.queryForList(sql2, email);
        
        // Step3. 추천 리스트에서 가장 최근의 좌표값 가져오기
        double givenLatitude = 0.0;
        double givenLongitude = 0.0;

        if (!recommendList.isEmpty()) {
            // 마지막 추천 항목 가져오기 (가정: list는 시간순으로 정렬됨)
            Map<String, Object> latestRecommend = recommendList.get(recommendList.size() - 1);

            // 추천 리스트에서 latitude와 longitude 추출
            givenLatitude = (double) latestRecommend.get("latitude");
            givenLongitude = (double) latestRecommend.get("longitude");
        }


        List<Map<String, Object>> output;
        if (isChecked && !isExpired) {
            // 추천 시스템에서 카페 추천 목록 가져오기
            output = cafeRecommendSystem.getCafeRecommendList(givenLatitude, givenLongitude);
        } else {
            System.out.println("토큰값이 없거나 토큰이 유효하지 않습니다.");
            return ResponseEntity.badRequest().body("토큰 검증 실패.");
        }

        return ResponseEntity.ok(output);
    }

    // @GetMapping("/user_profile_photos/{filename:.+}")
    // @ResponseBody
    // public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
    //     Path filePath = Paths.get("user_profile_photos").resolve(filename);
    //     try {
    //         Resource file = new UrlResource(filePath.toUri());
    //         if (file.exists() || file.isReadable()) {
    //             return ResponseEntity
    //                     .ok()
    //                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
    //                     .body(file);
    //         } else {
    //             throw new RuntimeException("Could not read the file!");
    //         }
    //     } catch (MalformedURLException e) {
    //         throw new RuntimeException("Error: " + e.getMessage());
    //     }
    // }




}
