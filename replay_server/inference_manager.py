import requests
import numpy as np
import cv2
from utils import encode_image, send_to_backend
from config import TRITON_URL

def infer_frame(camera_id, frame, timestamp):
    # 전처리 예시 (Resize, RGB 등)
    resized = cv2.resize(frame, (224, 224))
    rgb = cv2.cvtColor(resized, cv2.COLOR_BGR2RGB)
    input_tensor = np.transpose(rgb, (2, 0, 1)).astype(np.float32) / 255.0
    input_tensor = np.expand_dims(input_tensor, axis=0)

    payload = {
        "inputs": [{
            "name": "input",
            "shape": input_tensor.shape,
            "datatype": "FP32",
            "data": input_tensor.tolist()
        }]
    }

    response = requests.post(f"{TRITON_URL}/v2/models/model_name/infer", json=payload)
    result = response.json()

    send_to_backend(camera_id, timestamp, result)
