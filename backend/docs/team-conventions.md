# 네이밍, 스타일 가이드

### 기본적으로 Google Code Style Guide를 따른다.
### 검증 혹은 boolean 반환 메서드는 긍정문을 사용한다.
**Bad Case**
```java
public class ValidationExample {

  // 검증 메서드
  public boolean isNotValid(String value) {
    return value == null || value.trim().isEmpty();
  }

  // boolean 반환 메서드
  public boolean hasNoPermissions(User user) {
    return user.getPermissions().isEmpty();
  }
  
  public void validator() {
    // 사용 예시 (이중 부정)
    if (!validationExample.isNotValid(userInput)) {
      // 유효한 경우의 로직
    }
  }
}
```
**Good Case**
```java
public class ValidationExample {

  // 검증 메서드
  public boolean isValid(String value) {
    return value != null && !value.trim().isEmpty();
  }

  // boolean 반환 메서드
  public boolean hasPermissions(User user) {
    return !user.getPermissions().isEmpty();
  }
  
  // 사용 예시
  public void validator(String userInput) {
      if (userInput.isValid()) {
      }
  }
}
```
### dto라는 네이밍은 너무 포괄적이므로, request, response로 나누어 작성한다.

# Java Code Style
- 기본적으로 Google Code Style을 따른다.
- 변경사항
    - Tab Size : 4
    - Indent : 4
    - Continuation indent : 8

# 코드 컨벤션

###  클래스와 선언부 필드 사이는 개행한다.
**Bad Case**
```java
public class TeamConvention {
    private final String a;
    private final String b;
}
```

**Good Case**
```java
public class TeamConvention {
    
    private final String a;
    private final String b;
}
```

### `@Valid` + `@NotBlank`, `@NotNull` 를 통한 입력값 검증을 진행한다.

### Enum의 값을 정의할 때, 후행 쉼표를 사용한다.
**Bad Case**
```java
public enum DayOfWeek {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY
}
```
**Good Case**
```java
public enum DayOfWeek {
  SUNDAY,
  MONDAY,
  TUESDAY,
  WEDNESDAY,
  THURSDAY,
  FRIDAY,
  SATURDAY,
}
```

- Lombok 라이브러리를 사용하되, 그 범위를 제한한다.
    - `@Getter`, `@NoArgsConstructor`, `@RequiredArgsConstructor`, `@Builder`**(필드값 4개 이상 시 사용가능)**
- `@Data` 어노테이션을 사용하지 않는다. 해당 어노테이션 내부에 있는 값을 사용할 일이 있다면, 일일이 명시한다.

**Bad Case**
```java
@Data
public class UserDto {
    private String id;
    private String name;
    private int age;
}
```

**Good Case**
```java
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String name;
    private int age;
}
```
### 어노테이션이 여럿 있는 경우, 중요한 것을 가장 상단에 배치한다. (Lombok의 어노테이션을 가장 하단에 배치한다)
**Bad Case**
```java
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_info")
public class example {
    
}
```

**Good Case**
```java
@Entity
@Table(name = "user_info")
@NoArgsConstructor
@Getter
public class example {
    
}
```

# 클래스, 변수 및 메서드 관리

### dto는 record로 생성한다.
**Bad Case**
```java
@Getter
@RequireArgsConstructor
public class UserResponse {
    
    private final String name;
    private final String email;
}
```

**Good Case**
```java
public record UserResponse(String name, String email) {
}
```

### 메서드의 순서는`public`을 위에 두되, `private`의 경우 사용하는 메서드 아래에 둔다.
**Bad Case**
```java
publicA () {
    usingA();
}
publicB () {} 

publicC () {} 

private usingA() {}
```

**Good Case**
```java
publicA () {
    usingA();
}
private usingA() {}

publicB () {} 

publicC () {}
```

### 필드 변수는`static final` → `final` → `instance variable` 순서로 정의한다.

### 메서드 매개변수, 지역 변수에는 `final`을 사용한다.
**Bad Case**
```java
public void add(int a, int b) {
    int sum = a + b;
}
```
**Good Case**
```java
public void add(final int a, final int b) {
    final int sum = a + b;
}
```
### 초기화되지 않은 상태로 변수를 선언하지 않는다


# 엔티티

### `@Table`과`@Column`으로 테이블명, 컬럼명을 기본적으로 명시하지 않되, 필요에 따라 논의 후 적용한다.

#### 예약어인 경우 복수형 또는 동의어를 찾아 작성한다 (예: `order` → `orders`)

### `@Column`어노테이션의`nullable`은 false인 경우 표기한다.
**Good Case**
```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}
```

**Bad Case**
```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
```

### `@Embeddable`내부에는`@Embedded`을 사용하지 않는다.
**Bad Case**
```java
@Embeddable
public class Address { 
    private String street;
    private String city;
    
    // 문제점: Embeddable 내부에 Embedded 객체를 중첩시킴
    @Embedded
    private ZipCode zipCode; 
}

@Embeddable
public class ZipCode { 
    private String zipCodeValue;
    private String countryCode;
}
```

**Good Case**
```java
@Embeddable
public class Address { 
    private String street;
    private String city;
    private String zipCodeValue;
    private String countryCode; 
}
```

# 서비스 & 컨트롤러 구조 및 관리

### `@RestController`에서`@RequestMapping`을 최상단에 사용하지 않는다.
- 사용하지 않는 경우, 코드 중복이 늘어나지만 각각의 메서드를 따로 관리할 수 있다.

###  클래스 최상단에 `@Transactional(readOnly = true)`을 작성한다.
**Bad Case**
```java
@Service 
public class ProductService {

    @Transactional(readOnly = true)
    public Product getProductDetails(Long productId) {
        // ... 상품 조회 로직
    }
    
    // 쓰기 트랜잭션이 필요한 메서드에만 별도로 어노테이션을 오버라이드
    @Transactional
    public void createProduct(Product product) {
        // ... 상품 생성 로직
    }
}
```
**Good Case**
```java
@Service
@Transactional(readOnly = true) 
public class ProductService {

    // 읽기 전용 트랜잭션이 적용됨
    public Product getProductDetails(Long productId) {
        // ... 상품 조회 로직
    }
    
    // 쓰기 트랜잭션이 필요한 메서드에만 별도로 어노테이션을 오버라이드
    @Transactional
    public void createProduct(Product product) {
        // ... 상품 생성 로직
    }
}
```
