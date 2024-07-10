package org.springframework.boot.autoconfigure.security.oauth2.client;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionException;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientPropertiesRegistrationAdapter.class */
public final class OAuth2ClientPropertiesRegistrationAdapter {
    private OAuth2ClientPropertiesRegistrationAdapter() {
    }

    public static Map<String, ClientRegistration> getClientRegistrations(OAuth2ClientProperties properties) {
        Map<String, ClientRegistration> clientRegistrations = new HashMap<>();
        properties.getRegistration().forEach(key, value -> {
            ClientRegistration clientRegistration = (ClientRegistration) clientRegistrations.put(key, getClientRegistration(key, value, properties.getProvider()));
        });
        return clientRegistrations;
    }

    private static ClientRegistration getClientRegistration(String registrationId, OAuth2ClientProperties.Registration properties, Map<String, OAuth2ClientProperties.Provider> providers) {
        ClientRegistration.Builder builder = getBuilderFromIssuerIfPossible(registrationId, properties.getProvider(), providers);
        if (builder == null) {
            builder = getBuilder(registrationId, properties.getProvider(), providers);
        }
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        properties.getClass();
        PropertyMapper.Source from = map.from(this::getClientId);
        ClientRegistration.Builder builder2 = builder;
        builder2.getClass();
        from.to(this::clientId);
        properties.getClass();
        PropertyMapper.Source from2 = map.from(this::getClientSecret);
        ClientRegistration.Builder builder3 = builder;
        builder3.getClass();
        from2.to(this::clientSecret);
        properties.getClass();
        PropertyMapper.Source as = map.from(this::getClientAuthenticationMethod).as(ClientAuthenticationMethod::new);
        ClientRegistration.Builder builder4 = builder;
        builder4.getClass();
        as.to(this::clientAuthenticationMethod);
        properties.getClass();
        PropertyMapper.Source as2 = map.from(this::getAuthorizationGrantType).as(AuthorizationGrantType::new);
        ClientRegistration.Builder builder5 = builder;
        builder5.getClass();
        as2.to(this::authorizationGrantType);
        properties.getClass();
        PropertyMapper.Source from3 = map.from(this::getRedirectUri);
        ClientRegistration.Builder builder6 = builder;
        builder6.getClass();
        from3.to(this::redirectUriTemplate);
        properties.getClass();
        PropertyMapper.Source as3 = map.from(this::getScope).as(scope -> {
            return StringUtils.toStringArray(scope);
        });
        ClientRegistration.Builder builder7 = builder;
        builder7.getClass();
        as3.to(this::scope);
        properties.getClass();
        PropertyMapper.Source from4 = map.from(this::getClientName);
        ClientRegistration.Builder builder8 = builder;
        builder8.getClass();
        from4.to(this::clientName);
        return builder.build();
    }

    private static ClientRegistration.Builder getBuilderFromIssuerIfPossible(String registrationId, String configuredProviderId, Map<String, OAuth2ClientProperties.Provider> providers) {
        OAuth2ClientProperties.Provider provider;
        String issuer;
        String providerId = configuredProviderId != null ? configuredProviderId : registrationId;
        if (providers.containsKey(providerId) && (issuer = (provider = providers.get(providerId)).getIssuerUri()) != null) {
            String cleanedIssuer = cleanIssuerPath(issuer);
            ClientRegistration.Builder builder = ClientRegistrations.fromOidcIssuerLocation(cleanedIssuer).registrationId(registrationId);
            return getBuilder(builder, provider);
        }
        return null;
    }

    private static String cleanIssuerPath(String issuer) {
        if (issuer.endsWith("/")) {
            return issuer.substring(0, issuer.length() - 1);
        }
        return issuer;
    }

    private static ClientRegistration.Builder getBuilder(String registrationId, String configuredProviderId, Map<String, OAuth2ClientProperties.Provider> providers) {
        String providerId = configuredProviderId != null ? configuredProviderId : registrationId;
        CommonOAuth2Provider provider = getCommonProvider(providerId);
        if (provider == null && !providers.containsKey(providerId)) {
            throw new IllegalStateException(getErrorMessage(configuredProviderId, registrationId));
        }
        ClientRegistration.Builder builder = provider != null ? provider.getBuilder(registrationId) : ClientRegistration.withRegistrationId(registrationId);
        if (providers.containsKey(providerId)) {
            return getBuilder(builder, providers.get(providerId));
        }
        return builder;
    }

    private static String getErrorMessage(String configuredProviderId, String registrationId) {
        return configuredProviderId != null ? "Unknown provider ID '" + configuredProviderId + "'" : "Provider ID must be specified for client registration '" + registrationId + "'";
    }

    private static ClientRegistration.Builder getBuilder(ClientRegistration.Builder builder, OAuth2ClientProperties.Provider provider) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        provider.getClass();
        PropertyMapper.Source from = map.from(this::getAuthorizationUri);
        builder.getClass();
        from.to(this::authorizationUri);
        provider.getClass();
        PropertyMapper.Source from2 = map.from(this::getTokenUri);
        builder.getClass();
        from2.to(this::tokenUri);
        provider.getClass();
        PropertyMapper.Source from3 = map.from(this::getUserInfoUri);
        builder.getClass();
        from3.to(this::userInfoUri);
        provider.getClass();
        PropertyMapper.Source as = map.from(this::getUserInfoAuthenticationMethod).as(AuthenticationMethod::new);
        builder.getClass();
        as.to(this::userInfoAuthenticationMethod);
        provider.getClass();
        PropertyMapper.Source from4 = map.from(this::getJwkSetUri);
        builder.getClass();
        from4.to(this::jwkSetUri);
        provider.getClass();
        PropertyMapper.Source from5 = map.from(this::getUserNameAttribute);
        builder.getClass();
        from5.to(this::userNameAttributeName);
        return builder;
    }

    private static CommonOAuth2Provider getCommonProvider(String providerId) {
        try {
            return (CommonOAuth2Provider) ApplicationConversionService.getSharedInstance().convert(providerId, CommonOAuth2Provider.class);
        } catch (ConversionException e) {
            return null;
        }
    }
}