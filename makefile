.PHONY: all build test clean run

all: build test clean run   # all 실행 시 네 개 다 실행

# ===== 빌드 =====
build:
	@echo "🚀 Gradle 빌드 시작..."
	./gradlew clean build -x test
	@echo "✅ Gradle 빌드 완료!"

# ===== 테스트 =====
test:
	@echo "🧪 테스트 실행..."
	./gradlew test
	@echo "✅ 테스트 완료!"

# ===== 클린 =====
clean:
	@echo "🧹 클린 작업 실행..."
	./gradlew clean
	@echo "✅ 클린 완료!"

# ===== 실행 =====
run:
	@echo "▶️ 애플리케이션 실행..."
	./gradlew bootRun

# ===== 변수 =====
SSH_KEY := ~/.ssh/id_ed25519_poppang   # SSH 키 경로
JAR_NAME := poppang-be-test-0.0.1-SNAPSHOT.jar
LOCAL_JAR_PATH := build/libs/$(JAR_NAME)
REMOTE_USER := poppang
REMOTE_HOST := 183.103.19.203
REMOTE_DIR := /home/poppang
REMOTE_PATH := $(REMOTE_DIR)/
CONTAINER_NAME := poppang-beta

# ===== 서버 배포 =====
deploy: build
	@echo "📦 배포 시작..."
	# 1. JAR 업로드
	scp -i $(SSH_KEY) $(LOCAL_JAR_PATH) $(REMOTE_USER)@$(REMOTE_HOST):$(REMOTE_PATH)
	# 2. 서버 컨테이너 재실행
	ssh -i $(SSH_KEY) $(REMOTE_USER)@$(REMOTE_HOST) '\
		docker rm -f $(CONTAINER_NAME) || true && \
		docker run -d -p 8500:8500 \
			--name $(CONTAINER_NAME) \
			--network poppang-net \
			-v $(REMOTE_DIR)/$(JAR_NAME):/app/app.jar \
			-e SERVER_PORT=8500 \
			-e SPRING_DATASOURCE_URL="jdbc:mysql://mysql_test:3306/poppang_test_db?serverTimezone=UTC&characterEncoding=UTF-8" \
			-e SPRING_DATASOURCE_USERNAME=poppang_test \
			-e SPRING_DATASOURCE_PASSWORD="poppang1q2w3e4r!" \
			java-runner \
	'
	@echo "✅ 배포 완료!"