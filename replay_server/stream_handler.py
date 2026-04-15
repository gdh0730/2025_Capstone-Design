import cv2
import asyncio
from config import CAMERA_STREAMS
from datetime import datetime

frame_buffers = {}

async def capture_frames(name, url):
    cap = cv2.VideoCapture(url)
    while True:
        ret, frame = cap.read()
        if ret:
            timestamp = datetime.utcnow()
            frame_buffers[name] = (frame, timestamp)
        await asyncio.sleep(0.05)  # 20fps
    cap.release()

async def start_streams():
    await asyncio.gather(*(capture_frames(name, url) for name, url in CAMERA_STREAMS.items()))
