Set WshShell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
strDir = fso.GetParentFolderName(WScript.ScriptFullName)
WshShell.CurrentDirectory = strDir
WshShell.Run "javaw -jar """ & strDir & "\AzaniISP.jar""", 0, False
Set WshShell = Nothing
Set fso = Nothing
