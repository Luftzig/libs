package me.libs.server.security

import ratpack.http.Headers

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION

/**
 * @author Noam Y. Tenne
 */
class ApiBasicAuthentication {

    private SecurityService securityService

    ApiBasicAuthentication(SecurityService securityService) {
        this.securityService = securityService
    }

    public Subject resolve(Headers headers) {
        if (!headers.contains(AUTHORIZATION)) {
            return Subject.ANYONYMOUS
        }
        def authorization = headers.get(AUTHORIZATION)
        if (!authorization.startsWith('Basic ')) {
            return Subject.ANYONYMOUS
        }

        authorization = authorization - 'Basic '
        def decodedAuth = new String(authorization.decodeBase64())
        def splitDecodedAuth = decodedAuth.split('\\:')
        if (splitDecodedAuth.size() != 2) {
            return Subject.ANYONYMOUS
        }

        securityService.loginApiKey(splitDecodedAuth[0], splitDecodedAuth[1])
    }
}
