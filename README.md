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

- **开发团队**：zimo
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

# 配置文件说明文档

## 📁 配置文件结构

```
src/main/resources/
├── application.yml              # 主配置文件（通用配置）
├── application-dev.yml          # 开发环境配置
├── application-test.yml         # 测试环境配置
├── application-prod.yml         # 生产环境配置
└── logback-spring.xml           # 日志配置文件
```

---

## 🚀 快速开始

### 1. 环境激活

通过以下方式之一激活环境：

**方式一：修改 pom.xml（推荐）**
```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
</profiles>
```

**方式二：启动参数**
```bash
java -jar backend.jar --spring.profiles.active=dev
```

**方式三：环境变量**
```bash
export SPRING_PROFILES_ACTIVE=dev
java -jar backend.jar
```

### 2. 开发环境准备

#### 2.1 MySQL
```sql
-- 创建数据库
CREATE DATABASE smart_learning_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'smart_learning'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON smart_learning_dev.* TO 'smart_learning'@'localhost';
FLUSH PRIVILEGES;
```

#### 2.2 Redis
```bash
# Docker 启动
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 或使用本地安装
redis-server
```

#### 2.3 RabbitMQ
```bash
# Docker 启动（带管理界面）
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# 访问管理界面：http://localhost:15672
# 默认账号：guest / guest
```

#### 2.4 MinIO
```bash
# Docker 启动
docker run -d --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  minio/minio server /data --console-address ":9001"

# 访问控制台：http://localhost:9001
# 默认账号：minioadmin / minioadmin
```

---

## 📋 配置详解

### 主配置文件 (application.yml)

包含所有环境通用的配置：
- Spring Boot 基础配置
- Jackson 序列化配置
- MyBatis-Plus 全局配置
- Knife4j API 文档配置
- Actuator 监控配置
- 日志配置
- 服务器配置

### 开发环境 (application-dev.yml)

**特点：**
- 使用本地服务（localhost）
- 开启详细日志（DEBUG 级别）
- 开启 SQL 日志
- 简化的安全配置
- 允许本地跨域访问

**适用场景：**
- 本地开发调试
- 单元测试
- 集成测试

### 测试环境 (application-test.yml)

**特点：**
- 使用环境变量配置敏感信息
- 适中的日志级别（INFO）
- 连接池适中配置
- 启用健康检查

**适用场景：**
- 集成测试环境
- QA 测试
- 预发布验证

### 生产环境 (application-prod.yml)

**特点：**
- 所有敏感信息必须通过环境变量配置
- 启用 SSL/TLS
- 优化的连接池配置
- 最小化日志输出（WARN 级别）
- 严格的安全策略
- API 文档默认关闭
- 健康检查不显示详情

**适用场景：**
- 生产环境部署
- 高可用部署

---

## 🔐 安全配置指南

### 1. 敏感信息管理

**开发环境：** 可以硬编码在配置文件中（仅限开发）

**测试/生产环境：** 必须使用环境变量

```bash
# 数据库配置
export DB_HOST=your-db-host
export DB_PORT=3306
export DB_NAME=smart_learning_prod
export DB_USERNAME=your_username
export DB_PASSWORD=your_password

# Redis 配置
export REDIS_HOST=your-redis-host
export REDIS_PASSWORD=your_redis_password

# RabbitMQ 配置
export RABBITMQ_HOST=your-rabbitmq-host
export RABBITMQ_USERNAME=your_username
export RABBITMQ_PASSWORD=your_password

# MinIO 配置
export MINIO_ENDPOINT=https://your-minio-endpoint
export MINIO_ACCESS_KEY=your_access_key
export MINIO_SECRET_KEY=your_secret_key

# JWT 配置
export JWT_SECRET=your-strong-jwt-secret-key-at-least-32-characters

# 前端地址
export FRONTEND_URL=https://your-frontend-domain.com
```

### 2. JWT 密钥生成

```bash
# 生成强随机密钥（推荐）
openssl rand -base64 64

# 或使用 Java
java -cp . -c "System.out.println(java.util.UUID.randomUUID().toString() + java.util.UUID.randomUUID().toString());"
```

### 3. SSL/TLS 配置

生产环境务必启用：
- MySQL SSL 连接
- Redis SSL 连接
- RabbitMQ SSL 连接
- MinIO HTTPS 访问

---

## 📊 连接池配置说明

### Hikari 连接池参数

| 参数 | 开发环境 | 测试环境 | 生产环境 | 说明 |
|------|---------|---------|---------|------|
| minimum-idle | 5 | 10 | 20 | 最小空闲连接数 |
| maximum-pool-size | 20 | 30 | 50 | 最大连接数 |
| connection-timeout | 30s | 30s | 30s | 连接超时时间 |
| idle-timeout | 10min | 10min | 10min | 空闲连接存活时间 |
| max-lifetime | 30min | 30min | 30min | 连接最大存活时间 |

### Redis 连接池参数

| 参数 | 开发环境 | 测试环境 | 生产环境 | 说明 |
|------|---------|---------|---------|------|
| max-active | 20 | 30 | 50 | 最大活跃连接数 |
| max-idle | 10 | 15 | 20 | 最大空闲连接数 |
| min-idle | 2 | 5 | 10 | 最小空闲连接数 |
| max-wait | 5s | 5s | 5s | 最大等待时间 |

---

## 📝 日志配置说明

### 日志级别

- **开发环境：** DEBUG（方便调试）
- **测试环境：** INFO（记录关键信息）
- **生产环境：** WARN（只记录警告和错误）

### 日志文件

- **ALL 日志：** `logs/application.log`（所有级别）
- **ERROR 日志：** `logs/application.log.error`（仅错误）

### 日志滚动策略

- 单文件最大：100MB
- 保留天数：30天（生产 60天）
- 总大小上限：10GB（生产 20GB）
- 压缩归档：自动 gzip 压缩

### 异步日志

- 使用 AsyncAppender 提升性能
- 队列大小：512（ALL）/ 256（ERROR）
- 不丢弃日志（discardingThreshold=0）

---

## 🔧 自定义业务配置

在配置文件中添加了 `business` 配置段，用于业务相关配置：

### JWT 配置
```yaml
business:
  jwt:
    secret: your-secret-key           # JWT 密钥
    expiration: 604800                # 过期时间（秒）
    token-prefix: Bearer              # Token 前缀
    token-header: Authorization       # Header 名称
```

### 文件上传配置
```yaml
business:
  file:
    allowed-types:                    # 允许的文件类型
      - image/jpeg
      - image/png
    max-size: 10485760               # 最大大小（字节）
    path-prefix: dev                 # 存储路径前缀
```

### 跨域配置
```yaml
business:
  cors:
    allowed-origins:                  # 允许的源
      - http://localhost:3000
    allowed-methods:                  # 允许的方法
      - GET
      - POST
    allow-credentials: true          # 允许携带凭证
```

---

## 🏥 健康检查

### Actuator 端点

访问地址：`http://localhost:8080/api/actuator`

可用端点：
- `/actuator/health` - 健康检查
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 性能指标
- `/actuator/prometheus` - Prometheus 指标

### 健康检查内容

- MySQL 连接状态
- Redis 连接状态
- RabbitMQ 连接状态
- 磁盘空间检查

---

## 🐛 常见问题

### 1. 数据库连接失败

**问题：** `Communications link failure`

**解决：**
- 检查 MySQL 是否启动
- 检查数据库地址、端口、用户名、密码
- 检查防火墙规则
- 确认数据库已创建

### 2. Redis 连接失败

**问题：** `Unable to connect to Redis`

**解决：**
- 检查 Redis 是否启动
- 检查 Redis 地址、端口、密码
- 如果使用 Docker，检查端口映射

### 3. RabbitMQ 连接失败

**问题：** `Connection refused`

**解决：**
- 检查 RabbitMQ 是否启动
- 检查端口 5672 是否开放
- 检查用户名、密码、虚拟主机配置

### 4. MinIO 连接失败

**问题：** `The specified bucket does not exist`

**解决：**
- 登录 MinIO 控制台手动创建 bucket
- 或在代码中添加自动创建 bucket 逻辑

### 5. 日志文件未生成

**问题：** 日志目录不存在

**解决：**
```bash
# 创建日志目录
mkdir -p logs/dev
mkdir -p logs/test
mkdir -p /data/logs/smart-learning  # 生产环境
```

---

## 📦 Docker 部署建议

### docker-compose.yml 示例

```yaml
version: '3.8'

services:
  backend:
    image: smart-learning-backend:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - DB_USERNAME=root
      - DB_PASSWORD=${DB_PASSWORD}
      - REDIS_HOST=redis
      - REDIS_PASSWORD=${REDIS_PASSWORD}
      - RABBITMQ_HOST=rabbitmq
      - RABBITMQ_USERNAME=${RABBITMQ_USERNAME}
      - RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD}
      - MINIO_ENDPOINT=http://minio:9000
      - MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY}
      - MINIO_SECRET_KEY=${MINIO_SECRET_KEY}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - mysql
      - redis
      - rabbitmq
      - minio
    volumes:
      - ./logs:/data/logs/smart-learning

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=${DB_PASSWORD}
      - MYSQL_DATABASE=smart_learning_prod
    volumes:
      - mysql-data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis-data:/data

  rabbitmq:
    image: rabbitmq:3-management
    environment:
      - RABBITMQ_DEFAULT_USER=${RABBITMQ_USERNAME}
      - RABBITMQ_DEFAULT_PASS=${RABBITMQ_PASSWORD}
    volumes:
      - rabbitmq-data:/var/lib/rabbitmq

  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    environment:
      - MINIO_ROOT_USER=${MINIO_ACCESS_KEY}
      - MINIO_ROOT_PASSWORD=${MINIO_SECRET_KEY}
    volumes:
      - minio-data:/data

volumes:
  mysql-data:
  redis-data:
  rabbitmq-data:
  minio-data:
```

---

## 📖 相关文档

- [Spring Boot 配置文档](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [MyBatis-Plus 文档](https://baomidou.com/)
- [Knife4j 文档](https://doc.xiaominfo.com/)
- [Logback 文档](https://logback.qos.ch/manual/)

---

## ✅ 检查清单

### 开发环境启动前
- [ ] MySQL 已启动并创建数据库
- [ ] Redis 已启动
- [ ] RabbitMQ 已启动
- [ ] MinIO 已启动并创建 bucket
- [ ] 激活 dev profile

### 生产环境部署前
- [ ] 所有敏感信息使用环境变量
- [ ] JWT 密钥已生成并配置
- [ ] 启用 SSL/TLS
- [ ] 关闭或保护 API 文档
- [ ] 配置日志目录权限
- [ ] 配置健康检查
- [ ] 配置监控告警
- [ ] 进行压力测试
- [ ] 准备回滚方案

---

**版本：** v1.0.0  
**更新时间：** 2026-01-06  
**维护团队：** zimo



