export async function deletePostById(postId: string) {
    return await fetch(`/w-api/post-service/post/${postId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}