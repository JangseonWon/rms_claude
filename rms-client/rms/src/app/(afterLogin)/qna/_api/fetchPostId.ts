export async function fetchPostId(postId: String) {
    return await fetch(`/w-api/post-service/post/${postId}`, {
        method: 'PATCH',
        credentials: 'include',
        cache: 'no-store'
    });
}