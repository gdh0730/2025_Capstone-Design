import threading
import time
from inference_manager import infer_frame
from stream_handler import frame_buffers

def start_scheduler():
    def job():
        while True:
            for name, (frame, ts) in frame_buffers.items():
                threading.Thread(target=infer_frame, args=(name, frame, ts)).start()
            time.sleep(1)  # 1초 간격 추론
    threading.Thread(target=job).start()
