# JapanLearn Hub

## Overview

JapanLearn Hub is a Japanese learning web application built with Spring Boot, providing a comprehensive system for managing courses, lessons, vocabulary, grammar, and tracking user learning progress.

## System Architecture

### Technologies Used
- **Backend**: Spring Boot 3.5.5
- **Java Version**: 21
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with JWT
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Mapping**: MapStruct
- **Validation**: Jakarta Validation

### Project Structure

```
src/main/java/com/catsocute/japanlearn_hub/
├── common/                   
│   ├── constant/           
│   ├── controller/          
│   ├── dto/                 
│   ├── entity/              
│   ├── enums/          
│   ├── exception/         
│   └── utils/                
├── modules/                  
│   ├── auth/                
│   ├── blog/                
│   ├── course/             
│   ├── datainitializer/    
│   ├── exam/                
│   ├── interaction/         
│   ├── lesson/             
│   ├── profile/            
│   ├── user/               
│   └── configuration/       
└── JapanlearnHubApplication.java
```



## API Endpoints

### Base URL
```
http://localhost:8080/jlearnhub
```

### 1. Authentication API (`/api/v1/auth`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/token` | Login and get JWT token |
| POST | `/introspect` | Validate token |

### 2. User Management API (`/api/v1/users`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new user |
| GET | `/` | Get all users (with pagination) |
| GET | `/me` | Get current user information |
| PUT | `/change-password` | Change password |
| PUT | `/{userId}` | Update user roles |
| DELETE | `/{userId}` | Delete user |

### 3. Profile Management API (`/api/v1/profiles`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all profiles (with pagination) |
| GET | `/me` | Get current user profile |
| GET | `/{profileId}` | Get profile by ID |
| GET | `/user/{userId}` | Get profile by User ID |
| PUT | `/` | Update current profile |

### 4. Course Management API (`/api/v1/courses`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new course |
| GET | `/` | Get all courses (with pagination) |
| GET | `/{courseId}` | Get course by ID |
| GET | `/user/{userId}` | Get courses by User ID |
| PUT | `/{courseId}` | Update course |
| DELETE | `/{courseId}` | Delete course |

#### Lesson Management in Course (`/api/v1/courses/{courseId}`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/lesson` | Create new lesson in course |
| GET | `/lesson` | Get lessons by course |

### 5. Lesson Management API (`/api/v1/lessons`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all lessons (with pagination) |
| GET | `/{lessonId}` | Get lesson by ID |
| GET | `/type/{type}` | Get lessons by type (GRAMMAR, VOCABULARY) |
| GET | `/parent/{parentLessonId}` | Get child lessons by parent lesson |
| PUT | `/{lessonId}` | Update lesson |
| DELETE | `/{lessonId}` | Delete lesson |

### 6. Vocabulary Management API (`/api/v1/vocabularies`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new vocabulary |
| GET | `/` | Get all vocabularies (with pagination) |
| GET | `/{vocabularyId}` | Get vocabulary by ID |
| GET | `/level/{level}` | Get vocabularies by JLPT level |
| GET | `/type/{type}` | Get vocabularies by type (HIRAGANA, KATAKANA, KANJI, OTHER) |
| PUT | `/{vocabularyId}` | Update vocabulary |
| DELETE | `/{vocabularyId}` | Delete vocabulary |

### 7. Grammar Management API (`/api/v1/grammars`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new grammar |
| GET | `/` | Get all grammars (with pagination) |
| GET | `/{grammarId}` | Get grammar by ID |
| GET | `/level/{level}` | Get grammars by JLPT level |
| PUT | `/{grammarId}` | Update grammar |
| DELETE | `/{grammarId}` | Delete grammar |

### 8. Role Management API (`/api/v1/roles`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new role |
| GET | `/` | Get all roles (with pagination) |
| GET | `/{roleName}` | Get role by name |
| PUT | `/{id}` | Update role |
| DELETE | `/{name}` | Delete role by name |
| DELETE | `/` | Delete all roles |

### 9. Permission Management API (`/api/v1/permissions`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new permission |
| GET | `/` | Get all permissions (with pagination) |
| GET | `/{permissionId}` | Get permission by ID |
| DELETE | `/{permissionId}` | Delete permission |

### 10. Achievement Management API (`/api/v1/achievements`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all achievements (with pagination) |
| GET | `/me` | Get current user achievements |
| GET | `/{achievementId}` | Get achievement by ID |
| PUT | `/{achievementId}` | Update achievement by ID |
| PUT | `/type/{achievementType}` | Update achievement by type |
| DELETE | `/{achievementId}` | Delete achievement by ID |
| DELETE | `/type/{achievementType}` | Delete achievement by type |

### 11. User Level Management API (`/api/v1/levels`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all user levels (with pagination) |
| GET | `/{levelId}` | Get level by ID |
| DELETE | `/{levelId}` | Delete level |

### 12. User Stats Management API (`/api/v1/user-stats`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all user stats (with pagination) |
| GET | `/me` | Get current user stats |
| GET | `/{userId}` | Get user stats by User ID |
| PUT | `/` | Update current user stats |

### 13. Blog Management API (`/api/v1/blogs`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new blog |
| GET | `/` | Get all blogs (with pagination) |
| GET | `/{blogId}` | Get blog by ID |
| GET | `/author/{authorId}` | Get blogs by author ID |
| PUT | `/{blogId}` | Update blog |
| DELETE | `/{blogId}` | Delete blog |

### 14. Exam Management API (`/api/v1/exams`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new exam |
| GET | `/` | Get all exams (with pagination) |
| GET | `/{examId}` | Get exam by ID |
| GET | `/course/{courseId}` | Get exams by course ID |
| PUT | `/{examId}` | Update exam |
| DELETE | `/{examId}` | Delete exam |
| POST | `/{examId}/questions:add` | Add questions to exam |
| POST | `/{examId}/questions:remove` | Remove questions from exam |

### 15. Question Management API (`/api/v1/questions`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new question |
| GET | `/` | Get all questions (with pagination) |
| GET | `/{questionId}` | Get question by ID |
| GET | `/category/{category}` | Get questions by category |
| GET | `/difficulty/{difficulty}` | Get questions by difficulty |
| GET | `/jlpt/{jlptLevel}` | Get questions by JLPT level |
| GET | `/type/{questionType}` | Get questions by type |
| GET | `/search?keyword={keyword}` | Search questions by content |
| GET | `/random` | Get random questions (with optional filters) |
| PUT | `/{questionId}` | Update question |
| DELETE | `/{questionId}` | Delete question |

### 16. Question Option Management API (`/api/v1/question-options`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/{optionId}` | Get question option by ID |
| GET | `/question/{questionId}` | Get all options for a question |
| GET | `/question/{questionId}/correct` | Get correct options for a question |
| PUT | `/{optionId}` | Update question option |
| DELETE | `/{optionId}` | Delete question option |
| DELETE | `/question/{questionId}` | Delete all options for a question |

### 17. Comment Management API (`/api/v1/comments`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create new comment |
| GET | `/` | Get all comments (with pagination) |
| GET | `/{commentId}` | Get comment by ID |
| GET | `/target/{targetType}/{targetId}` | Get all comments for a target |
| GET | `/target/{targetType}/{targetId}/parent` | Get parent comments for a target (excluding replies) |
| GET | `/parent/{parentCommentId}/replies` | Get replies for a comment |
| GET | `/author/{authorId}` | Get comments by author |
| PUT | `/{commentId}` | Update comment |
| DELETE | `/{commentId}` | Delete comment |

## Pagination Parameters

All GET list APIs support pagination with the following parameters:

- `page`: Page number (default: 1)
- `size`: Number of items per page (default: 10)
- `field`: Sort field (default: createDate)
- `direction`: Sort direction - ASC/DESC (default: DESC)

## Enums Reference

### Question Category
- `VOCABULARY` - Vocabulary questions
- `GRAMMAR` - Grammar questions
- `READING` - Reading comprehension questions
- `LISTENING` - Listening questions
- `KANJI` - Kanji questions

### Question Difficulty
- `EASY` - Easy level
- `MEDIUM` - Medium level
- `HARD` - Hard level

### Question Type
- `MULTIPLE_CHOICE` - Multiple choice questions
- `TRUE_FALSE` - True/False questions
- `FILL_IN_THE_BLANK` - Fill in the blank questions
- `MATCHING` - Matching questions
- `SHORT_ANSWER` - Short answer questions

### JLPT Level
- `N5` - JLPT N5 level
- `N4` - JLPT N4 level
- `N3` - JLPT N3 level
- `N2` - JLPT N2 level
- `N1` - JLPT N1 level

### Comment Target Type
- `BLOG` - Comment on a blog post
- `QUESTION` - Comment on a question
- `COURSE` - Comment on a course
- `LESSON` - Comment on a lesson

## Security

- **JWT Authentication**: JWT token for authentication
- **Password Encryption**: Passwords encrypted with Spring Security
- **Role-based Access Control**: Role-based permissions
- **Input Validation**: Input validation with Jakarta Validation


