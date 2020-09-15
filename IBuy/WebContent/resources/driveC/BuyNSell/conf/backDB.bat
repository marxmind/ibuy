

echo off
echo Backup database.....
C:
cd C:\Program Files\MariaDB 10.4\bin 

echo Creating dir if not exist
if not exist "C:\BuyNSell\databasebackup" mkdir C:\BuyNSell\databasebackup

setlocal
set LogPath=C:\BuyNSell\log\
set LogFileExt=.log
set LogFileName=Daily Backup%LogFileExt%
::use set MyLogFile=%date:~4% instead to remove the day of the week
::[COLOR="DarkRed"]set MyLogFile=%date%
::set MyLogFile=%MyLogFile:/=-%[/COLOR]
set MyLogFile=%MyLogFile%
set MyLogFile=%LogPath%%MyLogFile%%LogFileName%
::Note that the quotes are REQUIRED around %MyLogFIle% in case it contains a space
IF NOT Exist "%LogPath%" mkdir %LogPath%
If NOT Exist "%MyLogFile%" goto:noseparator
Echo.>>"%MyLogFile%"
Echo.========================================================================ITALIAWorks========================================>>"%MyLogFile%"
:noseparator
::echo.%Date% >>"%MyLogFile%"
::echo.%Time% >>"%MyLogFile%"
echo.%Date% %Time% Preparing for backup... >>"%MyLogFile%"
echo.%Date% %Time% starting backup... >>"%MyLogFile%"
set dateNow=%date:~7,2%-%date:~4,2%-%date:~10,4%
echo %dateNow%.sql
mysqldump.exe -e -uroot -poctober181986* -hlocalhost ibuy > C:\BuyNSell\databasebackup\buynsell_%dateNow%.sql
echo.%Date% %Time% Preparing to save the backup >>"%MyLogFile%"
echo.%Date% %Time% Location: C:\BuyNSell\databasebackup >>"%MyLogFile%"
echo.%Date% %Time% Backup has been successfully proccessed with the file name of 'buynsell_%dateNow%.sql' >>"%MyLogFile%"
echo.%Date% %Time% Location: C:\BuyNSell\databasebackup\buynsell_%dateNow%.sql >>"%MyLogFile%"
echo.%Date% %Time% Ended... >>"%MyLogFile%"