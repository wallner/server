package org.osiam.security.authorization

import org.osiam.resources.ClientSpring
import org.osiam.security.authentication.ClientDetailsLoadingBean
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.AuthorizationRequest
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 09.07.13
 * Time: 08:52
 * To change this template use File | Settings | File Templates.
 */
class OsiamUserApprovalHandlerTest extends Specification {

    def clientDetailsLoadingBean = Mock(ClientDetailsLoadingBean)
    def osiamUserApprovalHandler = new OsiamUserApprovalHandler(clientDetailsLoadingBean: clientDetailsLoadingBean)
    def authorizationRequestMock = Mock(AuthorizationRequest)
    def authenticationMock = Mock(Authentication)

    def "should only add approval date if it is the correct state and user approved successfully"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId("example-client") >> clientMock
        def approvalParams = ['user_oauth_approval':'true']
        authorizationRequestMock.getApprovalParameters() >> approvalParams
        clientMock.getValidityInSeconds() >> 1337

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)

        then:
        clientMock.getExpiry() <= new Date(System.currentTimeMillis() + (1337 * 1000))
        1 * clientDetailsLoadingBean.updateClient(clientMock, 'example-client')
        true
    }

    def "should not add approval date if approval param map is empty and not containing key user_oauth_approval"() {
        given:
        def approvalParams = [:]
        authorizationRequestMock.getApprovalParameters() >> approvalParams

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)

        then:
        0 * clientDetailsLoadingBean.updateClient(_, _)
        true
    }

    def "should not add approval date if user denies approval"() {
        given:
        def approvalParams = ['user_oauth_approval':'false']
        authorizationRequestMock.getApprovalParameters() >> approvalParams

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)

        then:
        0 * 0 * clientDetailsLoadingBean.updateClient(_, _)
        true
    }

    def "should return true if implicit is configured as true to not ask user for approval"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId('example-client') >> clientMock
        clientMock.isImplicit() >> true

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        result
    }

    def "should return true if expiry date is valid and user approved client already and must not approve it again"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId('example-client') >> clientMock
        clientMock.getExpiry() >> new Date(System.currentTimeMillis() + (1337 * 1000))

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        result
    }

    def "should return false if implicit is not configured and user never approved the client before"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId('example-client') >> clientMock
        clientMock.isImplicit() >> false
        clientMock.getExpiry() >> null

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        !result
    }

    def "should return false if implicit is not configured and user approval date is expired"() {
        given:
        def clientMock = Mock(ClientSpring)
        authorizationRequestMock.getClientId() >> 'example-client'
        clientDetailsLoadingBean.loadClientByClientId('example-client') >> clientMock
        clientMock.isImplicit() >> false
        clientMock.getExpiry() >> new Date(System.currentTimeMillis() - 100000)

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        !result
    }
}