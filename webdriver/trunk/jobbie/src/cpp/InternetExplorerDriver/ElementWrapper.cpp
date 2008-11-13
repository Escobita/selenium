#include "StdAfx.h"

#include "utils.h"
#include "InternalCustomMessage.h"



using namespace std;

ElementWrapper::ElementWrapper(InternetExplorerDriver* ie, IHTMLElement *pElem)
	: pElement(pElem)
{
	this->ie = ie;
}


ElementWrapper::~ElementWrapper()
{
}

DataMarshaller& ElementWrapper::commandData()
{
	return ie->p_IEthread->getCmdData();
}

LPCWSTR ElementWrapper::getElementName()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETELEMENTNAME,)
	return data.output_string_.c_str();
}

LPCWSTR ElementWrapper::getAttribute(LPCWSTR name)
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETATTRIBUTE, name)
	return data.output_string_.c_str();
}

LPCWSTR ElementWrapper::getValue()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETVALUE,)
	return data.output_string_.c_str();
}


void ElementWrapper::sendKeys(LPCWSTR newValue)
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_SENDKEYS, newValue)

	if(data.output_bool_)
	{
		throw data.output_string_;
	}
}

void ElementWrapper::clear()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_CLEAR,)
}

bool ElementWrapper::isSelected()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_ISSELECTED,)
	return data.output_bool_;
}

void ElementWrapper::setSelected()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_SETSELECTED,)

	if(data.output_bool_)
	{
		safeIO::CoutA("Error in ElementWrapper::setSelected with exceptionThrown", true);
		throw data.output_string_;
	}
}

bool ElementWrapper::isEnabled()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_ISENABLED,)
	return data.output_bool_;
}

bool ElementWrapper::isDisplayed()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_ISDISPLAYED,)
	return data.output_bool_;
}

bool ElementWrapper::toggle()
{
	SCOPETRACER
	click();
	return isSelected();
}

void ElementWrapper::getLocationOnceScrolledIntoView(long* x, long* y) 
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETLOCATIONONCESCROLLEDINTOVIEW,)

	VARIANT result = data.output_variant_;
	
	if (result.vt != VT_ARRAY) {
		*x = 0;
		*y = 0;
		return;
	}

	SAFEARRAY* ary = result.parray;
	long index = 0;
	CComVariant xVariant;
	SafeArrayGetElement(ary, &index, (void*) &xVariant);
	*x = xVariant.lVal;

	CComVariant yVariant;
	index = 1;
	SafeArrayGetElement(ary, &index, (void*) &yVariant);
	*y = yVariant.lVal;

	SafeArrayDestroy(ary);
}

void ElementWrapper::getLocation(long* x, long* y) 
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETLOCATION,)

	VARIANT result = data.output_variant_;
	
	if (result.vt != VT_ARRAY) {
		*x = 0;
		*y = 0;
		return;
	}

	SAFEARRAY* ary = result.parray;
	long index = 0;
	CComVariant xVariant;
	SafeArrayGetElement(ary, &index, (void*) &xVariant);
	*x = xVariant.lVal;

	CComVariant yVariant;
	index = 1;
	SafeArrayGetElement(ary, &index, (void*) &yVariant);
	*y = yVariant.lVal;

	SafeArrayDestroy(ary);
}

long ElementWrapper::getWidth()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETWIDTH,)
	return data.output_long_;
}

long ElementWrapper::getHeight()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETHEIGHT,)
	return data.output_long_;
}

LPCWSTR ElementWrapper::getValueOfCssProperty(LPCWSTR propertyName)
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETVALUEOFCSSPROP, propertyName)
	return data.output_string_.c_str();
}

LPCWSTR ElementWrapper::getText()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETTEXT,)
	return data.output_string_.c_str();
}

void ElementWrapper::click()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_CLICK,)

	if(data.output_bool_)
	{
		throw data.output_string_;
	}
}

void ElementWrapper::submit()
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_SUBMIT,)
}


std::vector<ElementWrapper*>* ElementWrapper::getChildrenWithTagName(LPCWSTR tagName)
{
	SCOPETRACER
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_GETCHILDRENWTAGNAME, tagName)

	std::vector<IHTMLElement*> &allElems = data.output_list_html_element_;

	std::vector<ElementWrapper*> *toReturn = new std::vector<ElementWrapper*>();

	std::vector<IHTMLElement*>::const_iterator cur, end = allElems.end();
	for(cur = allElems.begin();cur < end; cur++)
	{
		IHTMLElement* elem = *cur;
		toReturn->push_back(new ElementWrapper(ie, elem));
	}
	return toReturn;


}

void ElementWrapper::releaseInterface()
{
	///// TODO !!!
	return;
	SEND_MESSAGE_WITH_MARSHALLED_DATA(_WD_ELEM_RELEASE,)
}

/////////////////////////////////////////////////////////////////////

DataMarshaller& ElementWrapper::prepareCmData()
{
	DataMarshaller& data = commandData();
	data.input_html_element_ = pElement;
	return data;
}

DataMarshaller& ElementWrapper::prepareCmData(LPCWSTR str)
{
	DataMarshaller& data = prepareCmData();
	data.input_string_ = str;
	return data;
}

bool ElementWrapper::sendThreadMsg(UINT msg, DataMarshaller& data)
{
	return ie->sendThreadMsg(msg, data);
}


