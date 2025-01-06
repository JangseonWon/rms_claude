export async function putPostReadByUserId(post_id: number) {
    return await fetch(`/w-api/post-service/post/${post_id}`, {
        method: 'PUT',
        credentials: 'include',
        cache: 'no-store'
    });
}