from typing import List, Dict

from fastapi import FastAPI

from geocoding import get_address_from_lating, get_lating_from_address
from utils import convert_nogps_request_to_google_request
from wifi import get_location_from_wifi

app = FastAPI()


@app.get('/')
def root():
    return {'is_alive': True}


@app.post('/wifi')
def wifi(access_points: List[Dict]):
    converted_request = convert_nogps_request_to_google_request(access_points)

    return get_location_from_wifi(converted_request)


@app.get('/geocoding/{address}')
def geocoding(address: str):
    return get_lating_from_address(address)


@app.get('/reverse-geocoding/{lating}')
def reverse_geocoding(lating: str):
    return get_address_from_lating(lating)


if __name__ == '__main__':
    import uvicorn

    uvicorn.run(app, host="127.0.0.1", port=8080)
