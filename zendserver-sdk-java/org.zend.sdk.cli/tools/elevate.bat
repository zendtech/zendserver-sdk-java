@echo off
if "%PROCESSOR_ARCHITECTURE%" == "x86" (
  %~d0%~p0..\lib\x86\elevate.exe -wait -k "CD %CD% & %* > out & exit" & type out 
) else (
  if "%PROCESSOR_ARCHITECTURE%" == "AMD64" (
    %~d0%~p0..\lib\x86_64\elevate.exe -wait -k "CD %CD% & %* > out & exit" & type out 
  ) else (
    echo Unsupported platform.
    @pause
  )
)

