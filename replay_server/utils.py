import requests
import numpy as np
from config import BACKEND_EVENT_API

def send_to_backend(camera_id, timestamp, inference_result):
    top_index = int(np.argmax(inference_result["outputs"][0]["data"]))
    score = float(np.max(inference_result["outputs"][0]["data"]))

    data = {
        "camera_id": camera_id,
        "timestamp": timestamp.isoformat(),
        "event_index": top_index,
        "score": score
    }

    requests.post(BACKEND_EVENT_API, json=data)
