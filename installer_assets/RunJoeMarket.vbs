Set WshShell = CreateObject("WScript.Shell")
Set FSO = CreateObject("Scripting.FileSystemObject")

' Get the directory where this script is located
ScriptDir = FSO.GetParentFolderName(Wscript.ScriptFullName)

' Set the working directory to the application directory (equivalent to cd /d)
WshShell.CurrentDirectory = ScriptDir

' Check if bundled JRE exists (64-bit first, then 32-bit)
JavaExe = "javaw.exe"
If FSO.FileExists(ScriptDir & "\jre64\bin\javaw.exe") Then
    JavaExe = Chr(34) & ScriptDir & "\jre64\bin\javaw.exe" & Chr(34)
ElseIf FSO.FileExists(ScriptDir & "\jre32\bin\javaw.exe") Then
    JavaExe = Chr(34) & ScriptDir & "\jre32\bin\javaw.exe" & Chr(34)
Else
    ' Fall back to system javaw
    JavaExe = "javaw"
End If

' Build the run command
Cmd = JavaExe & " -jar " & Chr(34) & ScriptDir & "\JoeMarket-1.0.jar" & Chr(34)

' Run the command completely hidden (0) and do not wait for it to exit (False)
On Error Resume Next
WshShell.Run Cmd, 0, False

If Err.Number <> 0 Then
    MsgBox "Java (JDK/JRE) was not found on this system." & vbCrLf & _
           "Joe Market requires Java 17 or higher to run." & vbCrLf & vbCrLf & _
           "Please install Java from: https://adoptium.net", vbCritical, "Joe Market - Java Required"
    Err.Clear
End If

Set WshShell = Nothing
Set FSO = Nothing
