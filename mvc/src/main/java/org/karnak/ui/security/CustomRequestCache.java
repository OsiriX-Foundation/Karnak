package org.karnak.ui.security;

import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import org.karnak.ui.authentication.LoginScreen;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomRequestCache extends HttpSessionRequestCache {

    /**
     * Saves unauthenticated requests so we can redirect the user to the page they
     * were trying to access once they’re logged in
     *
     * @param request  Request
     * @param response Response
     */
    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if (!SecurityUtils.isFrameworkInternalRequest(request)) {
            super.saveRequest(request, response);
        }
    }

    /**
     * Determine the path to navigate to:
     * Retrieve if existing the saved request or redirect to the root path
     *
     * @return the path resolved
     */
    public String resolveRedirectUrl() {
        SavedRequest savedRequest =
                getRequest(VaadinServletRequest.getCurrent().getHttpServletRequest(), VaadinServletResponse
                        .getCurrent().getHttpServletResponse());
        if (savedRequest instanceof DefaultSavedRequest) {
            final String requestURI = ((DefaultSavedRequest) savedRequest).getRequestURI(); //
            // check for valid URI and prevent redirecting to the login view
            if (requestURI != null && !requestURI.isEmpty() && !requestURI.contains(LoginScreen.ROUTE)) { //
                return requestURI.startsWith("/") ? requestURI.substring(1) : requestURI; //
            }
        }

        // if everything fails, redirect to the main view
        return "";
    }

}