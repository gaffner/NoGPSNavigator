from typing import Dict, List

import requests
import yaml

from consts import default_macaddress

requests.packages.urllib3.disable_warnings(requests.packages.urllib3.exceptions.InsecureRequestWarning)


def read_config() -> Dict:
    """
    Loads the configuration data from config.yaml file.
    returns a dictionary containing the configuration data.
    """

    try:
        # Open the config.yaml file in read mode
        with open('Server/config/config.yaml', 'r') as config_file:
            # Parse the contents of the file into a dictionary
            parsed_config = yaml.safe_load(config_file)
            return parsed_config
    except FileNotFoundError:
        # Return an error message if the file is not found
        return {'error': 'config.yaml file not found'}
    except yaml.YAMLError:
        # Return an error message if there is an error parsing the file
        return {'error': 'Error parsing config.yaml file'}


def access_points_to_location(access_points: List[Dict]) -> Dict:
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
                'module': 'google',
                'latitude': result['location']['lat'],
                'longitude': result['location']['lng']
            }
            return data
        else:
            return {
                'module': 'google',
                'error': response.json()['error']['message']
            }
    except Exception as e:
        return {
            'module': 'google',
            'error': str(e)
        }


def main():
    """
    this file should be used as module.
    this function is for testing purposes.
    """
    access_points = [
        {
            'macAddress': '',  # you can type your bssid mac address for example
        }
    ]

    result = access_points_to_location(access_points)
    print(result)


if __name__ == '__main__':
    main()
