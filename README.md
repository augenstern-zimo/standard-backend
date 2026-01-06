# standard-backend
后端开发基础框架配置

# 相关技术栈

# 后端开发系统

## 📖 项目简介

基于 Spring Boot 3.0.7 + JDK 17 构建的企业级后端应用，采用分层架构设计，集成了主流中间件和技术栈。

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.0.7 | 基础框架 |
| JDK | 17 | Java 开发工具包 |
| MySQL | 8.0+ | 关系型数据库 |
| MyBatis-Plus | 3.5.3 | ORM 框架 |
| Redis | 7.0+ | 缓存中间件 |
| RabbitMQ | 3.x | 消息队列 |
| MinIO | 最新版 | 对象存储 |
| Lombok | - | 代码简化工具 |
| Hutool | 5.8.18 | Java 工具类库 |

## 📁 项目结构

```
backend/
├── src/main/java/cloud/zimometaverse/backend/
│   ├── BackendApplication.java          # 启动类
│   ├── common/                          # 通用类
│   │   ├── Result.java                 # 统一响应结果
│   │   └── ResultCode.java             # 响应状态码枚举
│   ├── config/                          # 配置类
│   │   ├── JacksonConfig.java          # Jackson 序列化配置
│   │   ├── Knife4jConfig.java          # API 文档配置
│   │   ├── MinioConfig.java            # MinIO 配置
│   │   ├── MyBatisPlusConfig.java      # MyBatis-Plus 配置
│   │   ├── RabbitMQConfig.java         # RabbitMQ 配置
│   │   ├── RedisConfig.java            # Redis 配置
│   │   └── WebConfig.java              # Web 配置（跨域等）
│   ├── controller/                      # 控制器层
│   ├── service/                         # 服务层
│   ├── mapper/                          # 数据访问层
│   ├── domain/                          # 领域模型
│   │   ├── entity/                     # 实体类
│   │   ├── dto/                        # 数据传输对象
│   │   └── vo/                         # 视图对象
│   ├── exception/                       # 异常处理
│   │   ├── BizException.java           # 业务异常
│   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   └── util/                            # 工具类
├── src/main/resources/
│   ├── application.yml                  # 主配置文件
│   ├── application-dev.yml              # 开发环境配置
│   ├── application-test.yml             # 测试环境配置
│   ├── application-prod.yml             # 生产环境配置
│   ├── logback-spring.xml               # 日志配置
│   └── mapper/                          # MyBatis XML 映射文件
├── pom.xml                              # Maven 依赖配置
├── README.md                            # 项目说明
├── CONFIG_README.md                     # 配置文件详细说明
└── .gitignore                           # Git 忽略文件

```

## 🚀 快速开始

### 1. 环境准备

确保已安装以下软件：

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+
- RabbitMQ 3.x
- MinIO（可选）

### 2. 克隆项目

```bash
git clone <repository-url>
cd backend
```

### 3. 配置数据库

```sql
-- 创建数据库
CREATE DATABASE smart_learning_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 修改配置文件 application-dev.yml 中的数据库连接信息
```

### 4. 启动中间件

#### Redis
```bash
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

#### RabbitMQ
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

#### MinIO
```bash
docker run -d --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  minio/minio server /data --console-address ":9001"
```

### 5. 启动应用

```bash
# 方式一：使用 Maven
mvn spring-boot:run

# 方式二：使用 IDE
# 直接运行 BackendApplication.java

# 方式三：打包后运行
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 6. 访问应用

- 应用访问地址：http://localhost:8080/api
- 健康检查：http://localhost:8080/api/actuator/health

## 📚 开发规范

### 分层架构

```
Controller（控制器层）
    ↓ 接收请求、参数验证、结果返回
Service（服务层）
    ↓ 业务逻辑处理、事务控制
Mapper（数据访问层）
    ↓ 数据库操作
Database（数据库）
```

### 职责划分

- **Controller**：仅负责接收参数、调用 Service、返回结果，禁止编写业务逻辑
- **Service**：核心业务逻辑、事务控制、调用 Mapper
- **Mapper**：仅负责数据访问，不承载业务逻辑

### 命名规范

- **实体类（Entity）**：对应数据库表，使用名词，如 `User`、`Order`
- **DTO（Data Transfer Object）**：数据传输对象，用于接收请求参数，如 `UserCreateDTO`
- **VO（View Object）**：视图对象，用于返回给前端的数据，如 `UserVO`
- **Service 接口**：使用 `I` 前缀，如 `IUserService`
- **Service 实现类**：使用 `Impl` 后缀，如 `UserServiceImpl`

### 代码示例

#### Controller 示例

```java
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<UserVO> createUser(@Validated @RequestBody UserCreateDTO dto) {
        UserVO user = userService.createUser(dto);
        return Result.success(user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询用户")
    public Result<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }
}
```

#### Service 示例

```java
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserCreateDTO dto) {
        log.info("创建用户，username={}", dto.getUsername());
        
        // 1. 校验用户是否存在
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())
        );
        if (existUser != null) {
            throw new BizException(ResultCode.USER_ALREADY_EXISTS);
        }
        
        // 2. 创建用户
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        userMapper.insert(user);
        
        log.info("用户创建成功，userId={}", user.getId());
        
        // 3. 返回结果
        return convertToVO(user);
    }
    
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
```

## 🔐 安全规范

### 1. 敏感信息管理

- ❌ 禁止在代码中硬编码敏感信息（密码、密钥、Token 等）
- ✅ 使用环境变量或配置中心管理敏感信息
- ✅ 生产环境配置必须通过环境变量注入

### 2. 参数验证

- ✅ 所有对外接口必须进行参数校验（使用 `@Validated`）
- ✅ 不信任任何外部输入
- ✅ 使用白名单而非黑名单

### 3. 异常处理

- ✅ 使用统一的业务异常 `BizException`
- ✅ 异常消息必须有明确的业务语义
- ❌ 禁止在异常消息中暴露敏感信息

### 4. 日志规范

- ✅ 关键业务必须记录日志
- ✅ 日志需包含足够的上下文信息
- ❌ 禁止在日志中打印敏感信息（密码、身份证号等）

## 📊 监控与运维

### 健康检查

```bash
# 检查应用健康状态
curl http://localhost:8080/api/actuator/health

# 查看详细信息（需要认证）
curl http://localhost:8080/api/actuator/health/details
```

### 日志管理

- 日志文件位置：`logs/application.log`
- 错误日志位置：`logs/application.log.error`
- 日志保留天数：30 天（可配置）

### 性能监控

- Actuator 指标：http://localhost:8080/api/actuator/metrics
- Prometheus 指标：http://localhost:8080/api/actuator/prometheus

## 🐛 常见问题

详细的常见问题和解决方案请参考 [CONFIG_README.md](CONFIG_README.md)

## 📝 更新日志

### v1.0.0 (2026-01-06)

- ✅ 初始化项目结构
- ✅ 集成 Spring Boot 3.0.7 + JDK 17
- ✅ 集成 MySQL + MyBatis-Plus
- ✅ 集成 Redis + RabbitMQ + MinIO
- ✅ 配置多环境支持（dev、test、prod）
- ✅ 实现统一响应格式
- ✅ 实现全局异常处理
- ✅ 集成 Knife4j API 文档
- ✅ 配置日志系统

## 👥 团队

- **开发团队**：智能学习云帆平台开发组
- **联系方式**：dev@zimometaverse.cloud

## 📄 License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

---

**注意事项：**

1. 开发环境配置文件中的敏感信息仅供开发使用，生产环境必须修改
2. 部署前请仔细阅读 [CONFIG_README.md](CONFIG_README.md)
3. 遵循开发规范，保持代码质量
4. 定期更新依赖版本，关注安全漏洞

**祝开发愉快！🎉**

