<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://myfaces.apache.org/sandbox" prefix="s"%>

<html>

<%@include file="inc/head.inc" %>

<!--
/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//-->

<body>

<!--
managed beans used:
    validateForm
-->

<f:view>

    <f:loadBundle basename="org.apache.myfaces.examples.resource.example_messages" var="example_messages"/>

    <h:panelGroup id="body">

                <h:form id="form1" name="validateForm">
                   <h:panelGrid columns="3">

                                <h:outputLabel for="email" value="#{example_messages['validate_email']}" />
            <h:inputText id="email" value="#{validateForm.email}" required="true">
                <f:validator validatorId="org.apache.myfaces.validator.Email"/>
            </h:inputText>
                                <x:message id="emailError" for="email" styleClass="error" />

                                <h:outputLabel for="email2" value="#{example_messages['validate_email']}2" />
            <h:inputText id="email2" value="#{validateForm.email2}" required="true">
                <x:validateEmail />
            </h:inputText>
                                <x:message id="emailError2" for="email2" styleClass="error" />

                                <h:outputLabel for="creditCardNumber" value="#{example_messages['validate_credit']}" />
            <h:inputText id="creditCardNumber" value="#{validateForm.creditCardNumber}" required="true">
                <x:validateCreditCard />
            </h:inputText>
                                <x:message id="creditCardNumberError" for="creditCardNumber" styleClass="error" />

                               <h:outputLabel for="url" value="#{example_messages['validate_url']}" />
            <h:inputText id="url" value="#{validateForm.url}" required="true">
                <s:validateUrl />
            </h:inputText>            
                                <x:message id="urlError" for="url" styleClass="error" />

                 <h:outputLabel for="regExprValue" value="#{example_messages['validate_regexp']}" />
            <h:inputText id="regExprValue" value="#{validateForm.regExpr}" required="true">
                <x:validateRegExpr pattern='\d{5}' />
            </h:inputText>
                 <x:message id="regExprValueError" for="regExprValue" styleClass="error" />

                                <h:outputLabel for="equal" value="#{example_messages['validate_equal']}" />
            <h:inputText id="equal" value="#{validateForm.equal}" required="true"/>
                                <x:message id="equalError" for="equal" styleClass="error" />

                                <h:outputLabel for="equal2" value="#{example_messages['validate_equal']}2" />
            <h:inputText id="equal2" value="#{validateForm.equal2}" required="true">
                <x:validateEqual for="equal" />
            </h:inputText>
                                <x:message id="equal2Error" for="equal2" styleClass="error" />

            <h:panelGroup/>
                            <h:commandButton id="validateButton" value="#{example_messages['button_submit']}" action="#{validateForm.submit}"/>
            <h:panelGroup/>

                    </h:panelGrid>
                </h:form>

    </h:panelGroup>

</f:view>

<%@include file="inc/page_footer.jsp" %>

</body>

</html>
