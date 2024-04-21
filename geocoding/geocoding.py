from typing import Dict

import requests

from utils import read_config


def get_address_from_lating(lating: str) -> str:
    parsed_config = read_config()
    api_key = parsed_config.get('google_api')
    headers = {
        'accept': 'application/json',
        'Content-Type': 'application/json'
    }

    endpoint = f'https://maps.googleapis.com/maps/api/geocode/json?latlng={lating}&key={api_key}'

    try:
        response = requests.get(
            endpoint,
            headers=headers,
            verify=not parsed_config.get('no-ssl-verify', False)
        )
        if response.status_code == 200:  # Check if the request was successful
            result = response.json()
            try:
                return result["results"][0]["formatted_address"]
            except KeyError:
                return lating

    except Exception as e:
        return str(e)


def get_lating_from_address(address: str) -> Dict:
    parsed_config = read_config()
    api_key = parsed_config.get('google_api')
    headers = {
        'accept': 'application/json',
        'Content-Type': 'application/json'
    }

    endpoint = f'https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={api_key}'

    try:
        response = requests.get(
            endpoint,
            headers=headers,
            verify=not parsed_config.get('no-ssl-verify', False)
        )
        if response.status_code == 200:  # Check if the request was successful
            result = response.json()
            try:
                return {'lating': str(str(result['results'][0]['geometry']['location']['lat']) + ', ' +
                                      str(result['results'][0]['geometry']['location']['lng'])),
                        'address': result['results'][0]['formatted_address']}
            except KeyError:
                return {'error': 'Unknown error occurred'}

    except Exception as e:
        return str(e)
