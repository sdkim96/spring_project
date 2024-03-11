package web.web1.Oauth.oauth2;

import java.util.Map;

public class KakaoMemberInfo implements OAuth2MemberInfo{
    public KakaoMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    private Map<String, Object> attributes;
    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return (String) properties.get("nickname");
    }
    
    @Override
    public String getEmail() {
        return String.valueOf(attributes.get("id"));
    }
    
}