

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 7.00.0500 */
/* at Fri Mar 06 16:10:07 2009
 */
/* Compiler settings for .\activex_test_control.idl:
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

#ifndef __activex_test_control_h__
#define __activex_test_control_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __IChromeTestControl_FWD_DEFINED__
#define __IChromeTestControl_FWD_DEFINED__
typedef interface IChromeTestControl IChromeTestControl;
#endif 	/* __IChromeTestControl_FWD_DEFINED__ */


#ifndef ___IChromeTestControlEvents_FWD_DEFINED__
#define ___IChromeTestControlEvents_FWD_DEFINED__
typedef interface _IChromeTestControlEvents _IChromeTestControlEvents;
#endif 	/* ___IChromeTestControlEvents_FWD_DEFINED__ */


#ifndef __ChromeTestControl_FWD_DEFINED__
#define __ChromeTestControl_FWD_DEFINED__

#ifdef __cplusplus
typedef class ChromeTestControl ChromeTestControl;
#else
typedef struct ChromeTestControl ChromeTestControl;
#endif /* __cplusplus */

#endif 	/* __ChromeTestControl_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 


#ifndef __IChromeTestControl_INTERFACE_DEFINED__
#define __IChromeTestControl_INTERFACE_DEFINED__

/* interface IChromeTestControl */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IChromeTestControl;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("9AC37249-E247-4B82-AC1E-0917737528E9")
    IChromeTestControl : public IDispatch
    {
    public:
        virtual /* [id][requestedit][bindable][propput] */ HRESULT STDMETHODCALLTYPE put_BackColor( 
            /* [in] */ OLE_COLOR clr) = 0;
        
        virtual /* [id][requestedit][bindable][propget] */ HRESULT STDMETHODCALLTYPE get_BackColor( 
            /* [retval][out] */ OLE_COLOR *pclr) = 0;
        
        virtual /* [id][requestedit][bindable][propput] */ HRESULT STDMETHODCALLTYPE put_BorderColor( 
            /* [in] */ OLE_COLOR clr) = 0;
        
        virtual /* [id][requestedit][bindable][propget] */ HRESULT STDMETHODCALLTYPE get_BorderColor( 
            /* [retval][out] */ OLE_COLOR *pclr) = 0;
        
        virtual /* [id][requestedit][bindable][propput] */ HRESULT STDMETHODCALLTYPE put_ForeColor( 
            /* [in] */ OLE_COLOR clr) = 0;
        
        virtual /* [id][requestedit][bindable][propget] */ HRESULT STDMETHODCALLTYPE get_ForeColor( 
            /* [retval][out] */ OLE_COLOR *pclr) = 0;
        
        virtual /* [id][requestedit][bindable][propput] */ HRESULT STDMETHODCALLTYPE put_Caption( 
            /* [in] */ BSTR strCaption) = 0;
        
        virtual /* [id][requestedit][bindable][propget] */ HRESULT STDMETHODCALLTYPE get_Caption( 
            /* [retval][out] */ BSTR *pstrCaption) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_StringProp( 
            /* [retval][out] */ BSTR *pVal) = 0;
        
        virtual /* [helpstring][id][propput] */ HRESULT STDMETHODCALLTYPE put_StringProp( 
            /* [in] */ BSTR newVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_LongProp( 
            /* [retval][out] */ LONG *pVal) = 0;
        
        virtual /* [helpstring][id][propput] */ HRESULT STDMETHODCALLTYPE put_LongProp( 
            /* [in] */ LONG newVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_DoubleProp( 
            /* [retval][out] */ DOUBLE *pVal) = 0;
        
        virtual /* [helpstring][id][propput] */ HRESULT STDMETHODCALLTYPE put_DoubleProp( 
            /* [in] */ DOUBLE newVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_BoolProp( 
            /* [retval][out] */ VARIANT_BOOL *pVal) = 0;
        
        virtual /* [helpstring][id][propput] */ HRESULT STDMETHODCALLTYPE put_BoolProp( 
            /* [in] */ VARIANT_BOOL newVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_ByteProp( 
            /* [retval][out] */ BYTE *pVal) = 0;
        
        virtual /* [helpstring][id][propput] */ HRESULT STDMETHODCALLTYPE put_ByteProp( 
            /* [in] */ BYTE newVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_FloatProp( 
            /* [retval][out] */ FLOAT *pVal) = 0;
        
        virtual /* [helpstring][id][propput] */ HRESULT STDMETHODCALLTYPE put_FloatProp( 
            /* [in] */ FLOAT newVal) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE BigSetMethodRet( 
            /* [in] */ BSTR string_param,
            /* [in] */ BYTE byte_param,
            /* [in] */ FLOAT float_param,
            /* [in] */ VARIANT_BOOL bool_param,
            /* [retval][out] */ BSTR *ret) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE SetByte( 
            /* [in] */ BYTE val) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE SetByteRet( 
            /* [in] */ BYTE byte_param,
            /* [retval][out] */ BYTE *ret) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE SetStringRet( 
            /* [in] */ BSTR val,
            /* [retval][out] */ BSTR *ret) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE GetCookie( 
            /* [retval][out] */ BSTR *cookie) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IChromeTestControlVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IChromeTestControl * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IChromeTestControl * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IChromeTestControl * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IChromeTestControl * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IChromeTestControl * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IChromeTestControl * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IChromeTestControl * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][requestedit][bindable][propput] */ HRESULT ( STDMETHODCALLTYPE *put_BackColor )( 
            IChromeTestControl * This,
            /* [in] */ OLE_COLOR clr);
        
        /* [id][requestedit][bindable][propget] */ HRESULT ( STDMETHODCALLTYPE *get_BackColor )( 
            IChromeTestControl * This,
            /* [retval][out] */ OLE_COLOR *pclr);
        
        /* [id][requestedit][bindable][propput] */ HRESULT ( STDMETHODCALLTYPE *put_BorderColor )( 
            IChromeTestControl * This,
            /* [in] */ OLE_COLOR clr);
        
        /* [id][requestedit][bindable][propget] */ HRESULT ( STDMETHODCALLTYPE *get_BorderColor )( 
            IChromeTestControl * This,
            /* [retval][out] */ OLE_COLOR *pclr);
        
        /* [id][requestedit][bindable][propput] */ HRESULT ( STDMETHODCALLTYPE *put_ForeColor )( 
            IChromeTestControl * This,
            /* [in] */ OLE_COLOR clr);
        
        /* [id][requestedit][bindable][propget] */ HRESULT ( STDMETHODCALLTYPE *get_ForeColor )( 
            IChromeTestControl * This,
            /* [retval][out] */ OLE_COLOR *pclr);
        
        /* [id][requestedit][bindable][propput] */ HRESULT ( STDMETHODCALLTYPE *put_Caption )( 
            IChromeTestControl * This,
            /* [in] */ BSTR strCaption);
        
        /* [id][requestedit][bindable][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Caption )( 
            IChromeTestControl * This,
            /* [retval][out] */ BSTR *pstrCaption);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_StringProp )( 
            IChromeTestControl * This,
            /* [retval][out] */ BSTR *pVal);
        
        /* [helpstring][id][propput] */ HRESULT ( STDMETHODCALLTYPE *put_StringProp )( 
            IChromeTestControl * This,
            /* [in] */ BSTR newVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_LongProp )( 
            IChromeTestControl * This,
            /* [retval][out] */ LONG *pVal);
        
        /* [helpstring][id][propput] */ HRESULT ( STDMETHODCALLTYPE *put_LongProp )( 
            IChromeTestControl * This,
            /* [in] */ LONG newVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_DoubleProp )( 
            IChromeTestControl * This,
            /* [retval][out] */ DOUBLE *pVal);
        
        /* [helpstring][id][propput] */ HRESULT ( STDMETHODCALLTYPE *put_DoubleProp )( 
            IChromeTestControl * This,
            /* [in] */ DOUBLE newVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_BoolProp )( 
            IChromeTestControl * This,
            /* [retval][out] */ VARIANT_BOOL *pVal);
        
        /* [helpstring][id][propput] */ HRESULT ( STDMETHODCALLTYPE *put_BoolProp )( 
            IChromeTestControl * This,
            /* [in] */ VARIANT_BOOL newVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_ByteProp )( 
            IChromeTestControl * This,
            /* [retval][out] */ BYTE *pVal);
        
        /* [helpstring][id][propput] */ HRESULT ( STDMETHODCALLTYPE *put_ByteProp )( 
            IChromeTestControl * This,
            /* [in] */ BYTE newVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_FloatProp )( 
            IChromeTestControl * This,
            /* [retval][out] */ FLOAT *pVal);
        
        /* [helpstring][id][propput] */ HRESULT ( STDMETHODCALLTYPE *put_FloatProp )( 
            IChromeTestControl * This,
            /* [in] */ FLOAT newVal);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *BigSetMethodRet )( 
            IChromeTestControl * This,
            /* [in] */ BSTR string_param,
            /* [in] */ BYTE byte_param,
            /* [in] */ FLOAT float_param,
            /* [in] */ VARIANT_BOOL bool_param,
            /* [retval][out] */ BSTR *ret);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *SetByte )( 
            IChromeTestControl * This,
            /* [in] */ BYTE val);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *SetByteRet )( 
            IChromeTestControl * This,
            /* [in] */ BYTE byte_param,
            /* [retval][out] */ BYTE *ret);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *SetStringRet )( 
            IChromeTestControl * This,
            /* [in] */ BSTR val,
            /* [retval][out] */ BSTR *ret);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *GetCookie )( 
            IChromeTestControl * This,
            /* [retval][out] */ BSTR *cookie);
        
        END_INTERFACE
    } IChromeTestControlVtbl;

    interface IChromeTestControl
    {
        CONST_VTBL struct IChromeTestControlVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IChromeTestControl_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IChromeTestControl_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IChromeTestControl_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IChromeTestControl_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IChromeTestControl_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IChromeTestControl_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IChromeTestControl_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IChromeTestControl_put_BackColor(This,clr)	\
    ( (This)->lpVtbl -> put_BackColor(This,clr) ) 

#define IChromeTestControl_get_BackColor(This,pclr)	\
    ( (This)->lpVtbl -> get_BackColor(This,pclr) ) 

#define IChromeTestControl_put_BorderColor(This,clr)	\
    ( (This)->lpVtbl -> put_BorderColor(This,clr) ) 

#define IChromeTestControl_get_BorderColor(This,pclr)	\
    ( (This)->lpVtbl -> get_BorderColor(This,pclr) ) 

#define IChromeTestControl_put_ForeColor(This,clr)	\
    ( (This)->lpVtbl -> put_ForeColor(This,clr) ) 

#define IChromeTestControl_get_ForeColor(This,pclr)	\
    ( (This)->lpVtbl -> get_ForeColor(This,pclr) ) 

#define IChromeTestControl_put_Caption(This,strCaption)	\
    ( (This)->lpVtbl -> put_Caption(This,strCaption) ) 

#define IChromeTestControl_get_Caption(This,pstrCaption)	\
    ( (This)->lpVtbl -> get_Caption(This,pstrCaption) ) 

#define IChromeTestControl_get_StringProp(This,pVal)	\
    ( (This)->lpVtbl -> get_StringProp(This,pVal) ) 

#define IChromeTestControl_put_StringProp(This,newVal)	\
    ( (This)->lpVtbl -> put_StringProp(This,newVal) ) 

#define IChromeTestControl_get_LongProp(This,pVal)	\
    ( (This)->lpVtbl -> get_LongProp(This,pVal) ) 

#define IChromeTestControl_put_LongProp(This,newVal)	\
    ( (This)->lpVtbl -> put_LongProp(This,newVal) ) 

#define IChromeTestControl_get_DoubleProp(This,pVal)	\
    ( (This)->lpVtbl -> get_DoubleProp(This,pVal) ) 

#define IChromeTestControl_put_DoubleProp(This,newVal)	\
    ( (This)->lpVtbl -> put_DoubleProp(This,newVal) ) 

#define IChromeTestControl_get_BoolProp(This,pVal)	\
    ( (This)->lpVtbl -> get_BoolProp(This,pVal) ) 

#define IChromeTestControl_put_BoolProp(This,newVal)	\
    ( (This)->lpVtbl -> put_BoolProp(This,newVal) ) 

#define IChromeTestControl_get_ByteProp(This,pVal)	\
    ( (This)->lpVtbl -> get_ByteProp(This,pVal) ) 

#define IChromeTestControl_put_ByteProp(This,newVal)	\
    ( (This)->lpVtbl -> put_ByteProp(This,newVal) ) 

#define IChromeTestControl_get_FloatProp(This,pVal)	\
    ( (This)->lpVtbl -> get_FloatProp(This,pVal) ) 

#define IChromeTestControl_put_FloatProp(This,newVal)	\
    ( (This)->lpVtbl -> put_FloatProp(This,newVal) ) 

#define IChromeTestControl_BigSetMethodRet(This,string_param,byte_param,float_param,bool_param,ret)	\
    ( (This)->lpVtbl -> BigSetMethodRet(This,string_param,byte_param,float_param,bool_param,ret) ) 

#define IChromeTestControl_SetByte(This,val)	\
    ( (This)->lpVtbl -> SetByte(This,val) ) 

#define IChromeTestControl_SetByteRet(This,byte_param,ret)	\
    ( (This)->lpVtbl -> SetByteRet(This,byte_param,ret) ) 

#define IChromeTestControl_SetStringRet(This,val,ret)	\
    ( (This)->lpVtbl -> SetStringRet(This,val,ret) ) 

#define IChromeTestControl_GetCookie(This,cookie)	\
    ( (This)->lpVtbl -> GetCookie(This,cookie) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IChromeTestControl_INTERFACE_DEFINED__ */



#ifndef __activex_test_controlLib_LIBRARY_DEFINED__
#define __activex_test_controlLib_LIBRARY_DEFINED__

/* library activex_test_controlLib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_activex_test_controlLib;

#ifndef ___IChromeTestControlEvents_DISPINTERFACE_DEFINED__
#define ___IChromeTestControlEvents_DISPINTERFACE_DEFINED__

/* dispinterface _IChromeTestControlEvents */
/* [helpstring][uuid] */ 


EXTERN_C const IID DIID__IChromeTestControlEvents;

#if defined(__cplusplus) && !defined(CINTERFACE)

    MIDL_INTERFACE("EF88DE01-35AF-463F-9802-1BF908F48696")
    _IChromeTestControlEvents : public IDispatch
    {
    };
    
#else 	/* C style interface */

    typedef struct _IChromeTestControlEventsVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _IChromeTestControlEvents * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _IChromeTestControlEvents * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _IChromeTestControlEvents * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _IChromeTestControlEvents * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _IChromeTestControlEvents * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _IChromeTestControlEvents * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _IChromeTestControlEvents * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        END_INTERFACE
    } _IChromeTestControlEventsVtbl;

    interface _IChromeTestControlEvents
    {
        CONST_VTBL struct _IChromeTestControlEventsVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _IChromeTestControlEvents_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _IChromeTestControlEvents_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _IChromeTestControlEvents_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _IChromeTestControlEvents_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _IChromeTestControlEvents_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _IChromeTestControlEvents_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _IChromeTestControlEvents_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */


#endif 	/* ___IChromeTestControlEvents_DISPINTERFACE_DEFINED__ */


EXTERN_C const CLSID CLSID_ChromeTestControl;

#ifdef __cplusplus

class DECLSPEC_UUID("4E174456-5EE6-494D-B6F2-2B52898A620E")
ChromeTestControl;
#endif
#endif /* __activex_test_controlLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long *, unsigned long            , BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserMarshal(  unsigned long *, unsigned char *, BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserUnmarshal(unsigned long *, unsigned char *, BSTR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long *, BSTR * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


