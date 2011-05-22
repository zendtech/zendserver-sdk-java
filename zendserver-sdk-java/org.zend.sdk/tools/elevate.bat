@echo off
if "%PROCESSOR_ARCHITECTURE%" == "x86" (
	@start ..\lib\x86\elevate.exe "CD %CD% & %*
) else (
    if "%PROCESSOR_ARCHITECTURE%" == "AMD64" (
		@start ..\lib\x86_64\elevate.exe "CD %CD% & %*
    ) else (
		echo Unsupported platform.
		@pause
    )
)