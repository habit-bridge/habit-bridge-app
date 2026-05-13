# API 스펙 (MVP)

본 문서는 [requirements.md](./requirements.md)를 구현하기 위한 **HTTP API 계약**이다. 구현 시 모듈 경로(`auth`, `users`, `challenges`, …)와 1:1로 맞추기 쉽도록 리소스 단위로 정리한다.

## 1. 공통

### 1.1 Base URL / Version

- Base: `https://atlantic-acid-boneless.ngrok-free.dev//api/v1` 

### 1.2 형식

- `Content-Type: application/json` (파일 업로드 제외)
- 날짜/시간: ISO 8601 문자열, UTC 권장 (예: `2026-05-13T00:00:00.000Z`)
- 금액(XRP): 문자열 기반 십진수 권장 (부동소수 오차 방지), 예 `"10.5"` — 응답도 동일

### 1.3 인증

- 로그인 성공 시 **JWT 액세스 토큰** 발급.
- 보호된 엔드포인트: 헤더 `Authorization: Bearer <access_token>`.

### 1.4 에러 응답 (권장 형식)

모든 오류는 가능하면 아래 JSON을 사용한다.

```json
{
  "statusCode": 400,
  "error": "Bad Request",
  "code": "CHALLENGE_ALREADY_ENDED",
  "message": "Human readable message",
  "details": {}
}
```

자주 쓰는 `code` 예시:

| code | 의미 |
|------|------|
| `UNAUTHORIZED` | 토큰 없음/만료 |
| `FORBIDDEN` | 권한 없음 |
| `NOT_FOUND` | 리소스 없음 |
| `CONFLICT` | 중복 참여 등 |
| `CHALLENGE_ALREADY_ENDED` | 종료된 챌린지 참여 시도 |
| `PARTICIPATION_INVALID_STATE` | 에스크로/인증 단계와 상태 불일치 |
| `ESCROW_VALIDATION_FAILED` | 제출된 트랜잭션/페이로드 불일치 |

### 1.5 페이징 (목록 API 공통, 선택)

쿼리: `cursor` (opaque), `limit` (기본 20, 최대 100)  
응답:

```json
{
  "items": [],
  "nextCursor": null
}
```

---

## 2. Auth (`/auth`)

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| POST | `/auth/register` | 회원가입 | 불필요 |
| POST | `/auth/login` | 로그인, 토큰 발급 | 불필요 |

### 2.1 POST `/auth/register`

**Request body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | string | ✓ | 이메일 (unique) |
| password | string | ✓ | 평문 (전송은 HTTPS) |
| displayName | string |  | 기부 표기명, 없으면 이후 프로필에서 설정 |

**Response 201**

```json
{
  "id": "uuid",
  "email": "user@example.com",
  "displayName": "홍길동"
}
```

### 2.2 POST `/auth/login`

**Request body**

| 필드 | 타입 | 필수 |
|------|------|------|
| email | string | ✓ |
| password | string | ✓ |

**Response 200**

```json
{
  "accessToken": "jwt",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## 3. Users (`/users`)

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| GET | `/users/me` | 내 프로필 | 필요 |
| PATCH | `/users/me` | 프로필 수정 (`displayName`, XRPL 주소 등) | 필요 |

### 3.1 GET `/users/me`

**Response 200**

```json
{
  "id": "uuid",
  "email": "user@example.com",
  "displayName": "홍길동",
  "xrplAddress": "rXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
}
```

### 3.2 PATCH `/users/me`

**Request body** (부분 갱신)

| 필드 | 타입 | 필수 |
|------|------|------|
| displayName | string | |
| xrplAddress | string \| null | |

**Response 200**: GET `/users/me`와 동일 형식.

---

## 4. Challenges (`/challenges`)

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| POST | `/challenges` | 챌린지 생성 | 필요 |
| GET | `/challenges` | 목록 (필터) | 선택 (비로그인 공개 조회 가능 정책 시 불필요) |
| GET | `/challenges/{challengeId}` | 단건 | 선택 |

### 4.1 POST `/challenges`

**Request body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| title | string | ✓ | 제목 |
| description | string | ✓ | 소개 |
| startDate | string (date) | ✓ | 시작일 `YYYY-MM-DD` |
| durationWeeks | integer | ✓ | 기간(주), ≥ 1 |
| verificationFrequency | object | ✓ | 인증 주기 (아래 스키마) |
| verificationMethodDescription | string | ✓ | 인증 방법 설명 |
| depositXrp | string | ✓ | 보증금 XRP |

**`verificationFrequency` 스키마 (MVP)**

```json
{
  "type": "DAILY"
}
```

```json
{
  "type": "WEEKLY",
  "timesPerWeek": 3
}
```

추가 `type`은 구현 확장 시 명세에 추가.

**Response 201**

```json
{
  "id": "uuid",
  "creatorId": "uuid",
  "title": "아침 러닝",
  "description": "...",
  "startDate": "2026-05-01",
  "durationWeeks": 4,
  "verificationFrequency": { "type": "WEEKLY", "timesPerWeek": 1 },
  "verificationMethodDescription": "신문 날짜가 보이게 촬영",
  "depositXrp": "10.000000",
  "status": "SCHEDULED",
  "endsAt": "2026-05-29T23:59:59.999Z",
  "createdAt": "2026-05-13T07:00:00.000Z"
}
```

### 4.2 GET `/challenges`

**Query**

| 파라미터 | 설명 |
|----------|------|
| status | `SCHEDULED` \| `ACTIVE` \| `ENDED` (쉼표 구분 가능) |
| participating | `me` (로그인 시 내가 참여 중인 것만, 선택) |

**Response 200**: 챌린지 요약 객체 배열 (필드는 생성 응답과 동일하되 목록에 불필요한 필드 생략 가능).

### 4.3 GET `/challenges/{challengeId}`

**Response 200**: POST 생성 응답과 동일 + 선택적으로 집계 (`activeParticipantCount` 등).

---

## 5. Participations (`/participations`)

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| POST | `/participations` | 챌린지 참여 생성 (에스크로 대기) | 필요 |
| GET | `/participations/me` | 내 참여 목록 | 필요 |
| GET | `/participations/{participationId}` | 참여 단건 (+ 슬롯 진행 요약) | 필요 |

### 5.1 POST `/participations`

**Request body**

| 필드 | 타입 | 필수 |
|------|------|------|
| challengeId | uuid | ✓ |

**비즈니스 규칙**

- 챌린지가 `ENDED`가 아니어야 함.
- 동일 `(userId, challengeId)` 중복 불가 → `409 CONFLICT`.

**Response 201**

```json
{
  "id": "uuid",
  "userId": "uuid",
  "challengeId": "uuid",
  "status": "PENDING_DEPOSIT",
  "createdAt": "2026-05-13T07:00:00.000Z"
}
```

### 5.2 GET `/participations/me`

**Query**: `status` 필터 선택.

**Response 200**: 참여 객체 배열.

### 5.3 GET `/participations/{participationId}`

**Response 200**

```json
{
  "id": "uuid",
  "userId": "uuid",
  "challengeId": "uuid",
  "status": "ACTIVE",
  "verificationSummary": {
    "totalSlots": 12,
    "completedSlots": 3,
    "nextOpenSlotIndex": 4
  },
  "escrow": {
    "ledgerTxHash": "ABC...",
    "preparedAt": "2026-05-13T07:05:00.000Z",
    "confirmedAt": "2026-05-13T07:06:00.000Z"
  },
  "createdAt": "2026-05-13T07:00:00.000Z"
}
```

---

## 6. Escrow (`/escrow`) — EscrowPrepare / Submit

요구사항상 **Prepare**와 **Submit(확인)** API 분리.

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| POST | `/escrow/prepare` | 미서명 EscrowCreate / Xumm payload 생성 | 필요 |
| POST | `/escrow/submit` | 서명 결과 제출 및 검증·ledger 확인 트리거 | 필요 |

### 6.1 POST `/escrow/prepare`

**Request body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| participationId | uuid | ✓ | 대상 참여 |
| walletProvider | string | | `XUMM` \| `BLOB` 등 (클라이언트 힌트) |

**Response 200** (제공 방식에 따라 한 가지 또는 둘 다)

```json
{
  "participationId": "uuid",
  "prepareId": "uuid",
  "expiresAt": "2026-05-13T08:00:00.000Z",
  "xumm": {
    "uuid": "xumm-payload-uuid",
    "next": { "always": "https://xumm.app/..." }
  },
  "xrpl": {
    "unsignedTxJson": {},
    "unsignedTxBlob": "hex-if-needed"
  }
}
```

- 서버는 참여 상태가 `PENDING_DEPOSIT`일 때만 허용.
- 동일 참여에 대한 **재요청 멱등**: 동일 유효 `prepareId` 반환 또는 새 prepare 정책은 구현 문서에 명시.

### 6.2 POST `/escrow/submit`

**Request body** (하나만 채우거나 구현에서 통합)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| participationId | uuid | ✓ | |
| prepareId | uuid | ✓ | prepare와 매칭 |
| signedTxBlob | string | △ | XRPL 직접 서명 시 |
| xummPayloadUuid | string | △ | Xumm 완료 후 |

**Response 202** (비동기 확인 권장)

```json
{
  "participationId": "uuid",
  "status": "DEPOSIT_SUBMITTED",
  "ledgerTxHash": null,
  "message": "Submission accepted; confirmation pending"
}
```

**Response 200** (동기 확인 완료 시)

```json
{
  "participationId": "uuid",
  "status": "ACTIVE",
  "ledgerTxHash": "..."
}
```

서버 검증 항목은 요구사항 4.4와 동일: 금액·수신자·조건·멱등.

---

## 7. Verifications (`/verifications`)

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| GET | `/participations/{participationId}/verification-slots` | 인증 슬롯 목록 | 필요 |
| POST | `/participations/{participationId}/verifications` | 사진 업로드 | 필요 (`multipart/form-data`) |

### 7.1 GET `.../verification-slots`

참여가 `ACTIVE`일 때만 의미 있음.

**Response 200**

```json
{
  "slots": [
    {
      "slotIndex": 0,
      "windowStart": "2026-05-01T00:00:00.000Z",
      "windowEnd": "2026-05-07T23:59:59.999Z",
      "status": "OPEN"
    }
  ]
}
```

`status`: `OPEN` \| `SUBMITTED` \| `MISSED` \| `PENDING_REVIEW` (후속).

### 7.2 POST `.../verifications`

**Request**: `multipart/form-data`

| 필드 | 타입 | 필수 |
|------|------|------|
| file | binary | ✓ (image/*) |
| slotIndex | integer | ✓ |

**Response 201**

```json
{
  "id": "uuid",
  "participationId": "uuid",
  "slotIndex": 0,
  "imageUrl": "https://.../signed-or-public-path",
  "submittedAt": "2026-05-05T12:00:00.000Z",
  "status": "RECORDED"
}
```

---

## 8. Donations (`/donations`)

MVP에서 정산은 스케줄러가 수행하므로, 클라이언트는 **조회** 중심.

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| GET | `/donations` | 기부 이벤트 목록 (필터) | 선택 |
| GET | `/donations/me` | 내 기부 이력 | 필요 |

**Query (GET `/donations`)**

| 파라미터 | 설명 |
|----------|------|
| challengeId | 챌린지별 |
| userId | 특정 사용자 |
| attribution | `USER` \| `SERVICE` |

**Response 200**

```json
{
  "items": [
    {
      "id": "uuid",
      "challengeId": "uuid",
      "participationId": "uuid",
      "amountXrp": "10.000000",
      "attribution": "USER",
      "attributedDisplayName": "홍길동",
      "userId": "uuid",
      "createdAt": "2026-05-30T10:00:00.000Z"
    }
  ]
}
```

`attribution: "SERVICE"` 인 경우 `userId`는 `null`, `attributedDisplayName`은 서비스 고정명 스냅샷.

---

## 9. Rankings (`/rankings`)

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| GET | `/rankings/donations/top` | 기부 금액 TOP 10 | 불필요 |
| GET | `/rankings/success/top` | 챌린지 성공 횟수 TOP 10 | 불필요 |

### 9.1 GET `/rankings/donations/top`

**Response 200**

```json
{
  "ranked": [
    { "rank": 1, "userId": "uuid", "displayName": "홍길동", "totalDonationXrp": "120.500000" }
  ]
}
```

### 9.2 GET `/rankings/success/top`

**Response 200**

```json
{
  "ranked": [
    { "rank": 1, "userId": "uuid", "displayName": "홍길동", "successCount": 15 }
  ]
}
```

동률 처리 규칙(같은 금액/건수)은 구현 시 정의; 문서화 권장.

---

## 10. Scheduler / 내부 작업 (선택)

정산·ledger 재확인은 HTTP로 노출하지 않고 워커에서 실행하는 것을 권장한다. 운영 목적으로만 최소 노출할 경우:

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| POST | `/internal/settlements/run` | 종료 챌린지 정산 트리거 | Admin secret / 미노출 |

MVP 문서에서는 **내부 모듈 전용**으로 두고 공개 OpenAPI에는 포함하지 않아도 된다.

---

## 11. 요구사항 매핑 (추적용)

| 요구사항 (requirements) | API |
|---------------------------|-----|
| 회원가입/로그인 | §2 |
| 프로필·표시 이름 | §3 |
| 챌린지 생성/조회 | §4 |
| 미종료 챌린지 참여 | §5 |
| EscrowCreate Prepare/Submit | §6 |
| 날짜 포함 사진 인증 | §7 |
| 성공/실패 후 기부 기록 | §8 + 내부 정산 |
| TOP 10 × 2 | §9 |
