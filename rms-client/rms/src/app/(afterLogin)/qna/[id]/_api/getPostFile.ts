export async function getPostFile(postFileId: string) {
    return await fetch(`/w-api/post-service/post-files/${postFileId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}