@echo off
echo Compiling TradeStock Manager...
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/*;bin" -d bin src/com/tradestock/*.java src/com/tradestock/db/*.java src/com/tradestock/model/*.java src/com/tradestock/dao/*.java src/com/tradestock/ui/*.java
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
) else (
    echo Compilation failed with error code %ERRORLEVEL%.
)
