@echo off
set	BIN_PATH="C:\Program Files\Java\jdk1.6.0_11\bin"
cd bin
REM %BIN_PATH%\javah -d ..\..\EagleAndroidJni\jni\gles11 -jni eagle.android.gles11.GL11
%BIN_PATH%\javah -d ..\..\EagleAndroidJni\jni\gles11 -jni eagle.android.gles11.GL11Extension
cd ../
