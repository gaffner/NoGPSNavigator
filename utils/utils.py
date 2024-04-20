from typing import List, Dict

import yaml


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


def convert_nogps_request_to_google_request(request: List[Dict]):
    log_request(request)

    return request


def log_request(request):
    pass
