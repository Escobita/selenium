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

<f:view>

   <h:form>
       <style type="text/css">
            .ajaxListItem {}
            .ajaxList {}
       </style>
     <h:panelGrid columns="2">
       <h:outputText value="default suggest"/>
       <s:inputSuggestAjax suggestedItemsMethod="#{inputSuggestAjax.getItems}" styleLocation="" />

       <h:outputText value="suggest with limited suggested items"/>
       <s:inputSuggestAjax suggestedItemsMethod="#{inputSuggestAjax.getItems}" styleLocation="" maxSuggestedItems="2" />

      </h:panelGrid>
   </h:form>

</f:view>

<%@include file="inc/page_footer.jsp" %>

</body>

</html>

