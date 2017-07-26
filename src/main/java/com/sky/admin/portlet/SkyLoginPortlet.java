package com.sky.admin.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, property = { "com.liferay.portlet.display-category=SKY",
		"com.liferay.portlet.instanceable=true", "com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.private-request-attributes=false", "javax.portlet.display-name=Sky Portlet Login",
		"javax.portlet.init-param.template-path=/", "javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class SkyLoginPortlet extends MVCPortlet {

	private static Log _log = LogFactoryUtil.getLog(SkyLoginPortlet.class);

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		super.doView(renderRequest, renderResponse);
	}

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.render(request, response);
	}

	/*
	 * Metodo a ser utilizado pela view do portlet de login, para fazer o login
	 * no liferay
	 */
	@ProcessAction(name = "doLogin")
	public void doLogin(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		LiferayPortletConfig liferayPortletConfig = (LiferayPortletConfig) portletConfig;
		SessionMessages.add(actionRequest,
				liferayPortletConfig.getPortletId() + SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		try {

			// Login
			this.login(themeDisplay, actionRequest, actionResponse);

			// Loads the information of the user to the session
			loadAttributesinSession(actionRequest);

		} catch (Exception e) {
			_log.error("Login Error " + e.toString(), e);
		}
	}

	/*
	 * Metodo a ser utilizado pela view do portlet de login, para fazer o
	 * redirect para o valor informado no campo Redirect da view.
	 */
	@ProcessAction(name = "redirectAction")
	public void redirectAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		String redirect = actionRequest.getParameter("Redirect");

		if (redirect == null)
			actionResponse.sendRedirect("/home");
		else
			actionResponse.sendRedirect(redirect);
	}

	/*
	 * Metodo a ser utilizado pela view do portlet de login, para fazer o
	 * redirect para efetuar o logoff da aplicação.
	 */
	@ProcessAction(name = "redirectLogoutAction")
	public void redirectLogoutAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		actionResponse.sendRedirect(PortalUtil.getPortalURL(actionRequest) + "/c/portal/logout");
	}

	/*
	 * Metodo a ser utilizado para carregar os atributos do cliente na sessão.
	 */
	protected void loadAttributesinSession(ActionRequest actionRequest) {
		HttpSession session = this.getSession(actionRequest);

		session.setAttribute("SKY_USER_FIRSTNAME", ParamUtil.getString(actionRequest, "SKY_USER_FIRSTNAME"));
		session.setAttribute("SKY_USER_LASTNAME", ParamUtil.getString(actionRequest, "SKY_USER_LASTNAME"));
		session.setAttribute("SKY_USER_BIRTHDATE_DAY", ParamUtil.getString(actionRequest, "SKY_USER_BIRTHDATE_DAY"));
		session.setAttribute("SKY_USER_BIRTHDATE_MONTH", ParamUtil.getString(actionRequest, "SKY_USER_BIRTHDATE_MONTH"));
		session.setAttribute("SKY_USER_BIRTHDATE_YEAR", ParamUtil.getString(actionRequest, "SKY_USER_BIRTHDATE_YEAR"));
		session.setAttribute("SKY_USER_OPTIN_AMIGO", ParamUtil.getString(actionRequest, "SKY_USER_OPTIN_AMIGO"));
		session.setAttribute("SKY_USER_NEWSLETTER", ParamUtil.getString(actionRequest, "SKY_USER_NEWSLETTER"));

	}

	private HttpSession getSession(ActionRequest actionRequest) {
		HttpServletRequest servletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
		return servletRequest.getSession();
	}

	/*
	 * Metodo a ser utilizado dentro da classe SkyLoginPortlet, para fazer o
	 * login no liferay.
	 */
	protected void login(ThemeDisplay themeDisplay, ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
		HttpServletResponse response = PortalUtil.getHttpServletResponse(actionResponse);

		boolean rememberMe = ParamUtil.getBoolean(actionRequest, "rememberMe");
		// Verificação se o usuario ja esta logado
		if (!themeDisplay.isSignedIn()) {
			String portletId = PortalUtil.getPortletId(actionRequest);

			PortletPreferences portletPreferences = PortletPreferencesFactoryUtil
					.getStrictPortletSetup(themeDisplay.getLayout(), portletId);

			String authType = portletPreferences.getValue("authType", null);

			// Only admin
			if (actionRequest.getParameter("UserType").equals("admin")) {
				// Verify that the user is registered in Liferay.
				try{
					UserLocalServiceUtil.getUserByEmailAddress(themeDisplay.getCompanyId(),
							ParamUtil.getString(actionRequest, "SKY_USER_EMAIL"));
				}catch(com.liferay.portal.kernel.exception.NoSuchUserException e){
					SessionErrors.add(actionRequest, "ERROR_INVALID_USER"); 
					return;
				}

				try {
					AuthenticatedSessionManagerUtil.login(request, response,
							ParamUtil.getString(actionRequest, "SKY_USER_LOGIN"),
							ParamUtil.getString(actionRequest, "SKY_USER_PASSWORD"), rememberMe, authType);
				} catch( Exception ex) {
					SessionErrors.add(actionRequest, "ERROR"); 
					return;
				}
			}else{
				SessionErrors.add(actionRequest, "ERROR_NO_ADMIN"); 
				return;
			}

		} else {
			actionResponse.sendRedirect(PortalUtil.getPortalURL(actionRequest) + "/c/portal/logout");
		}

		String redirect = ParamUtil.getString(actionRequest, "Redirect");
		_log.info("Trying to redirect to: " + redirect);

		String mainPath = themeDisplay.getPathMain();

		if (Validator.isNotNull(redirect) && PropsValues.PORTAL_JAAS_ENABLE) {
			redirect = mainPath.concat("/portal/protected?redirect=").concat(HttpUtil.encodeURL(redirect));
		}else{
			if (PropsValues.PORTAL_JAAS_ENABLE) {
				redirect = mainPath.concat("/portal/protected");
			}else{
				if (ParamUtil.getBoolean(actionRequest, "doActionAfterLogin")) {
					return;
				}
			}
		}
		_log.info("Going to redirect to: " + redirect);
		actionResponse.sendRedirect(redirect);
	}

	protected void addSuccessMessage(ActionRequest actionRequest, ActionResponse actionResponse) {
		// Do Nothing
	}

	protected String getCompleteRedirectURL(HttpServletRequest request, String redirect) {

		HttpSession session = request.getSession();

		Boolean httpsInitial = (Boolean) session.getAttribute(WebKeys.HTTPS_INITIAL);

		String portalURL = null;

		if (PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS && !PropsValues.SESSION_ENABLE_PHISHING_PROTECTION
				&& (httpsInitial != null) && !httpsInitial.booleanValue()) {

			portalURL = PortalUtil.getPortalURL(request, false);
		} else {
			portalURL = PortalUtil.getPortalURL(request);
		}

		return portalURL.concat(redirect);
	}

}