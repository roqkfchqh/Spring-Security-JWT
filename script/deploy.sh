#!/usr/bin/env bash

# ------- 설정 값 --------
AWS_REGION="ap-northeast-2"
UBUNTU_AMI_ID="ami-0d5bb3742db8fc264"
INSTANCE_TYPE="t2.micro"
KEY_NAME="roqkf"
SECURITY_GROUP_NAME="lcy-ec2-sg"
SECURITY_GROUP_DESC="Allow 8080 for Java App"
REPO_URL="https://github.com/roqkfchqh/Spring-Security-JWT"
APP_DIR="/home/ubuntu/app"
PORT=8080
TAG="Homework"
GRADLE_VERSION="8.5"
# ------- 설정 값 --------

# 1) 보안 그룹 생성 & 8080, SSH 허용
SG_EXISTS=$(aws ec2 describe-security-groups \
  --group-names "$SECURITY_GROUP_NAME" \
  --region "$AWS_REGION" 2>/dev/null | jq -r '.SecurityGroups[0].GroupId' || true)

if [ -z "$SG_EXISTS" ]; then
  aws ec2 create-security-group \
    --group-name "$SECURITY_GROUP_NAME" \
    --description "$SECURITY_GROUP_DESC" \
    --region "$AWS_REGION" >/dev/null
else
  echo "보안 그룹 '$SECURITY_GROUP_NAME'이 이미 존재합니다."
fi

aws ec2 authorize-security-group-ingress \
  --group-name "$SECURITY_GROUP_NAME" \
  --protocol tcp \
  --port "$PORT" \
  --cidr 0.0.0.0/0 \
  --region "$AWS_REGION" >/dev/null 2>/dev/null || true

aws ec2 authorize-security-group-ingress \
  --group-name "$SECURITY_GROUP_NAME" \
  --protocol tcp \
  --port 22 \
  --cidr 211.224.58.200/32 \
  --region "$AWS_REGION" >/dev/null 2>/dev/null || true

# 2) user‑data 스크립트 작성
read -r -d '' USER_DATA <<EOF
#!/bin/bash
set -e

# --- 로그 설정 ---
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1
echo "User Data 스크립트 시작: \$(date)"

# --- Gradle 버전 정의 ---
GRADLE_VERSION="$GRADLE_VERSION"

# --- 시스템 업데이트 ---
apt-get update -y
apt-get upgrade -y

# --- GPG 키 저장 및 레포 등록 ---
apt-get install -y gnupg curl
curl -fsSL https://packages.adoptium.net/artifactory/api/gpg/key/public \
  | gpg --dearmor -o /usr/share/keyrings/temurin-archive-keyring.gpg

CODENAME=\$(awk -F= '/^VERSION_CODENAME/{print \$2}' /etc/os-release)
echo "deb [signed-by=/usr/share/keyrings/temurin-archive-keyring.gpg] \
  https://packages.adoptium.net/artifactory/deb \${CODENAME} main" \
  | tee /etc/apt/sources.list.d/temurin.list

# --- 패키지 설치 ---
apt-get update -y
DEBIAN_FRONTEND=noninteractive apt-get install -y \
  temurin-17-jdk git unzip wget

# --- Gradle 설치 ---
wget https://services.gradle.org/distributions/gradle-\${GRADLE_VERSION}-bin.zip -P /tmp
unzip -d /opt/gradle /tmp/gradle-\${GRADLE_VERSION}-bin.zip
ln -s /opt/gradle/gradle-\${GRADLE_VERSION}/bin/gradle /usr/bin/gradle

# --- 앱 디렉토리 준비 ---
mkdir -p ${APP_DIR}
chown ubuntu:ubuntu ${APP_DIR}

# --- 소스 클론 & 빌드 ---
sudo -u ubuntu bash -lc "
  git clone ${REPO_URL} ${APP_DIR} &&
  cd ${APP_DIR} &&
  gradle build -x test
"

# --- 백그라운드 실행 ---
JAR_FILE="${APP_DIR}/build/libs/jwt-0.0.1-SNAPSHOT.jar"
if [ -f "\$JAR_FILE" ]; then
  sudo -u ubuntu bash -lc "nohup java -DJWT_KEY=YjlmODZjOWVlYzlhNGE5MGI3NmE2M2E1ZmJkZGU3ZTFjODNmNmQyOTlkZmU0ZTc0OGU0NWNkOTFjYmZkNzQyNw== -jar \$JAR_FILE &"
else
  echo "JAR 파일이 존재하지 않습니다: \$JAR_FILE" >> /var/log/user-data.log
  exit 1
fi
EOF

# 3) EC2 인스턴스 생성
INSTANCE_ID=$(aws ec2 run-instances \
  --image-id "$UBUNTU_AMI_ID" \
  --instance-type "$INSTANCE_TYPE" \
  --key-name "$KEY_NAME" \
  --security-groups "$SECURITY_GROUP_NAME" \
  --user-data "$USER_DATA" \
  --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=$TAG}]" \
  --query 'Instances[0].InstanceId' \
  --output text \
  --region "$AWS_REGION")

echo "인스턴스 생성 중 ($INSTANCE_ID)"

# 4) running 대기
aws ec2 wait instance-running \
  --instance-ids "$INSTANCE_ID" \
  --region "$AWS_REGION"

# 5) 퍼블릭 DNS 확인
PUBLIC_DNS=$(aws ec2 describe-instances \
  --instance-ids "$INSTANCE_ID" \
  --query 'Reservations[0].Instances[0].PublicDnsName' \
  --output text \
  --region "$AWS_REGION")

if [ -z "$PUBLIC_DNS" ] || [ "$PUBLIC_DNS" == "None" ]; then
  PUBLIC_IP=$(aws ec2 describe-instances \
    --instance-ids "$INSTANCE_ID" \
    --query 'Reservations[0].Instances[0].PublicIpAddress' \
    --output text \
    --region "$AWS_REGION")
  echo "EC2($INSTANCE_ID) 준비 완료 http://$PUBLIC_IP:$PORT"
else
  echo "EC2($INSTANCE_ID) 준비 완료 http://$PUBLIC_DNS:$PORT"
fi
