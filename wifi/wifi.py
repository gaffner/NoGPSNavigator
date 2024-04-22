from typing import Dict, List

import requests

from utils import read_config
from wifi.consts import default_macaddress

requests.packages.urllib3.disable_warnings(requests.packages.urllib3.exceptions.InsecureRequestWarning)


def get_location_from_wifi(access_points: List[Dict]) -> List:
    """
    Searches for a network with a specific BSSID in the Google geolocation API.
    :param access_points: list of bssid's for each access point, and strength length
    :return: dictionary containing information about the network, or an error message if an error occurred.
    """

    parsed_config = read_config()
    api_key = parsed_config.get('google_api')
    headers = {
        'accept': 'application/json',
        'Content-Type': 'application/json'
    }

    # Set up the query parameters with the BSSID
    params = {
        'considerIp': 'false',
        'wifiAccessPoints': access_points + default_macaddress
    }
    endpoint = f'https://www.googleapis.com/geolocation/v1/geolocate?key={api_key}'

    try:
        response = requests.post(
            endpoint,
            headers=headers,
            json=params,
            verify=not parsed_config.get('no-ssl-verify', False)
        )
        if response.status_code == 200:  # Check if the request was successful
            result = response.json()
            data = {
                'location':
                    {
                        'latitude': result['location']['lat'],
                        'longitude': result['location']['lng']
                    },
                'accuracy':
                    {
                        'value': result['accuracy'],
                        'type': 'meter'
                    }
            }
            return [data]
        else:
            return [{
                'module': 'google',
                'error': response.json()['error']['message']
            }]
    except Exception as e:
        return [{
            'module': 'google',
            'error': str(e)
        }]


def main():
    """
    this file should be used as module.
    this function is for testing purposes.
    """
    access_points = [
        {
            'macAddress': '',  # you can type your bssid mac address for example.
        }
    ]

    result = get_location_from_wifi(access_points)
    print(result)


if __name__ == '__main__':
    main()
