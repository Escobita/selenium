// This is the main DLL file.

#include "stdafx.h"
#include "utils.h"

using namespace std;

#ifdef __cplusplus
extern "C" {
#endif

	InternetExplorerDriver* g_pStillOpenedIE = NULL;


JNIEXPORT jobject JNICALL Java_org_openqa_selenium_ie_InternetExplorerDriver_doExecuteScript
  (JNIEnv *env, jobject obj, jstring script, jobjectArray args)
{
	TRY
	{
	InternetExplorerDriver* wrapper = NULL; // = getIe(env, obj);

	// Convert the args into something we can use elsewhere.
	jclass numberClazz = env->FindClass("java/lang/Number");
	jclass booleanClazz = env->FindClass("java/lang/Boolean");
	jclass stringClazz = env->FindClass("java/lang/String");
	jclass elementClazz = env->FindClass("org/openqa/selenium/ie/InternetExplorerElement");

	jmethodID longValue = env->GetMethodID(numberClazz, "longValue", "()J");
	jmethodID booleanValue = env->GetMethodID(booleanClazz, "booleanValue", "()Z");
	jfieldID elementPointer = env->GetFieldID(elementClazz, "nodePointer", "J");

	jsize length = env->GetArrayLength(args);

	SAFEARRAYBOUND bounds;
	bounds.cElements = length;
	bounds.lLbound = 0;
	SAFEARRAY* convertedItems = SafeArrayCreate(VT_VARIANT, 1, &bounds);
	
	LONG index[1];
	for (jsize i = 0; i < length; i++) {
		index[0] = i;
		CComVariant dest;

		jobject arrayObject = env->GetObjectArrayElement(args, i);
		jclass objClazz = env->GetObjectClass(arrayObject);
		
		if (env->IsInstanceOf(arrayObject, numberClazz)) {
			jlong value = env->CallLongMethod(arrayObject, longValue);

			dest.vt = VT_I4;
			dest.lVal = (LONG) value;
		} else if (env->IsInstanceOf(arrayObject, stringClazz)) {
			wchar_t *converted = (wchar_t *)env->GetStringChars((jstring) arrayObject, 0);
			std::wstring value(converted);
			env->ReleaseStringChars((jstring) arrayObject, (jchar*) converted);

			dest.vt = VT_BSTR;
			dest.bstrVal = SysAllocString(value.c_str());
		} else if (env->IsInstanceOf(arrayObject, booleanClazz)) {
			bool value = env->CallBooleanMethod(arrayObject, booleanValue) == JNI_TRUE;

			dest.vt = VT_BOOL;
			dest.boolVal = value;
		} else if (env->IsInstanceOf(arrayObject, elementClazz)) {
			ElementWrapper* element = (ElementWrapper*) env->GetLongField(arrayObject, elementPointer);
			
			dest.vt = VT_DISPATCH;
			dest.pdispVal = element->getWrappedElement();
		}

		SafeArrayPutElement(convertedItems, &i, &dest);
	}

	const wchar_t* converted = (wchar_t *)env->GetStringChars(script, 0);
	CComVariant& result = wrapper->executeScript(converted, convertedItems);
	env->ReleaseStringChars(script, (jchar*) converted);

	// TODO (simon): Does this clear everything properly?
	SafeArrayDestroy(convertedItems);

	if (result.vt == VT_BSTR) {
		return bstr2jstring(env, result.bstrVal);
	} else if (result.vt == VT_DISPATCH) {
		// Attempt to create a new webelement
		IHTMLElement *node = (IHTMLElement*) result.pdispVal;
		if (!node) {
			cerr << L"Cannot convert response to element. Attempting to convert to string" << endl;
			return lpcw2jstring(env, comvariant2cw(result));
		}

		ElementWrapper* element = new ElementWrapper(wrapper, node);

		jclass clazz = env->FindClass("org/openqa/selenium/ie/InternetExplorerElement");
		jmethodID cId = env->GetMethodID(clazz, "<init>", "(J)V");

		return env->NewObject(clazz, cId, (jlong) element);
	} else if (result.vt == VT_BOOL) {
		jclass clazz = env->FindClass("java/lang/Boolean");
		jmethodID cId = env->GetMethodID(clazz, "<init>", "(Z)V");

		return env->NewObject(clazz, cId, (jboolean) (result.boolVal == VARIANT_TRUE));
	} else if (result.vt == VT_I4) {
		jclass clazz = env->FindClass("java/lang/Long");
		jmethodID cId = env->GetMethodID(clazz, "<init>", "(J)V");
		return env->NewObject(clazz, cId, (jlong) result.lVal);
	} else if (result.vt == VT_I8) {
		jclass clazz = env->FindClass("java/lang/Long");
		jmethodID cId = env->GetMethodID(clazz, "<init>", "(J)V");
		return env->NewObject(clazz, cId, (jlong) result.dblVal);
	} else if (result.vt == VT_USERDEFINED) {
		jclass newExcCls;
		env->ExceptionDescribe();
		env->ExceptionClear();
		newExcCls = env->FindClass("java/lang/RuntimeException");
		jmethodID cId = env->GetMethodID(newExcCls, "<init>", "(Ljava/lang/String;)V");

		jstring message = bstr2jstring(env, result.bstrVal);

		jobject exception;
		if (message) {
			exception = env->NewObject(newExcCls, cId, message);
		} else {
			cout << "Falling back" << endl;
			exception = env->NewObject(newExcCls, cId, (jstring) "Cannot extract cause of error");
		}

		env->Throw((jthrowable) exception);
		return NULL;
	}

	cerr << "Unknown variant type. Will attempt to coerce to string: " << result.vt << endl;
	return lpcw2jstring(env, comvariant2cw(result));
	}
	END_TRY_CATCH_ANY
	return NULL;
	
	return NULL;
}

JNIEXPORT jstring JNICALL Java_org_openqa_selenium_ie_InternetExplorerDriver_getPageSource
  (JNIEnv *env, jobject obj)
{
	TRY
	{
		InternetExplorerDriver* wrapper = NULL; // = getIe(env, obj);
		LPCWSTR text = wrapper->getPageSource();
		return lpcw2jstring(env, text);
	}
	END_TRY_CATCH_ANY

	return NULL;
}

#ifdef __cplusplus
}
#endif
