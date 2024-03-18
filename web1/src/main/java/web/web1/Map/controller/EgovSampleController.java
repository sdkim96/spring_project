package web.web1.Map.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import web.web1.Map.domain.map.AddressRequest;
import web.web1.Map.domain.map.GeoCoding;
import web.web1.Map.domain.searchhistory.SearchHistory;
import web.web1.Map.domain.searchhistory.CafeRecommendSystem;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


@RestController
@RequiredArgsConstructor
public class EgovSampleController {

	private final SearchHistory searchHistory;
	private final CafeRecommendSystem cafeRecommendSystem;

//   @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
  @PostMapping("/map/getaddress")
  public void getAddrApi(HttpServletRequest requestHeader, @RequestBody AddressRequest request, HttpServletResponse response) throws Exception {
    
	// 1. 헤더 및 바디에서 정보 얻기
	String authorizationHeader = requestHeader.getHeader("Authorization");
	String tokenValue = authorizationHeader != null ? authorizationHeader.replace("Bearer ", "") : null;
	System.out.println("토큰값 = " + tokenValue);
	
	System.out.println("도로명 주소 받기 시작");
    System.out.println(request.getCurrentPage());
	System.out.println(request.getConfmKey());
	System.out.println(request.getCountPerPage());
	System.out.println(request.getKeyword());
	System.out.println(request.getResultType());
	System.out.println(request.getProvider());

	//2. 유저의 쿼리를 히스토리에 저장
	if (tokenValue != null){
		searchHistory.createHistory(tokenValue, request.getKeyword());
	} else {
		System.out.println("토큰값이 없습니다.");
	
	}

	//3. API 호출해서 위치정보 json 받기
    String apiUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do?currentPage=" + request.getCurrentPage() +
                    "&countPerPage=" + request.getCountPerPage() + "&keyword=" + URLEncoder.encode(request.getKeyword(), "UTF-8") +
                    "&confmKey=" + request.getConfmKey() + "&resultType=" + request.getResultType();
    
	System.out.println("apiurl:"+apiUrl);

	URL url = new URL(apiUrl);
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod("GET"); // 외부 API에 따라 GET 또는 POST로 설정합니다.
	conn.setRequestProperty("Accept", "application/json");

	if (conn.getResponseCode() != 200) {
		throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	}

	BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));
	StringBuilder sb = new StringBuilder();
	String output;
	while ((output = br.readLine()) != null) {
		sb.append(output);
	}
	conn.disconnect();

	response.setCharacterEncoding("UTF-8");
	response.setContentType("application/json");
	response.getWriter().write(sb.toString()); // 외부 API로부터 받은 응답을 클라이언트로 전송
  }


//   @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@PostMapping("/map/getgeocoding")
public void getGeoCoding(HttpServletRequest requestHeader, @RequestBody GeoCoding request, HttpServletResponse response) throws Exception {
    System.out.println("지오코딩 시작");
    
    String authorizationHeader = requestHeader.getHeader("Authorization");
    String tokenValue = authorizationHeader != null ? authorizationHeader.replace("Bearer ", "") : null;
    
    System.out.println("토큰값 = " + tokenValue);
    System.out.println(request.getAddress());
    
    String reqUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" 
    + URLEncoder.encode(request.getAddress(), "UTF-8");
    String geoCodingKey = "AIzaSyBGHONO0r5jq-uNwYhQOcQ_IInfj3exhqo";
    String geoCodingUrl = reqUrl + "&key=" + geoCodingKey;
    System.out.println(geoCodingUrl);

    URL url2 = new URL(geoCodingUrl);
    HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Accept", "application/json");

    if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
    }

    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));
    StringBuilder sb = new StringBuilder();
    String output;
    while ((output = br.readLine()) != null) {
        sb.append(output);
    }
    conn.disconnect();

    String responseString = sb.toString();
    System.out.println("ResponseString :" +responseString);

    // 토큰 값이 존재하는 경우에만 실행
    if (tokenValue != null && !tokenValue.isEmpty()) {
        try {
            JSONObject jsonResponse = new JSONObject(responseString);
            JSONArray results = jsonResponse.getJSONArray("results");
            if(results.length() > 0) { // 결과가 있는지 확인
                JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                
                // 여기에서 추천 시스템 호출 등의 로직 실행
                cafeRecommendSystem.createRecommend(tokenValue, lat, lng);
            }
        } catch (JSONException e) {
            // JSON 처리 중 발생할 수 있는 예외 처리
            System.out.println("JSON 파싱 중 오류가 발생했습니다: " + e.getMessage());
        } catch (NumberFormatException e) {
            // 숫자 변환 중 발생할 수 있는 예외 처리
            System.out.println("숫자 변환 중 오류가 발생했습니다: " + e.getMessage());
        }
    } else {
        System.out.println("토큰값이 유효하지 않습니다.");
    }
    
    // 응답을 클라이언트로 전송하는 부분은 토큰 유무와 무관하게 실행
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    response.getWriter().write(responseString);
}


}
