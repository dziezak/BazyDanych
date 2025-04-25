import pyodbc
import configparser

# Parsowanie pliku konfiguracyjnego
cnf = configparser.ConfigParser()
cnf.read('cnf.ini')

# Łączenie z bazą danych
conn_str = (
    f'DRIVER={{ODBC Driver 17 for SQL Server}};'
    f'SERVER={cnf["mssqlDB"]["server"]};'
    f'DATABASE={cnf["mssqlDB"]["db"]};'
    f'UID={cnf["mssqlDB"]["user"]};'
    f'PWD={cnf["mssqlDB"]["pass"]};'
)

try:
    cnx_mssql = pyodbc.connect(conn_str)
    print("Połączenie udane.")
except pyodbc.Error as e:
    print(f"Błąd połączenia: {e}")
