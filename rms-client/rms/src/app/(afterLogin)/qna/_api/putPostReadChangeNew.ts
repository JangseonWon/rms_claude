export async function putPostReadChangeNew(post_id: number) {
    return await fetch(`/w-api/post-service/post/${post_id}/new`, {
        method: 'PUT',
        credentials: 'include',
        cache: 'no-store'
    });
}