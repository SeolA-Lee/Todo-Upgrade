## 📌 ERD
![todolist-erd](https://github.com/user-attachments/assets/ef9bb766-9f43-49cc-b9fa-5dbddca10948)

## 📃 요구사항 명세서

### 💁🏻 회원
|내용|메소드|URI|
|-----|-----|-----|
|회원가입|POST|`/member/register`|
|로그인|POST|`/member/login`|

### ☑️ TODO 리스트 CRUD
|내용|메소드|URI|
|-----|-----|-----|
|TODO 등록|POST|`/todo`|
|세부 할 일 등록|POST|`/todo/{todo_id}/detail`|
|TODO 조회|GET|`/todo`|
|TODO 상태 변경|PATCH|`/todo/{todo_id}`|
|세부 할 일 상태 변경|PATCH|`/todo/detail/{detail_id}`|
|TODO 삭제|DELETE|`/todo?ids=1,2,3`|
|세부 할 일 삭제|DELETE|`/todo/detail?ids=1,2,3`|
