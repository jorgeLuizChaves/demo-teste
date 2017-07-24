<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletSession" %>

<!-- START Login Google -->
<script src="https://apis.google.com/js/platform.js"></script> 
<script src="https://apis.google.com/js/api:client.js"></script>
<script src="https://apis.google.com/js/client:platform.js?onload=startApp"></script> 
<!-- END Login Google -->

<liferay-theme:defineObjects />

<portlet:defineObjects />

<portlet:actionURL name="doLogin" var="doLoginURL" />

	<portlet:actionURL name="redirectLogoutAction" var="redirectLogoutURL" />
	<portlet:actionURL name="redirectAction" var="redirectURL" />
	
	<liferay-ui:error key="ERROR" message="Dados incorretos tente novamente." />
	<liferay-ui:error key="ERROR_INVALID_USER" message="Usuario invalido." />
	<liferay-ui:error key="ERROR_NO_ADMIN" message="Usuario sem permissão para logar." />
	
	<c:choose>
		<c:when test="<%= themeDisplay.isSignedIn() && themeDisplay.getUser().getFirstName().equals("liferay")%>">
			<aui:form action="<%=redirectURL%>" method="post" id="redirectform" name="redirectform"	cssClass="form-redirect" data-form='form-redirect-sky'>
				<div class="row title-page">
					<div class="col-xs-12">
						<p>
							<aui:input data-form="txtRedirect-sky" name="Redirect" required="true" value="/" type="hidden"></aui:input>	
							<button type=submit name=btnRedirect id=btnRedirect data-form="btnRedirect" hidden=hidden>Home Redirect</button>
						</p>
					</div>
				</div>
			</aui:form>
			<script>
				document.querySelector("*[data-form='form-redirect-sky']").submit();
			</script>
		</c:when>
		<c:otherwise>
				
					<aui:form action="<%=doLoginURL%>" method="post" id="signinform" name="signinform"	cssClass="form-signin" data-form='form-signin-sky'>
							<div id="divFormLoginSky" class="row" visibility="hidden" >
								<aui:input data-form="txtLogin-sky" name="Login" required="true" value="liferay@sky.com" type="hidden"></aui:input>
								<aui:input data-form="txtSenha-sky" name="Senha" required="true" value="liferay@sky" type="hidden"></aui:input>	
								<aui:input data-form="txtUserType-sky" name="UserType" required="true" value="customer" type="hidden"></aui:input>	
								<aui:input data-form="txtRedirect-sky" name="Redirect" required="true" value="/home" type="hidden"></aui:input>	
								<aui:button data-form="btnLogin-sky" type="button" value="Entrar" style="display:none" hidden="hidden"></aui:button>
								<aui:input data-form="SKY_USER_FIRSTNAME" name="SKY_USER_FIRSTNAME" type="hidden" ></aui:input>
								<aui:input data-form="SKY_USER_LASTNAME" name="SKY_USER_LASTNAME" type="hidden" ></aui:input>
								<aui:input data-form="SKY_USER_EMAIL" name="SKY_USER_EMAIL" type="hidden" ></aui:input>
								<aui:input data-form="SKY_USER_LOGIN" name="SKY_USER_LOGIN" type="hidden" ></aui:input>
								<aui:input data-form="SKY_USER_PASSWORD" name="SKY_USER_PASSWORD" type="hidden" ></aui:input>
								<aui:input data-form="SKY_USER_BIRTHDATE_DAY" name="SKY_USER_BIRTHDATE_DAY" type="hidden" ></aui:input>
								<aui:input data-form="SKY_USER_BIRTHDATE_MONTH" name="SKY_USER_BIRTHDATE_MONTH" type="hidden" ></aui:input>
								<aui:input data-form="SKY_USER_BIRTHDATE_YEAR" name="SKY_USER_BIRTHDATE_YEAR" type="hidden" ></aui:input>
								<aui:input data-form="SKY_USER_OPTIN_AMIGO" name="SKY_USER_OPTIN_AMIGO" type="hidden"></aui:input>
								<aui:input data-form="SKY_USER_NEWSLETTER" name="SKY_USER_NEWSLETTER" type="hidden"></aui:input>			
							</div>
					</aui:form>

		</c:otherwise>
	</c:choose>		
