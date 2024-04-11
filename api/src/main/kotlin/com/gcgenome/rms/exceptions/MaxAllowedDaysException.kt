package com.gcgenome.rms.exceptions

class MaxAllowedDaysException() :RuntimeException("날짜 범위는 최대 7일까지 조회 가능하며, 요청 등록 종료일은 요청 등록 시작일보다 이전일 수 없습니다.")