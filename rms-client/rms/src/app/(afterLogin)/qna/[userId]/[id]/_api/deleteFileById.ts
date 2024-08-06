export async function deleteFileById(postId: String, fileId: String) {
    return await fetch(`/w-api/post-service/post/${postId}/file/${fileId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}