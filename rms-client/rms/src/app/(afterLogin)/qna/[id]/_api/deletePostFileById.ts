export async function deletePostFileById(postFileId: string) {
    return await fetch(`/w-api/post-service/post-files/${postFileId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}