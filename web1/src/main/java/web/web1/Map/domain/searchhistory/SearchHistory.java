package web.web1.Map.domain.searchhistory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import web.web1.Member.domain.models.Member;
import web.web1.Member.domain.token.TokenFactory;


public class SearchHistory {

    private TokenFactory tokenFactory;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SearchHistory(JdbcTemplate jdbcTemplate, TokenFactory tokenFactory, Member member) {
        this.tokenFactory = tokenFactory;
        this.jdbcTemplate = jdbcTemplate;
    }
    

    public List<Map<String, Object>> getHistory(String tokenValue) {
        
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
                String sql2 = "SELECT * FROM memberhistory WHERE email = ?";
                List<Map<String, Object>> historyList = jdbcTemplate.queryForList(sql2, email);
                return historyList;
            } else {
                System.out.println("당신의 이메일이 존재하지 않습니다.");
                return Collections.emptyList();
            }
        }
        else {
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효하지 않습니다.");
            return null;
        }

    }

    public void createHistory(String tokenValue, String searchQuery) {

        // 이 메소드는 user와 admin인 경우 실행 가능함
        List<String> allowedRoles = Arrays.asList("USER", "ADMIN");

        Map<String, Object> decodedToken = tokenFactory.decodeToken(tokenValue);
        String oauth2Id = (String) decodedToken.get("oauth2Id");

        Boolean isChecked = tokenFactory.checkToken(tokenValue, allowedRoles);
        Boolean isExpired = tokenFactory.isExpired(String.valueOf(decodedToken.get("exp")));
        
        if (isChecked && !isExpired){
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효합니다.");

            // oauth2Id를 이용해 사용자 이메일 조회
            String sql1 = "SELECT email FROM member WHERE oauth2id = ?";
            String email = jdbcTemplate.queryForObject(sql1, String.class, oauth2Id);
            if (email != null) {
                // 현재 시간을 검색 기록의 시간으로 사용
                LocalDateTime searchDateTime = LocalDateTime.now();
                String sql2 = "INSERT INTO memberhistory (email, search_query, search_datetime) VALUES (?, ?, ?)";
                jdbcTemplate.update(sql2, email, searchQuery, searchDateTime);
                System.out.println("검색 기록이 추가되었습니다.");
            } else {
                System.out.println("사용자 정보를 찾을 수 없습니다.");
            }
        } else {
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효하지 않습니다.");
        }
    }


    public void deleteHistory(String tokenValue) {

        List<String> allowedRoles = Arrays.asList("USER", "ADMIN");

        Map<String, Object> decodedToken = tokenFactory.decodeToken(tokenValue);
        String oauth2Id = (String) decodedToken.get("oauth2Id");

        Boolean isChecked = tokenFactory.checkToken(tokenValue, allowedRoles);
        Boolean isExpired = tokenFactory.isExpired(String.valueOf(decodedToken.get("exp")));
        
        if (isChecked && !isExpired){
            System.out.println("당신의 토큰은 이 작업을 실행하기에 유효합니다.");
            
            String sql1 = "SELECT email FROM member WHERE oauth2id = ?";
            String email = jdbcTemplate.queryForObject(sql1, String.class, oauth2Id);
            if (email != null) {
                String sql2 = "DELETE FROM memberhistory WHERE email = ?";
                int rowsAffected = jdbcTemplate.update(sql2, email);
                System.out.println(rowsAffected + " rows deleted.");
            } else {
                System.out.println("사용자 정보를 찾을 수 없습니다.");
            }
        } 
    }
}
