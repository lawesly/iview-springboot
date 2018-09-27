//package com.bootdo.system.shiro;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
//import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
//import org.apache.shiro.web.servlet.AdviceFilter;
//import org.apache.shiro.web.util.WebUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.bootdo.common.utils.StringUtils;
//import com.bootdo.system.domain.UserDO;
//
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.web.util.WebUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//
//public class ShiroLoginFilter extends AuthenticatingFilter {
//
//    //TODO - complete JavaDoc
//
//    public static final String DEFAULT_ERROR_KEY_ATTRIBUTE_NAME = "shiroLoginFailure";
//
//    public static final String DEFAULT_USERNAME_PARAM = "username";
//    public static final String DEFAULT_PASSWORD_PARAM = "password";
//    public static final String DEFAULT_REMEMBER_ME_PARAM = "rememberMe";
//
//    private static final Logger log = LoggerFactory.getLogger(FormAuthenticationFilter.class);
//
//    private String usernameParam = DEFAULT_USERNAME_PARAM;
//    private String passwordParam = DEFAULT_PASSWORD_PARAM;
//    private String rememberMeParam = DEFAULT_REMEMBER_ME_PARAM;
//
//    private String failureKeyAttribute = DEFAULT_ERROR_KEY_ATTRIBUTE_NAME;
//
//    public FormAuthenticationFilter() {
//        setLoginUrl(DEFAULT_LOGIN_URL);
//    }
//
//    @Override
//    public void setLoginUrl(String loginUrl) {
//        String previous = getLoginUrl();
//        if (previous != null) {
//            this.appliedPaths.remove(previous);
//        }
//        super.setLoginUrl(loginUrl);
//        if (log.isTraceEnabled()) {
//            log.trace("Adding login url to applied paths.");
//        }
//        this.appliedPaths.put(getLoginUrl(), null);
//    }
//
//    public String getUsernameParam() {
//        return usernameParam;
//    }
//
//    /**
//     * Sets the request parameter name to look for when acquiring the username.  Unless overridden by calling this
//     * method, the default is <code>username</code>.
//     *
//     * @param usernameParam the name of the request param to check for acquiring the username.
//     */
//    public void setUsernameParam(String usernameParam) {
//        this.usernameParam = usernameParam;
//    }
//
//    public String getPasswordParam() {
//        return passwordParam;
//    }
//
//    /**
//     * Sets the request parameter name to look for when acquiring the password.  Unless overridden by calling this
//     * method, the default is <code>password</code>.
//     *
//     * @param passwordParam the name of the request param to check for acquiring the password.
//     */
//    public void setPasswordParam(String passwordParam) {
//        this.passwordParam = passwordParam;
//    }
//
//    public String getRememberMeParam() {
//        return rememberMeParam;
//    }
//
//    /**
//     * Sets the request parameter name to look for when acquiring the rememberMe boolean value.  Unless overridden
//     * by calling this method, the default is <code>rememberMe</code>.
//     * <p/>
//     * RememberMe will be <code>true</code> if the parameter value equals any of those supported by
//     * {@link org.apache.shiro.web.util.WebUtils#isTrue(javax.servlet.ServletRequest, String) WebUtils.isTrue(request,value)}, <code>false</code>
//     * otherwise.
//     *
//     * @param rememberMeParam the name of the request param to check for acquiring the rememberMe boolean value.
//     */
//    public void setRememberMeParam(String rememberMeParam) {
//        this.rememberMeParam = rememberMeParam;
//    }
//
//    public String getFailureKeyAttribute() {
//        return failureKeyAttribute;
//    }
//
//    public void setFailureKeyAttribute(String failureKeyAttribute) {
//        this.failureKeyAttribute = failureKeyAttribute;
//    }
//
//    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        if (isLoginRequest(request, response)) {
//            if (isLoginSubmission(request, response)) {
//                if (log.isTraceEnabled()) {
//                    log.trace("Login submission detected.  Attempting to execute login.");
//                }
//                return executeLogin(request, response);
//            } else {
//                if (log.isTraceEnabled()) {
//                    log.trace("Login page view.");
//                }
//                //allow them to see the login page ;)
//                return true;
//            }
//        } else {
//            if (log.isTraceEnabled()) {
//                log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
//                        "Authentication url [" + getLoginUrl() + "]");
//            }
//
//            saveRequestAndRedirectToLogin(request, response);
//            return false;
//        }
//    }
//
//    /**
//     * This default implementation merely returns <code>true</code> if the request is an HTTP <code>POST</code>,
//     * <code>false</code> otherwise. Can be overridden by subclasses for custom login submission detection behavior.
//     *
//     * @param request  the incoming ServletRequest
//     * @param response the outgoing ServletResponse.
//     * @return <code>true</code> if the request is an HTTP <code>POST</code>, <code>false</code> otherwise.
//     */
//    @SuppressWarnings({"UnusedDeclaration"})
//    protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
//        return (request instanceof HttpServletRequest) && WebUtils.toHttp(request).getMethod().equalsIgnoreCase(POST_METHOD);
//    }
//
//    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
//        String username = getUsername(request);
//        String password = getPassword(request);
//        return createToken(username, password, request, response);
//    }
//
//    protected boolean isRememberMe(ServletRequest request) {
//        return WebUtils.isTrue(request, getRememberMeParam());
//    }
//
//    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
//                                     ServletRequest request, ServletResponse response) throws Exception {
//        issueSuccessRedirect(request, response);
//        //we handled the success redirect directly, prevent the chain from continuing:
//        return false;
//    }
//
//    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
//                                     ServletRequest request, ServletResponse response) {
//        if (log.isDebugEnabled()) {
//            log.debug( "Authentication exception", e );
//        }
//        setFailureAttribute(request, e);
//        //login failed, let request continue back to the login page:
//        return true;
//    }
//
//    protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
//        String className = ae.getClass().getName();
//        request.setAttribute(getFailureKeyAttribute(), className);
//    }
//
//    protected String getUsername(ServletRequest request) {
//        return WebUtils.getCleanParam(request, getUsernameParam());
//    }
//
//    protected String getPassword(ServletRequest request) {
//        return WebUtils.getCleanParam(request, getPasswordParam());
//    }
//
//
//}
