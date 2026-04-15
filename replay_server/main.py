from fastapi import FastAPI
from stream_handler import start_streams
from scheduler import start_scheduler

app = FastAPI()

@app.on_event("startup")
async def startup_event():
    await start_streams()       # 모든 RTSP 스트림을 비동기로 실행
    start_scheduler()           # 주기적인 추론 스케줄링 실행
    print("Application started and streams are running.")
    