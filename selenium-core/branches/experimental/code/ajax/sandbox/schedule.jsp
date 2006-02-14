<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>
<%@ taglib uri="http://myfaces.apache.org/sandbox" prefix="s"%>

<!--
/*
 * Copyright 2005 The Apache Software Foundation.
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
<html>

    <%@include file="inc/head.inc" %>

    <body>
        <f:view>
         <h:form>
            <h:messages
                tooltip="true"
                id="scheduleMessages"
                layout="table"
                globalOnly="false"/>
                
            <table cellpadding="5" border="0">
                <tr>
                    <td valign="top">
                        <h:panelGrid cellpadding="0" cellspacing="5" columns="1">
                            <x:inputCalendar
                                id="scheduleNavigator"
                                monthYearRowClass="yearMonthHeader"
                                weekRowClass="weekHeader"
                                currentDayCellClass="currentDayCell"
                                value="#{scheduleBean.date}"
                                valueChangeListener="#{scheduleBean.calendarValueChanged}"
                                binding="#{scheduleBean.dateInput}"/>
                            <h:outputLabel
                                for="modeInput"
                                id="modeLabel"
                                value="mode:"/>
                            <h:selectOneMenu
                                id="modeInput"
                                binding="#{scheduleBean.modeInput}"
                                immediate="true"
                                valueChangeListener="#{scheduleBean.modeValueChanged}"
                                onchange="this.form.submit();"
                                value="#{scheduleBean.mode}">
                                <f:selectItems
                                    id="modeItems"
                                    value="#{scheduleBean.modeItems}"/>
                            </h:selectOneMenu>
                            <h:outputLabel
                                for="themeInput"
                                id="themeLabel"
                                value="theme:" />
                            <h:selectOneMenu
                                id="themeInput"
                                immediate="true"
                                onchange="this.form.submit();"
                                value="#{scheduleBean.theme}">
                                <f:selectItem
                                    itemValue="default"
                                    itemLabel="default"/>
                                <f:selectItem
                                    itemValue="outlookxp"
                                    itemLabel="Outlook XP"/>
                                <f:selectItem
                                    itemValue="evolution"
                                    itemLabel="Evolution"/>
                            </h:selectOneMenu>
                            <h:outputLabel
                                for="readonlyInput"
                                id="readonlyLabel"
                                value="read-only:" />
                            <h:selectBooleanCheckbox
                                id="readonlyInput"
                                value="#{scheduleBean.readonly}"
                                valueChangeListener="#{scheduleBean.readonlyValueChanged}"
                                immediate="true"
                                onchange="this.form.submit();"
                                title="read-only?"/>
                            <h:outputLabel
                                for="tooltipInput"
                                id="tooltipLabel"
                                value="show tooltips:" />
                            <h:selectBooleanCheckbox
                                id="tooltipInput"
                                value="#{scheduleBean.tooltip}"
                                immediate="true"
                                onchange="this.form.submit();"
                                title="show tooltips?"/>
                        </h:panelGrid>
                    </td>
                    <td width="100%">
                        <s:schedule
                            value="#{scheduleBean.model}"
                            id="schedule1"
                            rendered="true"
                            visibleEndHour="18"
                            visibleStartHour="8"
                            workingEndHour="17"
                            workingStartHour="9"
                            readonly="#{scheduleBean.readonly}"
                            actionListener="#{scheduleBean.scheduleActionPerformed}"
                            tooltip="#{scheduleBean.tooltip}"
                            theme="#{scheduleBean.theme}"/>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <h3><h:outputText value="add appointment"/></h3>
                    </td>
                    <td>
                        <h:panelGrid cellpadding="0" cellspacing="0" columns="2">
                            <h:outputLabel
                                for="appStart"
                                value="start:"/>
                            <x:inputDate
                                id="appStart"
                                value="#{appointmentBean.appointmentStart}"
                                type="both"/>
                            <h:outputLabel
                                for="appEnd"
                                value="end:"/>
                            <x:inputDate
                                id="appEnd"
                                value="#{appointmentBean.appointmentEnd}"
                                type="both"/>
                            <h:outputLabel
                                for="appTitle"
                                value="title:"/>
                            <h:inputText
                                size="60"
                                id="appTitle"
                                value="#{appointmentBean.appointmentTitle}"/>
                            <h:outputLabel
                                for="appLocation"
                                value="location:"/>
                            <h:inputText
                                size="60"
                                id="appLocation"
                                value="#{appointmentBean.appointmentLocation}"/>
                            <h:outputLabel
                                for="appComments"
                                value="comments:"/>
                            <h:inputTextarea
                                cols="60"
                                rows="5"
                                id="appComments"
                                value="#{appointmentBean.appointmentComments}"/>
                            <h:commandButton
                                value="Add appointment"
                                actionListener="#{appointmentBean.addAppointment}"/>
                        </h:panelGrid>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <h3><h:outputText value="remove appointment"/></h3>
                    </td>
                    <td>
                        <h:commandButton
                            value="Remove selected appointment"
                            actionListener="#{appointmentBean.removeAppointment}"
                            disabled="#{!scheduleBean.entrySelected}"/>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <h3><h:outputText value="add holiday"/></h3>
                    </td>
                    <td>
                        <h:panelGrid cellpadding="0" cellspacing="0" columns="2">
                            <h:outputLabel
                                for="holidayDate"
                                value="date:"/>
                            <x:inputDate
                                id="holidayDate"
                                value="#{appointmentBean.holidayDate}"
                                type="date"/>
                            <h:outputLabel
                                for="holidayName"
                                value="name:"/>
                            <h:inputText
                                size="60"
                                id="holidayName"
                                value="#{appointmentBean.holidayName}"/>
                            <h:commandButton
                                value="Add holiday"
                                actionListener="#{appointmentBean.addHoliday}"/>
                        </h:panelGrid>
                    </td>
                </tr>
            </table>

         </h:form>
        </f:view>
    </body>

    <%@include file="inc/page_footer.jsp" %>

</html>
