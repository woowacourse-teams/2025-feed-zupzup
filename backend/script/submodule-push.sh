#!/bin/bash

# 사용법: ./script/submodule-push.sh "커밋 메시지"

# 커밋 메시지 체크
if [ -z "$1" ]; then
    echo "❌ 커밋 메시지를 입력해주세요!"
    echo "사용법: $0 \"커밋 메시지\""
    exit 1
fi

COMMIT_MESSAGE="$1"
SUBMODULE_PATH="./backend-submodule"

echo "🚀 서브모듈 푸시 시작..."

# 서브모듈 디렉토리로 이동
cd "$SUBMODULE_PATH" || {
    echo "❌ 서브모듈 디렉토리를 찾을 수 없습니다: $SUBMODULE_PATH"
    exit 1
}

# main 브랜치로 체크아웃
echo "📝 main 브랜치로 체크아웃..."
git checkout main

# 최신 상태로 업데이트
echo "⬇️ 최신 변경사항 가져오기..."
git pull origin main

# 변경사항 확인
if ! git diff --quiet || ! git diff --cached --quiet; then
    # 변경사항 스테이징
    echo "📋 변경사항 스테이징..."
    git add .

    # 커밋
    echo "💾 커밋: $COMMIT_MESSAGE"
    git commit -m "$COMMIT_MESSAGE"

    # 푸시
    echo "⬆️ 푸시 중..."
    if git push origin main; then
        echo "✅ 서브모듈 푸시 완료!"

        # 메인 프로젝트로 돌아가서 서브모듈 참조 업데이트
        cd ..
        echo "🔄 메인 프로젝트에서 서브모듈 참조 업데이트..."
        git add "$SUBMODULE_PATH"
        git commit -m "chore: update submodule reference"
        echo "✅ 모든 작업 완료!"
    else
        echo "❌ 푸시 실패!"
        exit 1
    fi
else
    echo "📝 변경사항이 없습니다."
fi
