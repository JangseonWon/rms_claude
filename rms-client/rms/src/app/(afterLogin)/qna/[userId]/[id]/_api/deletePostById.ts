export async function deletePostById(postId: String) {
    return await fetch(`/w-api/post-service/posts/${postId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}