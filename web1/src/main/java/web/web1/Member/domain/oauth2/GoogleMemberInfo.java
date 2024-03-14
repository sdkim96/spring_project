package web.web1.Member.domain.oauth2;

import java.util.Map;

import web.web1.Member.domain.oauth2.OAuth2MemberInfo;

public class GoogleMemberInfo implements OAuth2MemberInfo{
    public GoogleMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    private Map<String, Object> attributes;
    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}