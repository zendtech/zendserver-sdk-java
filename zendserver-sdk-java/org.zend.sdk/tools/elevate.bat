@echo off
if "%PROCESSOR_ARCHITECTURE%" == "x86" (
	@start ..\lib\x86\elevate.exe -k -wait "CD %CD% & %* & exit
) else (
    if "%PROCESSOR_ARCHITECTURE%" == "AMD64" (
		@start ..\lib\x86_64\elevate.exe -k -wait "CD %CD% & %* & exit
    ) else (
		echo Unsupported platform.
		@pause
    )
)