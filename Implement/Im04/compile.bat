@echo off
echo Compilando projeto...
if not exist "target\classes" mkdir target\classes

javac -d target\classes -cp "lib\*" src\main\java\br\edu\ufcg\grafos\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilacao concluida com sucesso!
) else (
    echo Erro na compilacao!
    exit /b 1
)

