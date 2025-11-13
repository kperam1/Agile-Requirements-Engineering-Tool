# Comment Section Implementation for User Stories

## Overview
Added a complete comment system to the user story feature, allowing users to add, view, update, and delete comments on user stories.

## Backend Changes

### 1. New Model: Comment
**File**: `backend/src/main/java/com/example/ideaboard/auth/model/Comment.java`
- Fields:
  - `id`: Auto-generated primary key
  - `userStory`: Many-to-one relationship with UserStory
  - `author`: Name of the comment author
  - `content`: Comment text (max 2000 characters)
  - `createdAt`: Timestamp when comment was created
  - `updatedAt`: Timestamp when comment was last updated

### 2. Updated Model: UserStory
**File**: `backend/src/main/java/com/example/ideaboard/auth/model/UserStory.java`
- Added one-to-many relationship with Comments
- Added `@JsonIgnore` annotation to prevent circular references
- Cascade delete: Comments are deleted when a user story is deleted

### 3. New Repository: CommentRepository
**File**: `backend/src/main/java/com/example/ideaboard/auth/repository/CommentRepository.java`
- Custom query: `findByUserStoryIdOrderByCreatedAtDesc()` to get comments for a story

### 4. New Controller: CommentController
**File**: `backend/src/main/java/com/example/ideaboard/auth/controller/CommentController.java`
- **Endpoints**:
  - `GET /api/comments/story/{storyId}` - Get all comments for a specific user story
  - `POST /api/comments/story/{storyId}` - Add a new comment to a user story
  - `PUT /api/comments/{commentId}` - Update a comment
  - `DELETE /api/comments/{commentId}` - Delete a comment
  - `GET /api/comments` - Get all comments

### 5. New DTO: CommentDTO
**File**: `backend/src/main/java/com/example/ideaboard/auth/dto/CommentDTO.java`
- Data Transfer Object to avoid circular references when returning comments
- Contains all comment fields plus userStoryId

### 6. Updated Controller: UserStoryController
**File**: `backend/src/main/java/com/example/ideaboard/auth/controller/UserStoryController.java`
- Added CRUD endpoints:
  - `GET /api/userstories/{id}` - Get a specific user story
  - `PUT /api/userstories/{id}` - Update a user story
  - `DELETE /api/userstories/{id}` - Delete a user story

## API Usage Examples

### Add a Comment
```http
POST /api/comments/story/1
Content-Type: application/json

{
  "author": "John Doe",
  "content": "This looks good to me!"
}
```

### Get Comments for a Story
```http
GET /api/comments/story/1
```

### Update a Comment
```http
PUT /api/comments/5
Content-Type: application/json

{
  "content": "Updated comment text"
}
```

### Delete a Comment
```http
DELETE /api/comments/5
```

## Database Schema

### comments table
- `id` (BIGINT, Primary Key, Auto-increment)
- `user_story_id` (BIGINT, Foreign Key → user_stories.id)
- `author` (VARCHAR, Not Null)
- `content` (VARCHAR(2000), Not Null)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

## Next Steps for Frontend Integration
1. Create a comment component in the JavaFX UI
2. Add comment display section to user story details view
3. Implement add/edit/delete comment functionality in the UI
4. Connect UI to the backend API endpoints
