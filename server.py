from typing import List, Dict

import uvicorn
from fastapi import FastAPI

from wifi import get_location_from_wifi

app = FastAPI()


@app.get('/')
def root():
    return {'is_alive': True}


@app.post('/wifi')
def wifi(access_points: List[Dict]):
    return get_location_from_wifi(access_points)


if __name__ == '__main__':
    uvicorn.run(app, host="127.0.0.1", port=8080)
