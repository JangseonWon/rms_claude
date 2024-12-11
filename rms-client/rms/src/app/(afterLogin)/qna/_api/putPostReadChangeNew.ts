export async function putPostReadChangeNew(post_id: number) {
    const res = await fetch(`/w-api/post-service/post/${post_id}/new`, {
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