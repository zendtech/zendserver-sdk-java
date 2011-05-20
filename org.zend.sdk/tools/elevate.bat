@echo off
if "%PROCESSOR_ARCHITECTURE%" == "x86" (
	@start x86-elevate.exe %*
) else (
    if "%PROCESSOR_ARCHITECTURE%" == "AMD64" (
		@start x64-elevate.exe %*
    ) else (
		echo Unsupported platform.
		@pause
    )
)