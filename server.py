import logging
from typing import List, Dict

from fastapi import FastAPI, Request

import utils
from geocoding import get_address_from_lating, get_lating_from_address
from wifi import get_location_from_wifi

logging.basicConfig(filename='gps.log', level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s', encoding='utf-16')
app = FastAPI()


@app.get('/')
def root(request: Request):
    logging.info(f"Got keepalive from {request.client.host}")
    return {'is_alive': True}


@app.post('/wifi')
def wifi(request: Request, access_points: List[Dict]):
    converted_request = utils.convert_nogps_request_to_google_request(access_points)
    logging.info(f"Wifi list from: {request.client.host} is: {access_points}")

    return get_location_from_wifi(converted_request)


@app.get('/geocoding/{address}')
def geocoding(request: Request, address: str):
    logging.info(f"{request.client.host} ask coordinates for {address}")
    return get_lating_from_address(address)


@app.get('/reverse-geocoding/{lating}')
def reverse_geocoding(request: Request, lating: str):
    logging.info(f"{request.client.host} ask Getting street name for {lating}")
    return get_address_from_lating(lating)


@app.post('/send-logs')
def send_logs(request: Request):
    logging.info(f"{request.client.host} sent logs")
    utils.save_user_logs(request)


if __name__ == '__main__':
    import uvicorn

    logging.info(f"starting NoGPSNavigator server")
    uvicorn.run(app, host="0.0.0.0", port=8080)
