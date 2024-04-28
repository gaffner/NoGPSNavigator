import asyncio
import logging
import os
from datetime import datetime
from typing import List, Dict

import yaml
import json
from fastapi import Request


def read_config() -> Dict:
    """
    Loads the configuration data from config.yaml file.
    returns a dictionary containing the configuration data.
    """

    try:
        # Open the config.yaml file in read mode
        with open('config/config.yaml', 'r') as config_file:
            # Parse the contents of the file into a dictionary
            parsed_config = yaml.safe_load(config_file)
            return parsed_config
    except FileNotFoundError:
        # Return an error message if the file is not found
        return {'error': 'config.yaml file not found'}
    except yaml.YAMLError:
        # Return an error message if there is an error parsing the file
        return {'error': 'Error parsing config.yaml file'}


def read_borders() -> Dict:
    """
    Loads the borders file
    """
    try:
        with open('config/config.yaml', 'r') as borders_file:
            parsed_borders = json.load(borders_file)
            return parsed_borders
    except FileNotFoundError:
        return {'error': 'borders.json file not found'}
    except json.JSONDecodeError:
        return {'error': 'Error parsing borders.json file'}


def convert_nogps_request_to_google_request(request: List[Dict]):
    converted = [{'macAddress': access_point['macAddress']} for access_point in request]

    return converted


def save_user_logs(request: Request):
    ip, body = request.client.host, asyncio.run(request.body())
    current_time = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
    with open(os.path.join('user_logs', f'{ip}_{current_time}.log'), 'wb') as f:
        f.write(body)

    f.close()
    logging.info(f'Saved logs to {ip}_{current_time}.log')


def verify_in_israel(latitude: int, longitude: int) -> bool:
    borders = read_borders()
    return ((borders['latitude']['min'] <= latitude <= borders['latitude']['max'])
            and (borders['longitude']['min'] <= longitude <= borders['longitude']['max']))
