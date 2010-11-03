#pragma once
#include "BrowserManager.h"

class SetElementSelectedCommandHandler :
	public WebDriverCommandHandler
{
public:

	SetElementSelectedCommandHandler(void)
	{
	}

	virtual ~SetElementSelectedCommandHandler(void)
	{
	}

protected:

	void SetElementSelectedCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else
		{
			std::wstring text(L"");
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str(), CP_UTF8));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				bool currentlySelected = pElementWrapper->IsSelected();

				if (!pElementWrapper->IsEnabled())
				{
					statusCode = EELEMENTNOTENABLED;
					response->m_value["message"] = "Cannot select disabled element";
				}
				else
				{
					bool displayed;
					statusCode = pElementWrapper->IsDisplayed(&displayed);
					if (statusCode != SUCCESS || !displayed) 
					{
						statusCode = EELEMENTNOTDISPLAYED;
						response->m_value["message"] = "Cannot select hidden element";
					}
					else
					{
						/* TODO(malcolmr): Why not: if (isSelected()) return; ? Do we really need to
						   re-set 'checked=true' for checkbox and do effectively nothing for select?
						   Maybe we should check for disabled elements first? */

						if (pElementWrapper->IsCheckBox()) {
							statusCode = SUCCESS;
							response->m_value = Json::Value::null;
							if (!pElementWrapper->IsSelected()) {
								pElementWrapper->Click(hwnd);
								pBrowserWrapper->m_waitRequired = true;
							}

							CComBSTR checked(L"checked");
							CComBSTR isTrue(L"true");
							CComVariant isChecked(isTrue);
							pElementWrapper->m_pElement->setAttribute(checked, isChecked, 0);

							if (currentlySelected != pElementWrapper->IsSelected()) {
								CComQIPtr<IHTMLDOMNode> checkBoxNode(pElementWrapper->m_pElement);
								pElementWrapper->FireEvent(checkBoxNode, L"onchange");
							}
						}
						else if (pElementWrapper->IsRadioButton()) {
							statusCode = SUCCESS;
							if (!pElementWrapper->IsSelected()) {
								pElementWrapper->Click(hwnd);
								pBrowserWrapper->m_waitRequired = true;
							}

							CComBSTR selected(L"selected");
							CComBSTR isTrue(L"true");
							CComVariant select(isTrue);
							pElementWrapper->m_pElement->setAttribute(selected, select, 0);

							if (currentlySelected != pElementWrapper->IsSelected()) {
								CComQIPtr<IHTMLDOMNode> radioButtonNode(pElementWrapper->m_pElement);
								pElementWrapper->FireEvent(radioButtonNode, L"onchange");
							}
						}
						else
						{
							statusCode = SUCCESS;
							CComQIPtr<IHTMLOptionElement> option(pElementWrapper->m_pElement);
							if (option) {
								option->put_selected(VARIANT_TRUE);

								// Looks like we'll need to fire the event on the select element and not
								// the option. Assume for now that the parent node is a select. Which is dumb.
								CComQIPtr<IHTMLDOMNode> optionNode(pElementWrapper->m_pElement);
								CComPtr<IHTMLDOMNode> parent;
								optionNode->get_parentNode(&parent);

								if (currentlySelected != pElementWrapper->IsSelected()) {
									pElementWrapper->FireEvent(parent, L"onchange");
								}
							}
							else
							{
								statusCode = EELEMENTNOTSELECTED;
								response->m_value["message"] = "Element type not selectable";
							}
						}
					}
				}
			}
			else
			{
				response->m_value["message"] = "Element is no longer valid";
			}

			response->m_statusCode = statusCode;
		}
	}
};
