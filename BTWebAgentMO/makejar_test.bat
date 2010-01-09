@echo off

set path=%path%;F:\java\bin
set jarpath=..\
set inpath=output

jar cvfm %jarpath%/angent.jar testMANIFEST.MF -C %inpath% .

if ERRORLEVEL 1 pause