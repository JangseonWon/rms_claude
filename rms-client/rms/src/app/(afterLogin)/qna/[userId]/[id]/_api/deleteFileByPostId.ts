export async function deleteFileByPostId(postId: String) {
    return await fetch(`/w-api/post-service/post/${postId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}