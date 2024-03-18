package web.web1.Map.domain.searchhistory;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import web.web1.Member.domain.token.TokenFactory;

@Service
@RequiredArgsConstructor
public class CafeRecommendSystem {
    
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationContext applicationContext;

    public void createRecommend(String tokenValue, double givenLatitude, double givenLongitude) {

        TokenFactory tokenFactory = applicationContext.getBean("googleTokenFactory", TokenFactory.class);

        // 이 메소드는 user와 admin인 경우 실행 가능함
        List<String> allowedRoles = Arrays.asList("USER", "ADMIN");

        Map<String, Object> decodedToken = tokenFactory.decodeToken(tokenValue);
        String oauth2Id = (String) decodedToken.get("oauth2Id");

        Boolean isChecked = tokenFactory.checkToken(tokenValue, allowedRoles);
        Boolean isExpired = tokenFactory.isExpired(String.valueOf(decodedToken.get("exp")));

        if (isChecked && !isExpired){
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효합니다.");

            // SQL 시작
            String sql1 = "SELECT email FROM member WHERE oauth2id = ?";
            String email = jdbcTemplate.queryForObject(sql1, String.class, oauth2Id);

            if (email != null) {
                String sql2 = "INSERT INTO recommend (email, latitude, longitude) VALUES (?, ?, ?)";
                jdbcTemplate.update(sql2, email, givenLatitude, givenLongitude);
            } else {
                System.out.println("당신의 이메일이 존재하지 않습니다.");
            }
        }
        else {
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효하지 않습니다.");
        }
    }

    public List<Map<String, Object>> getCafeRecommendList(double givenLatitude, double givenLongitude) {

        // SQL 시작
        // String makeLatIndexOfCafeTable = "CREATE INDEX idx_latitude ON cafe_table(latitude);";
        // String makeLonIndexOfCafeTable = "CREATE INDEX idx_longitude ON cafe_table(longitude);";
        // 위 2 sql문은 한 번만 실행하면 됨(실행했음)

        String timeComplexityLogN = """
        (SELECT *, 
            (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) + sin(radians(?)) * sin(radians(latitude)))) AS distance
        FROM cafe_table
        WHERE latitude BETWEEN ? - 0.0045 AND ? + 0.0045
        ORDER BY distance ASC
        LIMIT 100) 
        UNION
        (SELECT *, 
            (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) + sin(radians(?)) * sin(radians(latitude)))) AS distance
        FROM cafe_table
        WHERE longitude BETWEEN ? - 0.0045 AND ? + 0.0045
        ORDER BY distance ASC
        LIMIT 100)
        ORDER BY distance ASC
        LIMIT 5;
        """;

    // N 시간 복잡도를 가진 쿼리 유지
    String timeComplexityN = """
        SELECT
          *,
          (
            6371 * acos(
              cos(radians(?)) * cos(radians(latitude)) *
              cos(radians(longitude) - radians(?)) +
              sin(radians(?)) * sin(radians(latitude))
            )
          ) AS distance
        FROM
          cafe_table
        HAVING
          distance < 5 -- 5km 이내
        ORDER BY
          distance
        LIMIT 50;
        """;

    // jdbcTemplate.queryForList() 메서드를 사용하여 SQL 쿼리를 실행하고 결과를 받습니다.
    return jdbcTemplate.queryForList(timeComplexityLogN, new Object[]{
        givenLatitude, givenLongitude, givenLatitude, givenLatitude, givenLatitude,
        givenLatitude, givenLongitude, givenLatitude, givenLongitude, givenLongitude
    });        
    } 
}
