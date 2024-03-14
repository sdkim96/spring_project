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
import web.web1.Map.domain.map.AddressRequest;
import web.web1.Map.domain.map.GeoCoding;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class EgovSampleController {
  
//   @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
  @PostMapping("/map/getaddress")
  public void getAddrApi(@RequestBody AddressRequest request, HttpServletResponse response) throws Exception {
    System.out.println("도로명 주소 받기 시작");
    System.out.println(request.getCurrentPage());
	System.out.println(request.getConfmKey());
	System.out.println(request.getCountPerPage());
	System.out.println(request.getKeyword());
	System.out.println(request.getResultType());
	

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
  public void getGeoCoding(@RequestBody GeoCoding request, HttpServletResponse response) throws Exception {
    System.out.println("지오코딩 시작");
    System.out.println(request.getAddress());
	
	String reqUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" 
	+ URLEncoder.encode(request.getAddress(), "UTF-8");

	String geoCodingKey = "AIzaSyBGHONO0r5jq-uNwYhQOcQ_IInfj3exhqo";

	String geoCodingUrl = reqUrl + "&key=" + geoCodingKey;
	System.out.println(geoCodingUrl);

	URL url2 = new URL(geoCodingUrl);

	HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
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

}
