import configparser
import pyodbc

# parse config 
cnf = configparser.ConfigParser()
cnf.read('cnf.ini')

# Konfiguracja połączenia z bazą danych MSSQL
conn_str = (
    'DRIVER={ODBC Driver 17 for SQL Server};'  # Używamy ODBC Driver 17
    f'SERVER={cnf["mssqlDB"]["server"]};'
    f'DATABASE={cnf["mssqlDB"]["db"]};'
    f'UID={cnf["mssqlDB"]["user"]};'
    f'PWD={cnf["mssqlDB"]["pass"]};'
)

# Połączenie z bazą danych
cnx_mssql = pyodbc.connect(conn_str)

# Jeśli połączenie działa poprawnie, możesz przejść do dalszej pracy z bazą
cursor = cnx_mssql.cursor()
cursor.execute("SELECT @@VERSION")  # Przykładowe zapytanie
row = cursor.fetchone()
print(row)
