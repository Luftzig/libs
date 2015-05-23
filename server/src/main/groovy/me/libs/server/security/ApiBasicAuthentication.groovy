package me.libs.server.security

import ratpack.handling.Context

/**
 * @author Noam Y. Tenne
 */
class ApiBasicAuthentication {

    private SecurityService securityService

    ApiBasicAuthentication(SecurityService securityService) {
        this.securityService = securityService
    }

    public Subject resolve(Context context) {
        def authorization = context.request.headers.get('Authorization')
        println "Auth = $authorization"
        if (!authorization || !authorization.startsWith('Basic ')) {
            return Subject.ANYONYMOUS
        }

        authorization = authorization - 'Basic '
        def decodedAuth = new String(authorization.decodeBase64())
        def splitDecodedAuth = decodedAuth.split('\\:')
        if (splitDecodedAuth.size() != 2) {
            return Subject.ANYONYMOUS
        }

        println "Our filter is working: ${splitDecodedAuth[0]}-${splitDecodedAuth[1]}"
        securityService.loginApiKey(splitDecodedAuth[0], splitDecodedAuth[1])
    }
}
