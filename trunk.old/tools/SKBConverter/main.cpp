#include <windows.h>
#include <tchar.h>
#include <string>
#include <commctrl.h>
#include <iostream>
#include <fstream>
#include <cassert>
using namespace std;

ofstream outFile;
ofstream outFile2;

class EnumWndProcParam
{
public:
	TCHAR szTitle[256];
	HWND hFound;
};

BOOL CALLBACK EnumWindowsProc(HWND hwnd, LPARAM lParam)    
{    
	TCHAR szTitle[256] = {0};    
	::GetWindowText(hwnd, szTitle, sizeof(szTitle) - 1);
	EnumWndProcParam * param = (EnumWndProcParam *) lParam;
	if (_tcsstr(szTitle, param->szTitle))
	{
		param->hFound = hwnd;	
		return   FALSE;    
	}
	return TRUE;    
}    


HWND MyFindWindow(TCHAR * title)
{
	EnumWndProcParam param;
	param.hFound = 0;
	_tcscpy(param.szTitle, title);
	EnumWindows((WNDENUMPROC) EnumWindowsProc, (LPARAM) &param);
	return param.hFound;
}


bool SubmitQuery(HWND hInput, int number, HWND hSubmit)
{
	TCHAR strNum[10];
	_stprintf(strNum, TEXT("%d"), number);
	SendMessage(hInput, WM_SETTEXT, NULL, (LPARAM) strNum);
	PostMessage(hSubmit, WM_LBUTTONDOWN, (WPARAM) MK_LBUTTON, (LPARAM) MAKELPARAM(10,10));
	PostMessage(hSubmit, WM_LBUTTONUP, (WPARAM) MK_LBUTTON, (LPARAM) MAKELPARAM(10,10));
	HWND hDlg = 0;
	int i = 0;
	while ((hDlg == 0) && (i < 5))
	{
		 hDlg = ::FindWindowEx(NULL, NULL, NULL, TEXT("提示"));
		 ++i;
		 Sleep(100);
	}
	if (hDlg != 0)
	{
		SendMessage(hDlg, WM_CLOSE, 0, 0);
		return false;
	}
	return true;
}

bool GetList(HANDLE hProcess, HWND hTable)
{
	int count = SendMessage(hTable, LVM_GETITEMCOUNT, 0, 0);
	if (count == 0) 
		return true;

	const int MAX_CHAR_LEN = 50;
	LVITEM * pItem = (LVITEM *) VirtualAllocEx(hProcess, NULL, sizeof(LVITEM), MEM_COMMIT, PAGE_READWRITE);
	TCHAR * pBuffer = (TCHAR *) VirtualAllocEx(hProcess, NULL, MAX_CHAR_LEN, MEM_COMMIT, PAGE_READWRITE);
	if ((pItem == NULL) || (pBuffer == NULL))
		return false;
	LVITEM tvItm;
	memset(&tvItm, 0, sizeof(LVITEM));
	tvItm.mask = TVIF_TEXT;
	tvItm.pszText = pBuffer;
	tvItm.cchTextMax = MAX_CHAR_LEN;
	TCHAR buffer[MAX_CHAR_LEN];
	string filename; // Used for output TRF file
	ofstream trfFile;
	for (int i = 0; i < count; ++i)
	{
		for (int j = 0; j < 14; ++j)
		{
			tvItm.iItem = i;
			tvItm.iSubItem = j;
			WriteProcessMemory(hProcess, pItem, &tvItm, sizeof(LVITEM), NULL);
			SendMessage(hTable, LVM_GETITEMTEXT, i, (LPARAM) pItem);
			ReadProcessMemory(hProcess, pBuffer, buffer, MAX_CHAR_LEN, NULL);
			if (j == 0)
			{
				if (buffer[0] == ' ')
					filename = string(buffer+1);
				else
					filename = string(buffer);
				string t1, t2;
				string * pt = &t1;
				for (int e = 0; e < filename.length(); ++ e)
				{
					if (filename[e] == ' ')
						continue;
					if (filename[e] == '/')
					{
						filename[e] = '_';
						if (pt == &t1)
							pt = &t2;
						else
							pt = 0;
						continue;
					}
					if (pt != 0)
					{
						pt->push_back(filename[e]);
					}
				}
				if (t1[t1.length() -1] % 2 == 0)
				{
					swap(t1, t2);
					if (!t1.empty())
					{
						if (t1[t1.length() -1] % 2 == 0)
						{
							filename += string(".bak");
						}
					}
				}
				filename = string("C:\\trf\\") + filename;
				filename += string(".trf");
				trfFile.open(filename.c_str(), ios::out);
				trfFile << "trf2," << (buffer[0] == ' '?buffer+1:buffer) << "," << t1 << "," << t2 << endl;
			}
			else if (j == 3)
				trfFile << buffer << endl;
			else if (j == 5)
				trfFile << buffer << endl;
			outFile << buffer << "\t";
		}
		outFile << endl;
		outFile2 << "=================" << endl;
		PostMessage(hTable, WM_LBUTTONDBLCLK, MK_LBUTTON, MAKELPARAM(10, 30 + i*20));
		HWND hwnd = 0;
		int t = 0;
		while (t < 5)
		{
			hwnd = MyFindWindow(TEXT("全程"));
			if (hwnd) 
				break;
			++t;
			Sleep(100);
		}
		if (hwnd)
		{
			HWND hSubTable = 0;
			while (hSubTable == 0)
			{
				hSubTable = ::FindWindowEx(hwnd, NULL, TEXT("SysListView32"), TEXT("List1"));
			}
			int iSubCount = SendMessage(hSubTable, LVM_GETITEMCOUNT, 0, 0);
			assert(iSubCount != 0);
			for (int x = 0; x < iSubCount; ++x)
			{
				string last;
				bool dup = false;
				for (int y = 0; y < 8; ++y)
				{
					tvItm.iItem = x;
					tvItm.iSubItem = y;
					WriteProcessMemory(hProcess, pItem, &tvItm, sizeof(LVITEM), NULL);
					SendMessage(hSubTable, LVM_GETITEMTEXT, x, (LPARAM) pItem);
					ReadProcessMemory(hProcess, pBuffer, buffer, MAX_CHAR_LEN, NULL);
					outFile2 << buffer << "\t";
					switch (y)
					{
					case 2:
						trfFile << buffer << ",";
						break;
					case 4:
						if (buffer[0] == 0)
							dup = true;
						else
							trfFile << buffer << ",";
						break;
					case 5:
						if (buffer[0] == 0)
						{
							trfFile << last << endl;
						}
						else
						{
							if (dup == true)
							{
								trfFile << buffer << "," << buffer << endl;
								dup = false;
							}
							else
								trfFile << buffer << endl;
						}
						break;
					}
					last = buffer;
				}
				outFile2 << endl;
			}
			SendMessage(hwnd, WM_CLOSE, 0, 0);
			trfFile.close();
		}
		else
		{
			outFile << "===Error Getting Data===" << endl;
		}
	}
    // free
    VirtualFreeEx(hProcess, pItem, 0, MEM_RELEASE);
    VirtualFreeEx(hProcess, pBuffer, 0, MEM_RELEASE);

    return true;
}


int main(int argc, char * argv[])
{
	int min = 1;
	int max = 9999;
	
	// Handle commmand line
	if (argc == 3)
	{
		min = atoi(argv[1]);
		max = atoi(argv[2]);
	}
	else if (argc != 1)
	{
		MessageBox(NULL,TEXT("Wrong Parameter!"),TEXT("Error"), MB_OK);	
		return 1;
	}
	
	// Start SMSKB
	STARTUPINFO siStartupInfo;
    PROCESS_INFORMATION piProcessInfo;
    memset(&siStartupInfo, 0, sizeof(siStartupInfo));
    memset(&piProcessInfo, 0, sizeof(piProcessInfo));
    siStartupInfo.cb = sizeof(siStartupInfo); 
	if(!CreateProcess(TEXT("smskb.exe"), NULL, 0, 0, true, CREATE_DEFAULT_ERROR_MODE, 0, 0, &siStartupInfo, &piProcessInfo))
	{
		MessageBox(NULL,TEXT("Cannot start SMSKB!"),TEXT("Error"), MB_OK);	
		return 1;
	}

	// Wait for the program to finish initialization
	Sleep(5000);

	// Find SMSKB windows
	TCHAR * title = 0;
	title = TEXT("最准确、最好用的列车时刻表软件");

	HWND hMain = MyFindWindow(title);	
	HWND hTable = NULL, hInput = NULL, hSubmit = NULL;
	if (hMain != 0)
	{
		hMain = ::FindWindowEx(hMain, NULL, TEXT("SysTabControl32"), TEXT("Tab1"));
		if (!hMain)
		{
			MessageBox(NULL,TEXT("Tab1 not found"),TEXT("Error"), MB_OK);
			return 1;
		}
		hMain = ::FindWindowEx(hMain, NULL,TEXT("#32770"), TEXT(""));
		if (!hMain)
		{
			MessageBox(NULL,TEXT("child dialog not found"),TEXT("Error"), MB_OK);
			return 1;
		}
		hTable = ::FindWindowEx(hMain, NULL, TEXT("SysListView32"), TEXT("List1"));
		if (hTable == 0)
		{
			MessageBox(NULL,TEXT("ListView not found"),TEXT("Error"), MB_OK);
			return 1;
		}
		hInput = ::FindWindowEx(hMain, hTable, TEXT("Edit"), NULL);
		if (hInput == 0)
		{
			MessageBox(NULL,TEXT("EditBox not found"),TEXT("Error"), MB_OK);
			return 1;
		}
		hSubmit = ::FindWindowEx(hMain, hInput, TEXT("Button"), TEXT("查询"));
		if (hSubmit == 0)
		{
			MessageBox(NULL,TEXT("Submit Button not found"),TEXT("Error"), MB_OK);
			return 1;
		}
	}
	else
	{
		MessageBox(NULL,TEXT("SMSKB is not running"),TEXT("Error"), MB_OK);
		return 1;
	}

	// Get process handle
	DWORD dwProcessID = 0;
	GetWindowThreadProcessId(hMain, &dwProcessID);
	if (dwProcessID == 0)
	{
		MessageBox(NULL,TEXT("Error getting Process ID"),TEXT("Error"), MB_OK);
		return 1;
	}
	HANDLE hProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, dwProcessID);
	if (hProcess == 0)
	{
		MessageBox(NULL,TEXT("Error getting Process Handle"),TEXT("Error"), MB_OK);
		return 1;
	}

	assert(hTable != NULL);
	assert(hInput != NULL);
	assert(hSubmit != NULL);
	
	// Init output file
	outFile.open("c:\\1.txt");
	outFile2.open("c:\\2.txt");
	
	// Main function
	for (int i = min; i < max; ++i)
	{
		bool bResult = SubmitQuery(hInput, i, hSubmit);
		Sleep(200);
		if (bResult)
		{
			GetList(hProcess, hTable);
		}
	}

	// Close SMSKB
	SendMessage(hMain, WM_CLOSE, 0, 0);
    WaitForSingleObject(piProcessInfo.hProcess, INFINITE);

	// Close handles
    CloseHandle(piProcessInfo.hProcess);
    CloseHandle(piProcessInfo.hThread);
    CloseHandle(hProcess);

	// Close output files
	outFile.close();
	outFile2.close();

	return 0;
}