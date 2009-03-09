

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 7.00.0500 */
/* at Fri Mar 06 00:56:05 2009
 */
/* Compiler settings for .\google_update_idl.idl:
    Oicf, W1, Zp8, env=Win32 (32b run)
    protocol : dce , ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 475
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __google_update_idl_h__
#define __google_update_idl_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __IBrowserHttpRequest2_FWD_DEFINED__
#define __IBrowserHttpRequest2_FWD_DEFINED__
typedef interface IBrowserHttpRequest2 IBrowserHttpRequest2;
#endif 	/* __IBrowserHttpRequest2_FWD_DEFINED__ */


#ifndef __IProcessLauncher_FWD_DEFINED__
#define __IProcessLauncher_FWD_DEFINED__
typedef interface IProcessLauncher IProcessLauncher;
#endif 	/* __IProcessLauncher_FWD_DEFINED__ */


#ifndef __IProgressWndEvents_FWD_DEFINED__
#define __IProgressWndEvents_FWD_DEFINED__
typedef interface IProgressWndEvents IProgressWndEvents;
#endif 	/* __IProgressWndEvents_FWD_DEFINED__ */


#ifndef __IJobObserver_FWD_DEFINED__
#define __IJobObserver_FWD_DEFINED__
typedef interface IJobObserver IJobObserver;
#endif 	/* __IJobObserver_FWD_DEFINED__ */


#ifndef __IGoogleUpdate_FWD_DEFINED__
#define __IGoogleUpdate_FWD_DEFINED__
typedef interface IGoogleUpdate IGoogleUpdate;
#endif 	/* __IGoogleUpdate_FWD_DEFINED__ */


#ifndef __IGoogleUpdateCore_FWD_DEFINED__
#define __IGoogleUpdateCore_FWD_DEFINED__
typedef interface IGoogleUpdateCore IGoogleUpdateCore;
#endif 	/* __IGoogleUpdateCore_FWD_DEFINED__ */


#ifndef __ProcessLauncherClass_FWD_DEFINED__
#define __ProcessLauncherClass_FWD_DEFINED__

#ifdef __cplusplus
typedef class ProcessLauncherClass ProcessLauncherClass;
#else
typedef struct ProcessLauncherClass ProcessLauncherClass;
#endif /* __cplusplus */

#endif 	/* __ProcessLauncherClass_FWD_DEFINED__ */


#ifndef __InterfaceRegistrar_FWD_DEFINED__
#define __InterfaceRegistrar_FWD_DEFINED__

#ifdef __cplusplus
typedef class InterfaceRegistrar InterfaceRegistrar;
#else
typedef struct InterfaceRegistrar InterfaceRegistrar;
#endif /* __cplusplus */

#endif 	/* __InterfaceRegistrar_FWD_DEFINED__ */


#ifndef __OnDemandUserAppsClass_FWD_DEFINED__
#define __OnDemandUserAppsClass_FWD_DEFINED__

#ifdef __cplusplus
typedef class OnDemandUserAppsClass OnDemandUserAppsClass;
#else
typedef struct OnDemandUserAppsClass OnDemandUserAppsClass;
#endif /* __cplusplus */

#endif 	/* __OnDemandUserAppsClass_FWD_DEFINED__ */


#ifndef __OnDemandMachineAppsClass_FWD_DEFINED__
#define __OnDemandMachineAppsClass_FWD_DEFINED__

#ifdef __cplusplus
typedef class OnDemandMachineAppsClass OnDemandMachineAppsClass;
#else
typedef struct OnDemandMachineAppsClass OnDemandMachineAppsClass;
#endif /* __cplusplus */

#endif 	/* __OnDemandMachineAppsClass_FWD_DEFINED__ */


#ifndef __GoogleUpdateCoreClass_FWD_DEFINED__
#define __GoogleUpdateCoreClass_FWD_DEFINED__

#ifdef __cplusplus
typedef class GoogleUpdateCoreClass GoogleUpdateCoreClass;
#else
typedef struct GoogleUpdateCoreClass GoogleUpdateCoreClass;
#endif /* __cplusplus */

#endif 	/* __GoogleUpdateCoreClass_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 


#ifndef __IBrowserHttpRequest2_INTERFACE_DEFINED__
#define __IBrowserHttpRequest2_INTERFACE_DEFINED__

/* interface IBrowserHttpRequest2 */
/* [unique][nonextensible][oleautomation][uuid][object] */ 


EXTERN_C const IID IID_IBrowserHttpRequest2;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("5B25A8DC-1780-4178-A629-6BE8B8DEFAA2")
    IBrowserHttpRequest2 : public IUnknown
    {
    public:
        virtual HRESULT STDMETHODCALLTYPE Send( 
            /* [in] */ BSTR url,
            /* [in] */ BSTR post_data,
            /* [in] */ BSTR request_headers,
            /* [in] */ VARIANT response_headers_needed,
            /* [out] */ VARIANT *response_headers,
            /* [out] */ DWORD *response_code,
            /* [out] */ BSTR *cache_filename) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IBrowserHttpRequest2Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IBrowserHttpRequest2 * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IBrowserHttpRequest2 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IBrowserHttpRequest2 * This);
        
        HRESULT ( STDMETHODCALLTYPE *Send )( 
            IBrowserHttpRequest2 * This,
            /* [in] */ BSTR url,
            /* [in] */ BSTR post_data,
            /* [in] */ BSTR request_headers,
            /* [in] */ VARIANT response_headers_needed,
            /* [out] */ VARIANT *response_headers,
            /* [out] */ DWORD *response_code,
            /* [out] */ BSTR *cache_filename);
        
        END_INTERFACE
    } IBrowserHttpRequest2Vtbl;

    interface IBrowserHttpRequest2
    {
        CONST_VTBL struct IBrowserHttpRequest2Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IBrowserHttpRequest2_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IBrowserHttpRequest2_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IBrowserHttpRequest2_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IBrowserHttpRequest2_Send(This,url,post_data,request_headers,response_headers_needed,response_headers,response_code,cache_filename)	\
    ( (This)->lpVtbl -> Send(This,url,post_data,request_headers,response_headers_needed,response_headers,response_code,cache_filename) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IBrowserHttpRequest2_INTERFACE_DEFINED__ */


#ifndef __IProcessLauncher_INTERFACE_DEFINED__
#define __IProcessLauncher_INTERFACE_DEFINED__

/* interface IProcessLauncher */
/* [unique][helpstring][uuid][oleautomation][object] */ 


EXTERN_C const IID IID_IProcessLauncher;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("128C2DA6-2BC0-44c0-B3F6-4EC22E647964")
    IProcessLauncher : public IUnknown
    {
    public:
        virtual HRESULT STDMETHODCALLTYPE LaunchCmdLine( 
            /* [string][in] */ const WCHAR *cmd_line) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE LaunchBrowser( 
            /* [in] */ DWORD browser_type,
            /* [string][in] */ const WCHAR *url) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE LaunchCmdElevated( 
            /* [string][in] */ const WCHAR *app_guid,
            /* [string][in] */ const WCHAR *cmd_id,
            /* [in] */ DWORD caller_proc_id,
            /* [out] */ ULONG_PTR *proc_handle) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IProcessLauncherVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IProcessLauncher * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IProcessLauncher * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IProcessLauncher * This);
        
        HRESULT ( STDMETHODCALLTYPE *LaunchCmdLine )( 
            IProcessLauncher * This,
            /* [string][in] */ const WCHAR *cmd_line);
        
        HRESULT ( STDMETHODCALLTYPE *LaunchBrowser )( 
            IProcessLauncher * This,
            /* [in] */ DWORD browser_type,
            /* [string][in] */ const WCHAR *url);
        
        HRESULT ( STDMETHODCALLTYPE *LaunchCmdElevated )( 
            IProcessLauncher * This,
            /* [string][in] */ const WCHAR *app_guid,
            /* [string][in] */ const WCHAR *cmd_id,
            /* [in] */ DWORD caller_proc_id,
            /* [out] */ ULONG_PTR *proc_handle);
        
        END_INTERFACE
    } IProcessLauncherVtbl;

    interface IProcessLauncher
    {
        CONST_VTBL struct IProcessLauncherVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IProcessLauncher_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IProcessLauncher_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IProcessLauncher_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IProcessLauncher_LaunchCmdLine(This,cmd_line)	\
    ( (This)->lpVtbl -> LaunchCmdLine(This,cmd_line) ) 

#define IProcessLauncher_LaunchBrowser(This,browser_type,url)	\
    ( (This)->lpVtbl -> LaunchBrowser(This,browser_type,url) ) 

#define IProcessLauncher_LaunchCmdElevated(This,app_guid,cmd_id,caller_proc_id,proc_handle)	\
    ( (This)->lpVtbl -> LaunchCmdElevated(This,app_guid,cmd_id,caller_proc_id,proc_handle) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IProcessLauncher_INTERFACE_DEFINED__ */


/* interface __MIDL_itf_google_update_idl_0000_0002 */
/* [local] */ 

typedef /* [public][public] */ 
enum __MIDL___MIDL_itf_google_update_idl_0000_0002_0001
    {	COMPLETION_CODE_SUCCESS	= 1,
	COMPLETION_CODE_SUCCESS_CLOSE_UI	= ( COMPLETION_CODE_SUCCESS + 1 ) ,
	COMPLETION_CODE_ERROR	= ( COMPLETION_CODE_SUCCESS_CLOSE_UI + 1 ) ,
	COMPLETION_CODE_RESTART_ALL_BROWSERS	= ( COMPLETION_CODE_ERROR + 1 ) ,
	COMPLETION_CODE_REBOOT	= ( COMPLETION_CODE_RESTART_ALL_BROWSERS + 1 ) ,
	COMPLETION_CODE_RESTART_BROWSER	= ( COMPLETION_CODE_REBOOT + 1 ) ,
	COMPLETION_CODE_RESTART_ALL_BROWSERS_NOTICE_ONLY	= ( COMPLETION_CODE_RESTART_BROWSER + 1 ) ,
	COMPLETION_CODE_REBOOT_NOTICE_ONLY	= ( COMPLETION_CODE_RESTART_ALL_BROWSERS_NOTICE_ONLY + 1 ) ,
	COMPLETION_CODE_RESTART_BROWSER_NOTICE_ONLY	= ( COMPLETION_CODE_REBOOT_NOTICE_ONLY + 1 ) ,
	COMPLETION_CODE_RUN_COMMAND	= ( COMPLETION_CODE_RESTART_BROWSER_NOTICE_ONLY + 1 ) 
    } 	CompletionCodes;



extern RPC_IF_HANDLE __MIDL_itf_google_update_idl_0000_0002_v0_0_c_ifspec;
extern RPC_IF_HANDLE __MIDL_itf_google_update_idl_0000_0002_v0_0_s_ifspec;

#ifndef __IProgressWndEvents_INTERFACE_DEFINED__
#define __IProgressWndEvents_INTERFACE_DEFINED__

/* interface IProgressWndEvents */
/* [unique][helpstring][uuid][oleautomation][object] */ 


EXTERN_C const IID IID_IProgressWndEvents;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("1C642CED-CA3B-4013-A9DF-CA6CE5FF6503")
    IProgressWndEvents : public IUnknown
    {
    public:
        virtual HRESULT STDMETHODCALLTYPE DoClose( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE DoPause( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE DoResume( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE DoRestartBrowsers( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE DoReboot( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE DoLaunchBrowser( 
            /* [string][in] */ const WCHAR *url) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IProgressWndEventsVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IProgressWndEvents * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IProgressWndEvents * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IProgressWndEvents * This);
        
        HRESULT ( STDMETHODCALLTYPE *DoClose )( 
            IProgressWndEvents * This);
        
        HRESULT ( STDMETHODCALLTYPE *DoPause )( 
            IProgressWndEvents * This);
        
        HRESULT ( STDMETHODCALLTYPE *DoResume )( 
            IProgressWndEvents * This);
        
        HRESULT ( STDMETHODCALLTYPE *DoRestartBrowsers )( 
            IProgressWndEvents * This);
        
        HRESULT ( STDMETHODCALLTYPE *DoReboot )( 
            IProgressWndEvents * This);
        
        HRESULT ( STDMETHODCALLTYPE *DoLaunchBrowser )( 
            IProgressWndEvents * This,
            /* [string][in] */ const WCHAR *url);
        
        END_INTERFACE
    } IProgressWndEventsVtbl;

    interface IProgressWndEvents
    {
        CONST_VTBL struct IProgressWndEventsVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IProgressWndEvents_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IProgressWndEvents_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IProgressWndEvents_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IProgressWndEvents_DoClose(This)	\
    ( (This)->lpVtbl -> DoClose(This) ) 

#define IProgressWndEvents_DoPause(This)	\
    ( (This)->lpVtbl -> DoPause(This) ) 

#define IProgressWndEvents_DoResume(This)	\
    ( (This)->lpVtbl -> DoResume(This) ) 

#define IProgressWndEvents_DoRestartBrowsers(This)	\
    ( (This)->lpVtbl -> DoRestartBrowsers(This) ) 

#define IProgressWndEvents_DoReboot(This)	\
    ( (This)->lpVtbl -> DoReboot(This) ) 

#define IProgressWndEvents_DoLaunchBrowser(This,url)	\
    ( (This)->lpVtbl -> DoLaunchBrowser(This,url) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IProgressWndEvents_INTERFACE_DEFINED__ */


#ifndef __IJobObserver_INTERFACE_DEFINED__
#define __IJobObserver_INTERFACE_DEFINED__

/* interface IJobObserver */
/* [unique][helpstring][uuid][oleautomation][object] */ 


EXTERN_C const IID IID_IJobObserver;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("49D7563B-2DDB-4831-88C8-768A53833837")
    IJobObserver : public IUnknown
    {
    public:
        virtual HRESULT STDMETHODCALLTYPE OnShow( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE OnCheckingForUpdate( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE OnUpdateAvailable( 
            /* [string][in] */ const WCHAR *version_string) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE OnWaitingToDownload( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE OnDownloading( 
            /* [in] */ int time_remaining_ms,
            /* [in] */ int pos) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE OnWaitingToInstall( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE OnInstalling( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE OnPause( void) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE OnComplete( 
            /* [in] */ CompletionCodes code,
            /* [string][in] */ const WCHAR *reserved) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE SetEventSink( 
            /* [in] */ IProgressWndEvents *ui_sink) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IJobObserverVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IJobObserver * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IJobObserver * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IJobObserver * This);
        
        HRESULT ( STDMETHODCALLTYPE *OnShow )( 
            IJobObserver * This);
        
        HRESULT ( STDMETHODCALLTYPE *OnCheckingForUpdate )( 
            IJobObserver * This);
        
        HRESULT ( STDMETHODCALLTYPE *OnUpdateAvailable )( 
            IJobObserver * This,
            /* [string][in] */ const WCHAR *version_string);
        
        HRESULT ( STDMETHODCALLTYPE *OnWaitingToDownload )( 
            IJobObserver * This);
        
        HRESULT ( STDMETHODCALLTYPE *OnDownloading )( 
            IJobObserver * This,
            /* [in] */ int time_remaining_ms,
            /* [in] */ int pos);
        
        HRESULT ( STDMETHODCALLTYPE *OnWaitingToInstall )( 
            IJobObserver * This);
        
        HRESULT ( STDMETHODCALLTYPE *OnInstalling )( 
            IJobObserver * This);
        
        HRESULT ( STDMETHODCALLTYPE *OnPause )( 
            IJobObserver * This);
        
        HRESULT ( STDMETHODCALLTYPE *OnComplete )( 
            IJobObserver * This,
            /* [in] */ CompletionCodes code,
            /* [string][in] */ const WCHAR *reserved);
        
        HRESULT ( STDMETHODCALLTYPE *SetEventSink )( 
            IJobObserver * This,
            /* [in] */ IProgressWndEvents *ui_sink);
        
        END_INTERFACE
    } IJobObserverVtbl;

    interface IJobObserver
    {
        CONST_VTBL struct IJobObserverVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IJobObserver_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IJobObserver_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IJobObserver_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IJobObserver_OnShow(This)	\
    ( (This)->lpVtbl -> OnShow(This) ) 

#define IJobObserver_OnCheckingForUpdate(This)	\
    ( (This)->lpVtbl -> OnCheckingForUpdate(This) ) 

#define IJobObserver_OnUpdateAvailable(This,version_string)	\
    ( (This)->lpVtbl -> OnUpdateAvailable(This,version_string) ) 

#define IJobObserver_OnWaitingToDownload(This)	\
    ( (This)->lpVtbl -> OnWaitingToDownload(This) ) 

#define IJobObserver_OnDownloading(This,time_remaining_ms,pos)	\
    ( (This)->lpVtbl -> OnDownloading(This,time_remaining_ms,pos) ) 

#define IJobObserver_OnWaitingToInstall(This)	\
    ( (This)->lpVtbl -> OnWaitingToInstall(This) ) 

#define IJobObserver_OnInstalling(This)	\
    ( (This)->lpVtbl -> OnInstalling(This) ) 

#define IJobObserver_OnPause(This)	\
    ( (This)->lpVtbl -> OnPause(This) ) 

#define IJobObserver_OnComplete(This,code,reserved)	\
    ( (This)->lpVtbl -> OnComplete(This,code,reserved) ) 

#define IJobObserver_SetEventSink(This,ui_sink)	\
    ( (This)->lpVtbl -> SetEventSink(This,ui_sink) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IJobObserver_INTERFACE_DEFINED__ */


#ifndef __IGoogleUpdate_INTERFACE_DEFINED__
#define __IGoogleUpdate_INTERFACE_DEFINED__

/* interface IGoogleUpdate */
/* [unique][helpstring][uuid][oleautomation][object] */ 


EXTERN_C const IID IID_IGoogleUpdate;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("31AC3F11-E5EA-4a85-8A3D-8E095A39C27B")
    IGoogleUpdate : public IUnknown
    {
    public:
        virtual HRESULT STDMETHODCALLTYPE CheckForUpdate( 
            /* [string][in] */ const WCHAR *guid,
            /* [in] */ IJobObserver *observer) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE Update( 
            /* [string][in] */ const WCHAR *guid,
            /* [in] */ IJobObserver *observer) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IGoogleUpdateVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IGoogleUpdate * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IGoogleUpdate * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IGoogleUpdate * This);
        
        HRESULT ( STDMETHODCALLTYPE *CheckForUpdate )( 
            IGoogleUpdate * This,
            /* [string][in] */ const WCHAR *guid,
            /* [in] */ IJobObserver *observer);
        
        HRESULT ( STDMETHODCALLTYPE *Update )( 
            IGoogleUpdate * This,
            /* [string][in] */ const WCHAR *guid,
            /* [in] */ IJobObserver *observer);
        
        END_INTERFACE
    } IGoogleUpdateVtbl;

    interface IGoogleUpdate
    {
        CONST_VTBL struct IGoogleUpdateVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IGoogleUpdate_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IGoogleUpdate_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IGoogleUpdate_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IGoogleUpdate_CheckForUpdate(This,guid,observer)	\
    ( (This)->lpVtbl -> CheckForUpdate(This,guid,observer) ) 

#define IGoogleUpdate_Update(This,guid,observer)	\
    ( (This)->lpVtbl -> Update(This,guid,observer) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IGoogleUpdate_INTERFACE_DEFINED__ */


#ifndef __IGoogleUpdateCore_INTERFACE_DEFINED__
#define __IGoogleUpdateCore_INTERFACE_DEFINED__

/* interface IGoogleUpdateCore */
/* [unique][helpstring][uuid][oleautomation][object] */ 


EXTERN_C const IID IID_IGoogleUpdateCore;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("909489C2-85A6-4322-AA56-D25278649D67")
    IGoogleUpdateCore : public IUnknown
    {
    public:
        virtual HRESULT STDMETHODCALLTYPE LaunchCmdElevated( 
            /* [string][in] */ const WCHAR *app_guid,
            /* [string][in] */ const WCHAR *cmd_id,
            /* [in] */ DWORD caller_proc_id,
            /* [out] */ ULONG_PTR *proc_handle) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IGoogleUpdateCoreVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IGoogleUpdateCore * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IGoogleUpdateCore * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IGoogleUpdateCore * This);
        
        HRESULT ( STDMETHODCALLTYPE *LaunchCmdElevated )( 
            IGoogleUpdateCore * This,
            /* [string][in] */ const WCHAR *app_guid,
            /* [string][in] */ const WCHAR *cmd_id,
            /* [in] */ DWORD caller_proc_id,
            /* [out] */ ULONG_PTR *proc_handle);
        
        END_INTERFACE
    } IGoogleUpdateCoreVtbl;

    interface IGoogleUpdateCore
    {
        CONST_VTBL struct IGoogleUpdateCoreVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IGoogleUpdateCore_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IGoogleUpdateCore_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IGoogleUpdateCore_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IGoogleUpdateCore_LaunchCmdElevated(This,app_guid,cmd_id,caller_proc_id,proc_handle)	\
    ( (This)->lpVtbl -> LaunchCmdElevated(This,app_guid,cmd_id,caller_proc_id,proc_handle) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IGoogleUpdateCore_INTERFACE_DEFINED__ */



#ifndef __GoogleUpdateLib_LIBRARY_DEFINED__
#define __GoogleUpdateLib_LIBRARY_DEFINED__

/* library GoogleUpdateLib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_GoogleUpdateLib;

EXTERN_C const CLSID CLSID_ProcessLauncherClass;

#ifdef __cplusplus

class DECLSPEC_UUID("ABC01078-F197-4b0b-ADBC-CFE684B39C82")
ProcessLauncherClass;
#endif

EXTERN_C const CLSID CLSID_InterfaceRegistrar;

#ifdef __cplusplus

class DECLSPEC_UUID("9564861C-3469-4c9a-956A-74D5690790E6")
InterfaceRegistrar;
#endif

EXTERN_C const CLSID CLSID_OnDemandUserAppsClass;

#ifdef __cplusplus

class DECLSPEC_UUID("2F0E2680-9FF5-43c0-B76E-114A56E93598")
OnDemandUserAppsClass;
#endif

EXTERN_C const CLSID CLSID_OnDemandMachineAppsClass;

#ifdef __cplusplus

class DECLSPEC_UUID("6F8BD55B-E83D-4a47-85BE-81FFA8057A69")
OnDemandMachineAppsClass;
#endif

EXTERN_C const CLSID CLSID_GoogleUpdateCoreClass;

#ifdef __cplusplus

class DECLSPEC_UUID("E225E692-4B47-4777-9BED-4FD7FE257F0E")
GoogleUpdateCoreClass;
#endif
#endif /* __GoogleUpdateLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long *, unsigned long            , BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserMarshal(  unsigned long *, unsigned char *, BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserUnmarshal(unsigned long *, unsigned char *, BSTR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long *, BSTR * ); 

unsigned long             __RPC_USER  VARIANT_UserSize(     unsigned long *, unsigned long            , VARIANT * ); 
unsigned char * __RPC_USER  VARIANT_UserMarshal(  unsigned long *, unsigned char *, VARIANT * ); 
unsigned char * __RPC_USER  VARIANT_UserUnmarshal(unsigned long *, unsigned char *, VARIANT * ); 
void                      __RPC_USER  VARIANT_UserFree(     unsigned long *, VARIANT * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


