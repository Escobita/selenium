#pragma once

#include "nsCOMPtr.h"
#include "gecko18/nsIAccessibleDocument.h"
#include "gecko19/nsIAccessibleDocument.h"

class AccessibleDocumentWrapper 
{
public:
	AccessibleDocumentWrapper(nsISupports *node) 
	{
		wrapper_18 = do_QueryInterface(node);
		wrapper_19 = do_QueryInterface(node);
	}

	HWND getHWND() 
	{
		if (!isValid()) return NULL;

		void *hwnd = NULL;
		nsresult rv;
		
		if (wrapper_19) {
			rv = wrapper_19->GetWindowHandle(&hwnd);
			if(NS_SUCCEEDED(rv)){ return static_cast<HWND>(hwnd); }
		}

		if (wrapper_18) {
			rv = wrapper_18->GetWindowHandle(&hwnd);
			if(NS_SUCCEEDED(rv)){ return static_cast<HWND>(hwnd); }
		}

		return NULL;
	}

private:
	bool isValid() const 
	{
		return (wrapper_18 != NULL) || (wrapper_19 != NULL);
	}

	nsCOMPtr<nsIAccessibleDocument_18> wrapper_18;
	nsCOMPtr<nsIAccessibleDocument_19> wrapper_19;
};
