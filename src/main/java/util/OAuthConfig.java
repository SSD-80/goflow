package util;

public final class OAuthConfig {

    // Google OAuth config values from config.properties
    public static final String CLIENT_ID = CommonUtil.properties.getProperty("oauth.clientId");
    public static final String CLIENT_SECRET = CommonUtil.properties.getProperty("oauth.clientSecret");
    public static final String REDIRECT_URI = CommonUtil.properties.getProperty("oauth.redirectUri");

    public static final String AUTHZ_ENDPOINT = CommonUtil.properties.getProperty("oauth.authzEndpoint");
    public static final String TOKEN_ENDPOINT = CommonUtil.properties.getProperty("oauth.tokenEndpoint");
    public static final String USERINFO_ENDPOINT = CommonUtil.properties.getProperty("oauth.userInfoEndpoint");
    public static final String ISSUER = CommonUtil.properties.getProperty("oauth.issuer");
    public static final String SCOPE = CommonUtil.properties.getProperty("oauth.scope");

    private OAuthConfig() {
        // prevent instantiation
    }
}
