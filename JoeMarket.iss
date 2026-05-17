#ifndef AppArch
  #define AppArch "x64"
#endif

#define AppName      "Joe Market"
#define AppVersion   "1.0"
#define AppPublisher "Youssef Ebrahim"
#define AppExeName   "JoeMarket.exe"
#define JarName      "JoeMarket-1.0.jar"
#define LaunchScript "JoeMarket.exe"

[Setup]
; ── Basic Info ──────────────────────────────────────────
AppName={#AppName}
AppVersion={#AppVersion}
AppPublisher={#AppPublisher}
AppContact=Youssef_33263051
DefaultDirName={autopf}\JoeMarket
DefaultGroupName={#AppName}
UninstallDisplayIcon={app}\icon.ico
OutputDir=installer_output

#if AppArch == "x64"
  OutputBaseFilename=JoeMarket_Setup_{#AppVersion}_x64
  ArchitecturesAllowed=x64compatible arm64
  ArchitecturesInstallIn64BitMode=x64compatible arm64
#else
  OutputBaseFilename=JoeMarket_Setup_{#AppVersion}_x86
  ArchitecturesAllowed=x86
#endif

SetupIconFile=icon.ico
Compression=lzma2/ultra64
SolidCompression=yes
WizardStyle=modern

; ── Wizard appearance ───────────────────────────────────
WizardImageFile=icon.bmp
WizardSmallImageFile=icon.bmp
WizardImageStretch=no

; ── Require admin rights ────────────────────────────────
PrivilegesRequired=lowest
PrivilegesRequiredOverridesAllowed=dialog

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "Create a Desktop shortcut"; GroupDescription: "Additional icons:"
Name: "quicklaunchicon"; Description: "Create a Quick Launch shortcut"; GroupDescription: "Additional icons:"; OnlyBelowVersion: 6.1

[Files]
; Main JAR
Source: "target\{#JarName}"; DestDir: "{app}"; Flags: ignoreversion

; Launch script
Source: "installer_assets\{#LaunchScript}"; DestDir: "{app}"; Flags: ignoreversion

; Icon
Source: "installer_assets\icon.ico"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
; Start Menu
Name: "{group}\{#AppName}"; Filename: "{app}\{#LaunchScript}"; WorkingDir: "{app}"; IconFilename: "{app}\icon.ico"; Comment: "Launch Joe Market Grocery Shop"
Name: "{group}\Uninstall {#AppName}"; Filename: "{uninstallexe}"

; Desktop shortcut
Name: "{autodesktop}\{#AppName}"; Filename: "{app}\{#LaunchScript}"; WorkingDir: "{app}"; IconFilename: "{app}\icon.ico"; Tasks: desktopicon

; Quick Launch
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#AppName}"; Filename: "{app}\{#LaunchScript}"; WorkingDir: "{app}"; Tasks: quicklaunchicon

[Run]
Filename: "{app}\{#LaunchScript}"; Description: "Launch {#AppName} now"; Flags: nowait postinstall skipifsilent shellexec

[UninstallDelete]
Type: filesandordirs; Name: "{app}"

[Code]
function IsJavaInstalled(): Boolean;
var
  JavaPath: string;
begin
  Result :=
    RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment',
      'CurrentVersion', JavaPath) or
    RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\JDK',
      'CurrentVersion', JavaPath) or
    RegQueryStringValue(HKCU, 'SOFTWARE\JavaSoft\Java Runtime Environment',
      'CurrentVersion', JavaPath);

  if not Result then
    Result :=
      FileExists(ExpandConstant('{pf}\Java\jre\bin\java.exe')) or
      FileExists(ExpandConstant('{pf64}\Java\jre\bin\java.exe'));
end;

function InitializeSetup(): Boolean;
begin
  Result := True;

  if not IsJavaInstalled() then
  begin
    if MsgBox(
      'Java (JDK 17 or higher) was not detected on this system.' + #13#10 +
      'Joe Market requires Java to run.' + #13#10#13#10 +
      'Continue installation anyway?',
      mbConfirmation, MB_YESNO) = IDNO then
      Result := False;
  end;
end;