#include <map>
#include <vector>

template <typename T>
class EventHandlerBase
{
public:
	virtual void raise(T param) = 0;
};

template <typename ListenerT, typename T>
class BrowserWrapperEventHandler : public EventHandlerBase<T>
{
	typedef void (ListenerT::*Handler)(T);
	ListenerT* m_object;
	Handler m_member;
	
public:

	BrowserWrapperEventHandler(ListenerT* object, Handler member)
		: m_object(object), m_member(member)
	{}

	void raise(T param)
	{
		return (m_object->*m_member)(param);		
	}	
};

template <typename T>
class BrowserWrapperEvent
{
	typedef std::map<int, EventHandlerBase<T> *> HandlersMap;
	HandlersMap m_handlers;
	int m_count;

public:

	BrowserWrapperEvent()
		: m_count(0) {}

	template <typename ListenerT>
	int attach(ListenerT* object, void (ListenerT::*member)(T))
	{
		m_handlers[m_count] = (new BrowserWrapperEventHandler<ListenerT, T>(object,member));
		m_count++;	
		return m_count-1;
	}

	bool detach(int id)
	{
		HandlersMap::iterator it = m_handlers.find(id);

		if(it == m_handlers.end())
			return false;
		
		delete it->second;
		m_handlers.erase(it);				
		return true;
	}

	void raise(T param)
	{
		// Cache the keys to the map. The event handler might
		// detach itself from the event, which would invalidate
		// an iterator directly on the map.
		std::vector<int> eventIdList;
		HandlersMap::iterator it = m_handlers.begin();
		for(; it != m_handlers.end(); it++)
		{
			eventIdList.push_back(it->first);
		}

		for(int i = 0; i < eventIdList.size(); ++i)
		{
			it = m_handlers.find(eventIdList[i]);
			if(it != m_handlers.end())
			{
				it->second->raise(param);
			}
		}
	}
};