import sqlite3
from typing import Dict

class DBSaver:
    def __init__(self, db_name='navigation.db'):
        self.db_name = db_name
        self._create_table()

    def _create_table(self):
        conn = sqlite3.connect(self.db_name)
        cursor = conn.cursor()
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS location_data (
            ssid TEXT,
            bssid TEXT NOT NULL PRIMARY KEY,
            latitude REAL,
            longitude REAL,
            timestamp TEXT NOT NULL
        )
        ''')
        conn.commit()
        conn.close()

    def save_wifi_to_db(self, access_point: Dict):
        conn = sqlite3.connect(self.db_name)
        cursor = conn.cursor()
        cursor.execute('''
        INSERT OR REPLACE INTO location_data (ssid, bssid, latitude, longitude, timestamp) 
        VALUES (?, ?, ?, ?, ?)
        ''', (access_point['ssid'], access_point['bssid'], access_point['latitude'], access_point['longitude'], access_point['timestamp']))
        conn.commit()
        conn.close()

    def get_wifi_from_db(self) -> Dict:
        conn = sqlite3.connect(self.db_name)
        cursor = conn.cursor()
        cursor.execute('SELECT * FROM location_data')
        rows = cursor.fetchall()
        conn.close()
        return {'data': rows}
