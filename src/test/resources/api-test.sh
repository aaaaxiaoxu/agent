#!/bin/bash

# 设置API基础URL
BASE_URL="http://localhost:8080"

# 测试健康检查接口
echo "Testing Health Endpoint..."
curl -s -w "\nStatus: %{http_code}\n" "$BASE_URL/health"
echo ""

# 测试用户登录接口
echo "Testing User Login..."
curl -s -w "\nStatus: %{http_code}\n" -X POST \
  -H "Content-Type: application/json" \
  -d '{"userAccount":"testuser","userPassword":"password123"}' \
  "$BASE_URL/user/login"
echo ""

# 测试获取用户信息接口
echo "Testing Get User By ID..."
curl -s -w "\nStatus: %{http_code}\n" "$BASE_URL/user/get?id=1"
echo ""

# 测试发送验证码接口
echo "Testing Send Verification Code..."
curl -s -w "\nStatus: %{http_code}\n" -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}' \
  "$BASE_URL/email/verification-code/send"
echo ""

# 测试验证码校验接口
echo "Testing Check Verification Code..."
curl -s -w "\nStatus: %{http_code}\n" -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","code":"123456"}' \
  "$BASE_URL/email/verification-code/check"
echo "" 