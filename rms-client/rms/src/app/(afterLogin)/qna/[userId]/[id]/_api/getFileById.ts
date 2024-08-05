export async function getFileById(postId: String, fileId: String) {
    return await fetch(`/w-api/post-service/post/${postId}/file/${fileId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}