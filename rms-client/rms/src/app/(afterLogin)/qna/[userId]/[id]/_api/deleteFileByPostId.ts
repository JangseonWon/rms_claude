export async function deleteFileByPostId(postId: String) {
    return await fetch(`/w-api/post-service/post/${postId}/file`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}