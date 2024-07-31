export async function deleteCommentById(postId: String, commentId: String) {
    return await fetch(`/w-api/post-service/post/${postId}/comment/${commentId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}