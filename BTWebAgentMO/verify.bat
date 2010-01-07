@echo off

set path=%path%;D:\Nokia\Devices\S40_SDK_3rd_Edition\bin\;E:\S40_SDK_3rd_Edition\bin
set bin2nokia=D:\Nokia\Devices\S40_SDK_3rd_Edition\lib;E:\S40_SDK_3rd_Edition\lib\classes.zip
set outpath=output

preverify1.1.exe -d %outpath% -classpath bin;%bin2nokia%  bin

if ERRORLEVEL 1 pause