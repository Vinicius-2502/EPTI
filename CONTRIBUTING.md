# Contributing to EPTI Backend API

Thank you for your interest in contributing to the EPTI Backend API! This guide will help you get started with contributing to this project.

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+
- Docker (optional)
- Git

### Setup

1. **Fork the repository**
   ```bash
   # Fork the repository on GitHub, then clone your fork
   git clone https://github.com/your-username/EPTI-BackEnd.git
   cd EPTI-BackEnd
   ```

2. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/original-owner/EPTI-BackEnd.git
   ```

3. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **Set up your development environment**
   ```bash
   # Install dependencies
   ./mvnw dependency:resolve
   
   # Run the application in development mode
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=development
   ```

## 📋 Development Guidelines

### Code Style

This project uses EditorConfig for consistent code formatting. Your IDE should automatically apply the formatting rules.

- Use 4 spaces for indentation in Java files
- Use 2 spaces for YAML and JSON files
- Trim trailing whitespace
- Insert final newline at end of files

### Commit Messages

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
type(scope): description

[optional body]

[optional footer]
```

Examples:
- `feat(auth): add JWT token refresh functionality`
- `fix(user): resolve email validation issue`
- `docs(api): update authentication documentation`
- `test(user): add integration tests for user service`

### Types of Changes

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw clean verify jacoco:report

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run tests with specific profile
./mvnw test -Dspring.profiles.active=test
```

### Test Structure

- Unit tests: Test individual components in isolation
- Integration tests: Test component interactions
- Repository tests: Test database operations
- Controller tests: Test REST endpoints

### Writing Tests

1. **Unit Tests**
   ```java
   @ExtendWith(MockitoExtension.class)
   class UserServiceTest {
       
       @Mock
       private UserRepository userRepository;
       
       @InjectMocks
       private UserService userService;
       
       @Test
       void shouldCreateUser() {
           // Given
           RegisterRequest request = new RegisterRequest();
           // ... setup
           
           // When
           User result = userService.register(request);
           
           // Then
           assertThat(result).isNotNull();
           assertThat(result.getUsername()).isEqualTo(request.getUsername());
       }
   }
   ```

2. **Integration Tests**
   ```java
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   @ActiveProfiles("test")
   @Testcontainers
   class UserControllerIntegrationTest {
       
       @Container
       static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
       
       @Autowired
       private TestRestTemplate restTemplate;
       
       @Test
       void shouldRegisterUser() {
           // Test implementation
       }
   }
   ```

## 🐛 Bug Reports

### Reporting Bugs

1. Check existing issues to avoid duplicates
2. Use the bug report template
3. Provide clear steps to reproduce
4. Include environment details (OS, Java version, etc.)
5. Add relevant logs or screenshots

### Bug Report Template

```markdown
## Bug Description
Brief description of the bug

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: [e.g., Ubuntu 22.04]
- Java version: [e.g., 21]
- Spring Boot version: [e.g., 3.3.0]
- Database: [e.g., PostgreSQL 15]

## Additional Context
Any other relevant information
```

## 💡 Feature Requests

### Proposing Features

1. Check existing issues and feature requests
2. Use the feature request template
3. Describe the problem you're trying to solve
4. Explain why this feature would be valuable
5. Consider implementation suggestions

### Feature Request Template

```markdown
## Feature Description
Brief description of the feature

## Problem Statement
What problem does this solve?

## Proposed Solution
How should this work?

## Alternatives Considered
Other approaches you considered

## Additional Context
Any other relevant information
```

## 🔍 Code Review Process

### Review Guidelines

1. **Functionality**: Does the code work as intended?
2. **Testing**: Are there adequate tests?
3. **Documentation**: Is the code well-documented?
4. **Style**: Does the code follow project conventions?
5. **Performance**: Are there any performance concerns?
6. **Security**: Are there any security implications?

### Review Checklist

- [ ] Code follows project style guidelines
- [ ] Tests are included and passing
- [ ] Documentation is updated
- [ ] No breaking changes (unless necessary)
- [ ] Security implications are considered
- [ ] Performance impact is acceptable
- [ ] Error handling is appropriate

## 📖 Documentation

### Documentation Types

- **API Documentation**: Generated with OpenAPI/Swagger
- **Code Documentation**: JavaDoc comments
- **README**: Project setup and usage instructions
- **Architecture**: System design and architecture decisions

### Documentation Guidelines

- Keep documentation up-to-date
- Use clear and concise language
- Include code examples where helpful
- Document configuration options
- Explain design decisions

## 🚀 Release Process

### Versioning

This project follows [Semantic Versioning](https://semver.org/):

- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Release Steps

1. Update version in `pom.xml`
2. Update `CHANGELOG.md`
3. Create release tag
4. Deploy to production
5. Update documentation

## 🤝 Community Guidelines

### Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on what is best for the community
- Show empathy towards other community members

### Getting Help

- Check the documentation first
- Search existing issues
- Ask questions in discussions
- Join our community channels

## 📧 Contact

For questions about contributing:

- Create an issue for bugs or feature requests
- Start a discussion for general questions
- Contact maintainers for urgent matters

Thank you for contributing to EPTI Backend API! 🎉
