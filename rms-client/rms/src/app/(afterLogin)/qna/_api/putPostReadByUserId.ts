export async function putPostReadByUserId(post_id: number) {
    const res = await fetch(`/w-api/post-service/post/${post_id}`, {
        method: 'PUT',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}