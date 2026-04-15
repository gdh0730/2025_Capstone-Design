import os

def concatenate_java_files(root_dir, output_path):
    """
    root_dir 하위의 모든 .java 파일을 찾아서,
    순차적으로 내용을 output_path에 이어붙인다.
    각 파일의 앞부분에는 파일명을 표시하여 구분한다.
    """
    # 1) .java 파일 경로를 저장할 리스트
    java_files = []

    # 2) os.walk를 사용해 하위 디렉토리까지 재귀적으로 순회
    for dirpath, dirnames, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.lower().endswith('.java'):
                full_path = os.path.join(dirpath, filename)
                java_files.append(full_path)

    # 3) 파일 목록을 알파벳(또는 경로) 순으로 정렬
    java_files = sorted(java_files)

    # 4) 결과를 쓸 출력 파일 열기 (UTF-8 인코딩)
    with open(output_path, 'w', encoding='utf-8') as out_f:
        for java_path in java_files:
            # 4-1) 파일명(또는 경로)을 구분자처럼 출력
            out_f.write(f"==== {os.path.relpath(java_path, root_dir)} ====\n")
            # 4-2) 실제 자바 소스 코드 읽어서 붙이기
            try:
                with open(java_path, 'r', encoding='utf-8') as in_f:
                    content = in_f.read()
                    out_f.write(content)
            except UnicodeDecodeError:
                # 혹시 다른 인코딩으로 작성된 파일이 있을 경우
                with open(java_path, 'r', encoding='latin-1') as in_f:
                    content = in_f.read()
                    out_f.write(content)

            # 4-3) 파일 간 구분을 위한 빈 줄 추가
            out_f.write("\n\n")

    print(f"모든 .java 파일을 '{output_path}'로 병합하여 저장했습니다.")


if __name__ == "__main__":
    # 예시: 이 부분을 원하는 경로로 수정하세요.
    # - root_dir: .java 파일이 들어있는 최상위 디렉토리
    # - output_path: 최종 결과물을 저장할 파일 경로(예: merged_java.txt)
    root_dir = "./src/main/java/com/poseman/backend"
    output_path = "merged_java.txt"

    concatenate_java_files(root_dir, output_path)
