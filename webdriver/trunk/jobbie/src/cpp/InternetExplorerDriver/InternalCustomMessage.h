#ifndef JOBBIE_INTERNALCUSTOMMESSAGE_H_
#define JOBBIE_INTERNALCUSTOMMESSAGE_H_

#define _WD_START						WM_USER+1
#define _WD_GETIE						WM_USER+2
#define _WD_SWITCHTOFRAME				WM_USER+3

#define _WD_ELEM_ISDISPLAYED			WM_USER+10
#define _WD_ELEM_ISENABLED				WM_USER+11
#define _WD_ELEM_GETLOCATION			WM_USER+12
#define _WD_ELEM_GETHEIGHT				WM_USER+13
#define _WD_ELEM_GETWIDTH				WM_USER+14
#define _WD_ELEM_GETELEMENTNAME			WM_USER+15
#define _WD_ELEM_GETATTRIBUTE			WM_USER+16
#define _WD_ELEM_GETVALUE				WM_USER+17
#define _WD_ELEM_SENDKEYS				WM_USER+18
#define _WD_ELEM_CLEAR					WM_USER+19
#define _WD_ELEM_ISSELECTED				WM_USER+20
#define _WD_ELEM_SETSELECTED			WM_USER+21
#define _WD_ELEM_GETVALUEOFCSSPROP		WM_USER+22
#define _WD_ELEM_GETTEXT				WM_USER+23
#define _WD_ELEM_CLICK					WM_USER+24
#define _WD_ELEM_SUBMIT					WM_USER+25
#define _WD_ELEM_GETCHILDRENWTAGNAME	WM_USER+26

#define _WD_GETVISIBLE					WM_USER+30
#define _WD_SETVISIBLE					WM_USER+31
#define _WD_GETCURRENTURL				WM_USER+32
#define _WD_GETPAGESOURCE				WM_USER+33
#define _WD_GETTITLE					WM_USER+34
#define _WD_GETURL						WM_USER+35
#define _WD_GOFORWARD					WM_USER+36
#define _WD_GOBACK						WM_USER+37

#define _WD_SELELEMENTBYXPATH			WM_USER+40
#define _WD_SELELEMENTSBYXPATH			WM_USER+41
#define _WD_SELELEMENTBYID				WM_USER+42
#define _WD_SELELEMENTSBYID				WM_USER+43
#define _WD_SELELEMENTBYLINK			WM_USER+44
#define _WD_SELELEMENTSBYLINK			WM_USER+45
#define _WD_SELELEMENTBYNAME			WM_USER+46
#define _WD_SELELEMENTSBYNAME			WM_USER+47
#define _WD_SELELEMENTBYCLASSNAME		WM_USER+48
#define _WD_SELELEMENTSBYCLASSNAME		WM_USER+49

#define _WD_GETCOOKIES					WM_USER+50
#define _WD_ADDCOOKIE					WM_USER+51

#define _WD_WAITFORNAVIGATIONTOFINISH	WM_USER+60
#define _WD_ELEM_RELEASE				WM_USER+61

#define _WD_QUIT_IE						WM_USER+70
#define _WD_EXECUTESCRIPT				WM_USER+71
#define _WD_GETACTIVEELEMENT			WM_USER+72

#endif // JOBBIE_INTERNALCUSTOMMESSAGE_H_
