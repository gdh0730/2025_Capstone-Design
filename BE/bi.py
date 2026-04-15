def binary_division(dividend, divisor):
    # 피제수와 제수를 리스트로 변환
    dividend = list(map(int, dividend))
    divisor = list(map(int, divisor))
    divisor_len = len(divisor)

    # 나눗셈 수행
    for i in range(len(dividend) - divisor_len + 1):
        # 현재 비트가 1인 경우에만 XOR 수행
        if dividend[i] == 1:
            for j in range(divisor_len):
                dividend[i + j] ^= divisor[j]
        print(dividend)        
                
    # 나머지 반환
    remainder = dividend[-(divisor_len - 1):]
    return ''.join(map(str, remainder))

# 예제 실행
dividend = "11000111010"  # 피제수
divisor = "1001"         # 제수
remainder = binary_division(dividend, divisor)
print(f"나머지: {remainder}")
