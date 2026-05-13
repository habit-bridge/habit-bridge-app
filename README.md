# Habit Bridge Demo

Habit Bridge Demo는 XRP Ledger(XRPL)의 Escrow를 활용해 사용자가 습관형성 챌린지에 참여하고, 성공/실패 결과에 따라 보증금이 기부로 귀속되는 MVP 앱입니다.

이 저장소는 Android Compose 기반 클라이언트 구현과 MVP 요구사항/화면/API 문서를 함께 관리합니다. 프로젝트의 상세한 동작과 화면 구성은 `docs` 문서를 기준으로 확인하면 됩니다.

## 문서

- [요구사항 문서](./docs/requirements.md): MVP 범위, 핵심 유저 시나리오, 기능/비기능 요구사항
- [API 스펙](./docs/api-spec.md): 서버 HTTP API 계약, 요청/응답 형식, 에러 코드
- [앱 화면 이동 흐름](./docs/screen-flow.md): 화면 ID(S00~S60), 네비게이션 맵, 시나리오별 이동 흐름
- [화면별 UI 요소 명세](./docs/ui-spec.md): 각 화면에 필요한 UI 요소, 상태별 UI, 공통 컴포넌트

## MVP 주요 기능

- 회원가입/로그인 및 프로필 관리
- 챌린지 생성/조회
- 종료되지 않은 챌린지 참여
- Xaman 또는 XRPL 지갑을 통한 EscrowCreate 서명 흐름
- 날짜가 포함된 사진 인증 업로드
- 성공/실패 판정 및 Donation 이벤트 표시
- 기부 금액 TOP 10 / 챌린지 성공 TOP 10 랭킹

## 개발 참고

앱 구현 시에는 다음 순서로 문서를 보면 흐름을 파악하기 쉽습니다.

1. `docs/requirements.md`에서 전체 요구사항 확인
2. `docs/screen-flow.md`에서 앱 이동 흐름 확인
3. `docs/ui-spec.md`에서 화면별 UI 요소 확인
4. `docs/api-spec.md`에서 각 화면이 호출할 API 확인
