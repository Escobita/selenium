#pragma once
#include <ctime>
#include "BrowserManager.h"
#include "interactions.h"

const LPCTSTR fileDialogNames[] = {
	_T("#32770"),
	_T("ComboBoxEx32"),
	_T("ComboBox"),
	_T("Edit"),
	NULL
};

class SendKeysCommandHandler :
	public WebDriverCommandHandler
{
public:
	struct FileNameData {
		HWND main;
		HWND hwnd;
		DWORD ieProcId;
		const wchar_t* text;
	};


	SendKeysCommandHandler(void)
	{

	}

	virtual ~SendKeysCommandHandler(void)
	{
	}

private:
	static WORD WINAPI SendKeysCommandHandler::SetFileValue(FileNameData *data)
	{
		Sleep(200);
		HWND ieMain = data->main;
		HWND dialogHwnd = ::GetLastActivePopup(ieMain);

		int maxWait = 10;
		while ((dialogHwnd == ieMain) && --maxWait)
		{
			::Sleep(200);
			dialogHwnd = ::GetLastActivePopup(ieMain);
		}

		if (!dialogHwnd || (dialogHwnd == ieMain))
		{
			// No dialog directly owned by the top-level window.
			// Look for a dialog belonging to the same process as
			// the IE server window. This isn't perfect, but it's
			// all we have for now.
			maxWait = 10;
			while ((dialogHwnd == ieMain) && --maxWait)
			{
				ProcessWindowInfo procWinInfo;
				procWinInfo.dwProcessId = data->ieProcId;
				::EnumWindows(SendKeysCommandHandler::FindDialogWindowForProcess, (LPARAM)&procWinInfo);
				if (procWinInfo.hwndBrowser != NULL)
				{
					dialogHwnd = procWinInfo.hwndBrowser;
				}
			}
		}

		if (!dialogHwnd || (dialogHwnd == ieMain))
		{
			//LOG(WARN) << "No dialog found";
			return false;
		}

		return sendKeysToFileUploadAlert(dialogHwnd, data->text);
	}

	static BOOL CALLBACK SendKeysCommandHandler::FindDialogWindowForProcess(HWND hwnd, LPARAM arg)
	{
		ProcessWindowInfo *procWinInfo = (ProcessWindowInfo *)arg;

		// Could this be an Internet Explorer Server window?
		// 7 == "#32770\0"
		char name[7];
		if (GetClassNameA(hwnd, name, 7) == 0)
		{
			// No match found. Skip
			return TRUE;
		}
		
		if (strcmp("#32770", name) != 0)
		{
			return TRUE;
		}
		else
		{
			DWORD dwProcessId = NULL;
			::GetWindowThreadProcessId(hwnd, &dwProcessId);
			if (procWinInfo->dwProcessId == dwProcessId)
			{
				// Once we've found the first Internet Explorer_Server window
				// for the process we want, we can stop.
				procWinInfo->hwndBrowser = hwnd;
				return FALSE;
			}
		}

		return TRUE;
	}

	static bool sendKeysToFileUploadAlert(HWND dialogHwnd, const wchar_t* value) 
	{
		HWND editHwnd = NULL;
		int maxWait = 10;
		while (!editHwnd && --maxWait)
		{
			wait(200);
			editHwnd = dialogHwnd;
			for (int i = 1; fileDialogNames[i]; ++i)
			{
				editHwnd = getChildWindow(editHwnd, fileDialogNames[i]);
			}
		}

		if (editHwnd)
		{
			// Attempt to set the value, looping until we succeed.
			const wchar_t* filename = value;
			size_t expected = wcslen(filename);
			size_t curr = 0;

			while (expected != curr)
			{
				::SendMessage(editHwnd, WM_SETTEXT, 0, (LPARAM) filename);
				wait(1000);
				curr = ::SendMessage(editHwnd, WM_GETTEXTLENGTH, 0, 0);
			}

			HWND openHwnd = ::FindWindowExW(dialogHwnd, NULL, L"Button", L"&Open");
			if (openHwnd)
			{
				::SendMessage(openHwnd, WM_LBUTTONDOWN, 0, 0);
				::SendMessage(openHwnd, WM_LBUTTONUP, 0, 0);
			}

			return true;
		}

		//LOG(WARN) << "No edit found";
		return false;
	}
protected:

	void SendKeysCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else if (commandParameters.find("value") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "value";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str(), CP_UTF8));

			std::wstring keys(L"");
			Json::Value keyArray(commandParameters["value"]);
			for (int i = 0; i < keyArray.size(); ++i )
			{
				std::string key(keyArray[i].asString());
				keys.append(CA2W(key.c_str(), CP_UTF8));
			}

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);

			if (statusCode == SUCCESS)
			{
				bool displayed;
				statusCode = pElementWrapper->IsDisplayed(&displayed);
				if (statusCode != SUCCESS || !displayed)
				{
					response->m_statusCode = EELEMENTNOTDISPLAYED;
					response->m_value["message"] = "Element is not displayed";
					return;
				}

				if (!pElementWrapper->IsEnabled())
				{
					response->m_statusCode = EELEMENTNOTENABLED;
					response->m_value["message"] = "Element is not enabled";
					return;
				}

				CComQIPtr<IHTMLElement> element(pElementWrapper->m_pElement);

				element->scrollIntoView(CComVariant(VARIANT_TRUE));

				CComQIPtr<IHTMLInputFileElement> file(element);
				if (file)
				{
					DWORD ieProcId;
					::GetWindowThreadProcessId(hwnd, &ieProcId);
					HWND topLevelHwnd = NULL;
					pBrowserWrapper->m_pBrowser->get_HWND(reinterpret_cast<SHANDLE_PTR*>(&topLevelHwnd));

					FileNameData keyData;
					keyData.main = topLevelHwnd;
					keyData.hwnd = hwnd;
					keyData.text = keys.c_str();
					keyData.ieProcId = ieProcId;

					DWORD threadId;
					::CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE) &SendKeysCommandHandler::SetFileValue, (void *) &keyData, 0, &threadId);

					element->click();
					// We're now blocked until the dialog closes.
					return;
				}

				CComQIPtr<IHTMLElement2> element2(element);
				element2->focus();

				// Check we have focused the element.
				CComPtr<IDispatch> dispatch;
				element->get_document(&dispatch);
				CComQIPtr<IHTMLDocument2> document(dispatch);

				bool hasFocus = false;
				clock_t maxWait = clock() + 1000;
				for (int i = clock(); i < maxWait; i = clock())
				{
					wait(1);
					CComPtr<IHTMLElement> activeElement;
					if (document->get_activeElement(&activeElement) == S_OK)
					{
						CComQIPtr<IHTMLElement2> activeElement2(activeElement);
						if (element2.IsEqualObject(activeElement2))
						{
							hasFocus = true;
							break;
						}
					}
				}

				if (!hasFocus)
				{
					//cerr << "We don't have focus on element." << endl;
				}

				sendKeys(hwnd, keys.c_str(), manager->GetSpeed());
			}
			else
			{
				response->m_value["message"] = "Element is no longer valid";
			}
			response->m_statusCode = statusCode;
		}
	}
};
