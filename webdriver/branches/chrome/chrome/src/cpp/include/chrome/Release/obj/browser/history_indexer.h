

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 7.00.0500 */
/* at Fri Mar 06 01:11:01 2009
 */
/* Compiler settings for .\history\history_indexer.idl:
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

#ifndef __history_indexer_h__
#define __history_indexer_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __IChromeHistoryIndexer_FWD_DEFINED__
#define __IChromeHistoryIndexer_FWD_DEFINED__
typedef interface IChromeHistoryIndexer IChromeHistoryIndexer;
#endif 	/* __IChromeHistoryIndexer_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 


#ifndef __IChromeHistoryIndexer_INTERFACE_DEFINED__
#define __IChromeHistoryIndexer_INTERFACE_DEFINED__

/* interface IChromeHistoryIndexer */
/* [unique][nonextensible][oleautomation][uuid][object] */ 


EXTERN_C const IID IID_IChromeHistoryIndexer;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("9C1100DD-51D4-4827-AE9F-3B8FAC4AED72")
    IChromeHistoryIndexer : public IUnknown
    {
    public:
        virtual HRESULT STDMETHODCALLTYPE SendPageData( 
            /* [in] */ VARIANT time,
            /* [in] */ BSTR url,
            /* [in] */ BSTR html,
            /* [in] */ BSTR title,
            /* [in] */ BSTR thumbnail_format,
            /* [in] */ VARIANT thumbnail) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE DeleteUserHistoryBetween( 
            /* [in] */ VARIANT begin_time,
            /* [in] */ VARIANT end_time) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IChromeHistoryIndexerVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IChromeHistoryIndexer * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IChromeHistoryIndexer * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IChromeHistoryIndexer * This);
        
        HRESULT ( STDMETHODCALLTYPE *SendPageData )( 
            IChromeHistoryIndexer * This,
            /* [in] */ VARIANT time,
            /* [in] */ BSTR url,
            /* [in] */ BSTR html,
            /* [in] */ BSTR title,
            /* [in] */ BSTR thumbnail_format,
            /* [in] */ VARIANT thumbnail);
        
        HRESULT ( STDMETHODCALLTYPE *DeleteUserHistoryBetween )( 
            IChromeHistoryIndexer * This,
            /* [in] */ VARIANT begin_time,
            /* [in] */ VARIANT end_time);
        
        END_INTERFACE
    } IChromeHistoryIndexerVtbl;

    interface IChromeHistoryIndexer
    {
        CONST_VTBL struct IChromeHistoryIndexerVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IChromeHistoryIndexer_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IChromeHistoryIndexer_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IChromeHistoryIndexer_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IChromeHistoryIndexer_SendPageData(This,time,url,html,title,thumbnail_format,thumbnail)	\
    ( (This)->lpVtbl -> SendPageData(This,time,url,html,title,thumbnail_format,thumbnail) ) 

#define IChromeHistoryIndexer_DeleteUserHistoryBetween(This,begin_time,end_time)	\
    ( (This)->lpVtbl -> DeleteUserHistoryBetween(This,begin_time,end_time) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IChromeHistoryIndexer_INTERFACE_DEFINED__ */



#ifndef __history_indexerLib_LIBRARY_DEFINED__
#define __history_indexerLib_LIBRARY_DEFINED__

/* library history_indexerLib */
/* [helpstring][uuid] */ 


EXTERN_C const IID LIBID_history_indexerLib;
#endif /* __history_indexerLib_LIBRARY_DEFINED__ */

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


