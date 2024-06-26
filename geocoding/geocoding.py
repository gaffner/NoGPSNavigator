from typing import Dict

import requests

from utils import read_config, verify_in_israel


def get_address_from_lating(lating: str) -> str:
    if len(lating.split(', ')) > 0 and not verify_in_israel(float(lating.split(',')[0]), float(lating.split(', ')[1])):
        return "Location is too far, please try again"

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
            except (KeyError, IndexError):
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
            except (KeyError, IndexError):
                return {'lating': '0, 0', 'address': 'unknown'}

    except Exception as e:
        return str(e)
