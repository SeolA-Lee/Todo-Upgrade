## 📋 TODO LIST
이번 미션은 TODO 리스트를 구현하는 미션입니다.

아래 요구사항에 맞춰 API 를 구상하고 구현합니다.

PR 작성시 현재 리드미를 삭제하고 요구사항 명세서를 **반드시** 작성해주세요  

## 🎯 미션 요구사항

### 🔑 로그인 기능 (선택)

사용자는 이메일(email)과 비밀번호(password)를 통해 로그인해야 합니다.

로그인에 성공한 사용자만 TODO 리스트에 접근할 수 있습니다.

### 📌 TODO 리스트 CRUD (필수)

**TODO 등록**

사용자는 TODO를 등록할 수 있습니다.
- TODO는 한번에 하나만 등록합니다.

각 TODO는 최대 3개의 세부 할 일을 포함할 수 있습니다.
- 세부 할 일은 한번에 하나만 등록합니다.

**TODO 조회**

TODO 리스트 조회 시, 페이지네이션을 통해 한 번에 10개씩 보여줘야 합니다.
- 조회시 세부 할 일도 함께 조회해야 합니다.

**TODO 삭제**

사용자는 TODO 및 세부 할 일을 완료 시 개별로 삭제할 수 있습니다.

사용자는 여러 개의 TODO 및 세부 할 일을 한 번에 선택하여 삭제할 수 있습니다.

### ⚠️ 예외 처리

- 사용자의 모든 입력에 대해 예외 처리를 반드시 구현해야 합니다.
- 예외 상황 발생 시 적절한 에러 메시지를 표시하고, 입력을 다시 받아야 합니다.

```java
public class UserNotfoundException extends ApplicationException {
    public UserNotfoundException() {
        super(HttpStatus.NOT_FOUND.value(), "유저를 찾을 수 없습니다.");
    }
}
```

### 🧪 테스트 코드 작성

- 반드시 모든 기능에 대한 테스트 코드를 작성해야 합니다.
- 예외 상황에 대한 테스트 케이스도 포함되어야 합니다.

**테스트 코드 예시**
```java
class UserManageUsecaseTest {

    private UserSaveService userSaveService;
    private UserValidateService userValidateService;
    private PasswordUtil passwordUtil;
    private UserManageUsecase userManageUsecase;

    @BeforeEach
    void setUp() {
        userSaveService = mock(UserSaveService.class);
        userValidateService = mock(UserValidateService.class);
        passwordUtil = mock(PasswordUtil.class);

        userManageUsecase = new UserManageUsecase(userSaveService, userValidateService, passwordUtil);
    }

    @Test
    @DisplayName("회원가입 시 비밀번호는 암호화되고 저장되어야 한다")
    void testRegisterUserSuccess() {
        // given
        String email = "test@example.com";
        String rawPassword = "myPassword123";
        String encryptedPassword = "encrypted123";
        String nickname = "Tester";

        UserRegisterDto dto = new UserRegisterDto(email, rawPassword, nickname);

        when(passwordUtil.encrypt(rawPassword)).thenReturn(encryptedPassword);

        // when
        userManageUsecase.register(dto);

        // then
        verify(userValidateService, times(1)).validateDuplication(email);
        verify(passwordUtil, times(1)).encrypt(rawPassword);
        verify(userSaveService, times(1)).save(any(User.class));
    }
}
```



