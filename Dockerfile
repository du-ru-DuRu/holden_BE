# OpenJDK 17 이미지로부터 기본 이미지를 설정합니다.
FROM openjdk:17-jdk

# 빌드된 JAR 파일의 경로를 ARG로 설정합니다.
ARG JAR_FILE=build/libs/aiStudy-0.0.1-SNAPSHOT.jar

# 빌드된 JAR 파일을 이미지의 루트 디렉토리로 복사합니다.
COPY ${JAR_FILE} aiStudy-0.0.1-SNAPSHOT.jar

# Firebase 서비스 계정 키 파일을 이미지의 루트 디렉토리로 복사합니다.
COPY src/main/resources/holden-fcbe7-firebase-adminsdk-wwt0x-51580532f0.json src/main/resources/

# 컨테이너가 시작될 때 실행할 명령어를 설정합니다. 여기서는 JAR 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "aiStudy-0.0.1-SNAPSHOT.jar"]
