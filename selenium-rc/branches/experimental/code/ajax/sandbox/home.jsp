<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<html>

    <%@include file="inc/head.inc" %>

    <f:view>

        <f:loadBundle basename="org.apache.myfaces.examples.resource.example_messages" var="example_messages"/>

        <h:panelGrid>
            <h:panelGrid id="header_group1" columns="2" styleClass="pageHeader"  >
                <t:graphicImage id="header_logo" url="images/logo_mini.jpg" alt="#{example_messages['alt_logo']}" />
                <f:verbatim>
                    <h:outputText style="font-size:20px;color:#FFFFFF;" escape="false" value="MyFaces - The free JavaServer&#8482; Faces Implementation"/>
                    <h:outputText style="font-size:10px;color:#FFFFFF;"value="(Version 1.1.1)"/>
                </f:verbatim>
            </h:panelGrid>

            <h:outputLink value="inputSuggestAjax.jsf" ><f:verbatim>InputSuggestAjax</f:verbatim></h:outputLink>
            <h:outputLink value="inputSuggest.jsf" ><f:verbatim>Input Suggest</f:verbatim></h:outputLink>
            <h:outputLink value="schedule.jsf" ><f:verbatim>Schedule</f:verbatim></h:outputLink>
            <h:outputLink value="autoUpdateDataTable.jsf" ><f:verbatim>Automatically updated dataTable per AJAX</f:verbatim></h:outputLink>
            <h:outputLink value="validateUrl.jsf" ><f:verbatim>Validation example 2 - including URL validator</f:verbatim></h:outputLink>
        </h:panelGrid>
    </f:view>

</html>
